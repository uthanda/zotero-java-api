package zotero.apiimpl.properties;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.Library;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.Attachment;
import zotero.api.constants.ZoteroKeys.Document;
import zotero.api.constants.ZoteroKeys.Entity;
import zotero.api.constants.ZoteroKeys.Item;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.api.properties.Property;
import zotero.api.properties.PropertyDate;
import zotero.api.properties.PropertyInteger;
import zotero.api.properties.PropertyString;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CreatorsImpl;
import zotero.apiimpl.collections.RelationshipsImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.schema.ZoteroSchema;
import zotero.apiimpl.rest.schema.ZoteroSchema.ZoteroType;

public final class PropertiesImpl implements Properties
{
	private static final Logger logger = LogManager.getLogger(PropertiesImpl.class);

	private Map<String, Property<?>> properties = new HashMap<>();

	private final LibraryImpl library;

	public PropertiesImpl(LibraryImpl library)
	{
		this.library = library;
	}
	
	@Override
	public String getString(String key)
	{
		return ((PropertyString) getProperty(key)).getValue();
	}

	@Override
	public Integer getInteger(String key)
	{
		return ((PropertyInteger) getProperty(key)).getValue();
	}

	@Override
	public Date getDate(String key)
	{
		return ((PropertyDate) getProperty(key)).getValue();
	}

	@Override
	public Property<?> getProperty(String key)
	{
		if (!properties.containsKey(key))
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.INVALID_PROPERTY, "Property " + key + " invalid because it's not in the collection");
		}
		
		return properties.get(key);
	}

	@Override
	public Set<String> getPropertyNames()
	{
		Set<String> set = new HashSet<>();
		set.addAll(this.properties.keySet());
		return set;
	}

	public void addProperty(Property<?> property)
	{
		this.properties.put(property.getKey(), property);
	}

	@Override
	public void putValue(String key, String value)
	{
		((PropertyString) getProperty(key)).setValue(value);
	}

	@Override
	public void putValue(String key, Integer value)
	{
		((PropertyInteger) getProperty(key)).setValue(value);
	}

	@Override
	public String toString()
	{
		return String.format("[Properties props:%s]", properties.toString());
	}

	public static PropertiesImpl fromRest(LibraryImpl library, ZoteroRestItem item)
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
	
		logger.debug("Starting to deserialize {}", item);
	
		for (Map.Entry<String, Object> e : item.getData().entrySet())
		{
			String name = e.getKey();
			Object value = e.getValue();
	
			logger.debug("Processing {} of type {} and value {}", name, value != null ? value.getClass().getCanonicalName() : "?", value);
	
			Property<?> property = null;
	
			// Deal with known properties
			switch (e.getKey())
			{
				case Document.CREATORS:
				{
					property = PropertyCreatorsImpl.fromRest(library, value);
					break;
				}
				case Item.TAGS:
				{
					property = PropertyTagsImpl.fromRest(library, value);
					break;
				}
				case Item.COLLECTIONS:
				{
					property = PropertyCollectionsImpl.fromRest(library, value);
					break;
				}
				case Item.RELATIONS:
				{
					property = PropertyRelationshipsImpl.fromRest(library, value);
					break;
				}
				default:
				{
					property = createSimpleProperty(schema, e, name, value);
				}
			}
	
			properties.properties.put(name, property);
		}
	
		return properties;
	
	}

	@SuppressWarnings("squid:S1452")
	protected static Property<?> createSimpleProperty(ZoteroSchema schema, Map.Entry<String, Object> e, String name, Object value)
	{
		Property<?> property;
		if (value instanceof String && schema.getDateKeys().contains(name))
		{
			property = PropertyDateImpl.fromRest(name, value);
		}
		else if (Item.ITEM_TYPE.equals(e.getKey()))
		{
			property = new PropertyEnumImpl<>(e.getKey(), ItemType.class, ItemType.fromZoteroType((String) e.getValue()));
		}
		else if (Attachment.LINK_MODE.equals(e.getKey()))
		{
			property = new PropertyEnumImpl<>(e.getKey(), LinkMode.class, LinkMode.fromZoteroType((String) e.getValue()));
		}
		else if (value instanceof Double)
		{
			property = new PropertyIntegerImpl(name, ((Double) value).intValue());
		}
		else if (value instanceof Boolean)
		{
			// Need to think about whether this could happen
			property = new PropertyStringImpl(name, null);
		}
		else
		{
			property = new PropertyStringImpl(name, (String) value);
		}
		return property;
	}

	@SuppressWarnings({"squid:S1199"})
	public static void toRest(ZoteroRestData data, Properties properties, boolean deltaMode)
	{
		PropertiesImpl props = (PropertiesImpl) properties;
	
		for (Map.Entry<String, Property<?>> e : props.properties.entrySet())
		{
			String key = e.getKey();
			PropertyImpl<?> prop = (PropertyImpl<?>) e.getValue();
	
			// In Delta mode, we only add the changed properties
			if (deltaMode && !prop.isDirty())
			{
				continue;
			}
			
			data.put(key, prop.toRestValue());
		}
	}

	public static void initializeCollectionProperties(PropertiesImpl properties)
	{
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Collection.NAME, null));
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Collection.PARENT_COLLECTION, null));
	}

	public static void initializeDocumentProperties(ItemType type, PropertiesImpl properties, PropertiesImpl current)
	{
		if (type == ItemType.ATTACHMENT)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE, "Cannot initalize an attachment using initalize document properties");
		}
	
		initializeItemProperties(type, properties, current);
	
		properties.properties.put(Document.CREATORS, new PropertyCreatorsImpl(new CreatorsImpl(properties.library)));
	}

	static void initializeItemProperties(ItemType type, PropertiesImpl properties, PropertiesImpl current)
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
		ZoteroType zoteroType = null;
	
		properties.properties.put(Item.ITEM_TYPE, new PropertyEnumImpl<>(Item.ITEM_TYPE, ItemType.class, type));
		properties.properties.put(Item.RELATIONS, new PropertyRelationshipsImpl(new RelationshipsImpl(properties.library)));
		properties.properties.put(Item.TAGS, new PropertyTagsImpl(new TagsImpl()));
	
		for (ZoteroType itemType : schema.getTypes())
		{
			if (itemType.getId().equals(type.getZoteroName()))
			{
				zoteroType = itemType;
				break;
			}
		}
	
		if (zoteroType == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE, "Invalid type " + type.name());
		}
	
		for (String key : zoteroType.getFields().keySet())
		{
			Property<?> prop = current != null ? current.getProperty(key) : null;
	
			if (prop == null)
			{
				prop = new PropertyStringImpl(key, null);
			}
	
			properties.properties.put(key, prop);
		}
	}

	@SuppressWarnings({"squid:S1199"})
	public static void initializeAttachmentProperties(LinkMode mode, PropertiesImpl properties)
	{
		initializeItemProperties(ItemType.ATTACHMENT, properties, null);
		
		properties.addProperty(new PropertyEnumImpl<>(Attachment.LINK_MODE, LinkMode.class, mode));
		properties.addProperty(new PropertyStringImpl(Attachment.CHARSET, null));
		properties.addProperty(new PropertyStringImpl(Attachment.CONTENT_TYPE, null));
	
		switch (mode)
		{
			case IMPORTED_FILE:
			{
				properties.properties.put(Attachment.FILENAME, new PropertyStringImpl(Attachment.FILENAME, null));
				properties.properties.put(Attachment.MD5, new PropertyStringImpl(Attachment.MD5, null));
				properties.properties.put(Attachment.MTIME, new PropertyStringImpl(Attachment.MTIME, null));
				break;
			}
			case IMPORTED_URL:
			{
				properties.properties.put(Entity.URL, new PropertyStringImpl(Entity.URL, null));
				properties.properties.put(Attachment.FILENAME, new PropertyStringImpl(Attachment.FILENAME, null));
				properties.properties.put(Attachment.MD5, new PropertyStringImpl(Attachment.MD5, null));
				properties.properties.put(Attachment.MTIME, new PropertyStringImpl(Attachment.MTIME, null));
				break;
			}
			case LINKED_FILE:
			{
				properties.properties.put(Attachment.PATH, new PropertyStringImpl(Attachment.PATH, null));
				break;
			}
			case LINKED_URL:
			{
				properties.properties.put(Entity.URL, new PropertyStringImpl(Entity.URL, null));
				break;
			}
			default:
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE, "Cannot initalize an attachment of mode " + mode + ".  Mode not implemented.");
			}
	
		}
	}

	public static void initializeNoteProperties(PropertiesImpl properties, PropertiesImpl current)
	{
		initializeItemProperties(ItemType.NOTE, properties, current);
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Item.NOTE, null));
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Attachment.PARENT_ITEM, null));
	}

	@Override
	public Library getLibrary()
	{
		return library;
	}
}