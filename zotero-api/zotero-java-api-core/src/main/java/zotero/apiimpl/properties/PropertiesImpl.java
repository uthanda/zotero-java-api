package zotero.apiimpl.properties;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.Creator;
import zotero.api.Tag;
import zotero.api.collections.Collections;
import zotero.api.collections.Creators;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
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
import zotero.api.properties.PropertyList;
import zotero.api.properties.PropertyObject;
import zotero.api.properties.PropertyString;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CollectionsImpl;
import zotero.apiimpl.collections.CreatorsImpl;
import zotero.apiimpl.collections.RelationshipsImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestData.DataBuilder;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.schema.ZoteroSchema;
import zotero.apiimpl.rest.schema.ZoteroSchema.ZoteroType;

public final class PropertiesImpl implements Properties
{
	private static final Logger logger = LogManager.getLogger(PropertiesImpl.class);

	private Map<String, Property<?>> properties = new HashMap<>();

	@Override
	public String getString(String key)
	{
		return ((PropertyString) properties.get(key)).getValue();
	}

	@Override
	public Integer getInteger(String key)
	{
		return ((PropertyInteger) properties.get(key)).getValue();
	}

	@Override
	public Date getDate(String key)
	{
		return ((PropertyDate) properties.get(key)).getValue();
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

	@SuppressWarnings({"unchecked","squid:S1199"})
	public static PropertiesImpl fromRest(LibraryImpl library, ZoteroRestItem item)
	{
		PropertiesImpl properties = new PropertiesImpl();
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
					// Convert to a list of Creators
					property = new PropertyListImpl<>(Document.CREATORS, Creator.class, CreatorsImpl.fromRest(value));
					break;
				}
				case Item.TAGS:
				{
					Tags tags = TagsImpl.fromRest(library, value);

					property = new PropertyListImpl<>(Item.TAGS, Tag.class, tags);
					break;
				}
				case Item.COLLECTIONS:
				{
					CollectionsImpl collections = CollectionsImpl.fromRest(library, (List<String>) value);
					property = new PropertyObjectImpl<>(Item.COLLECTIONS, CollectionsImpl.class, collections);
					break;
				}
				case Item.RELATIONS:
				{
					RelationshipsImpl relationships = RelationshipsImpl.fromMap((Map<String, Object>) value);
					property = new PropertyObjectImpl<>(Item.RELATIONS, RelationshipsImpl.class, relationships);
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
			logger.debug("Attempting to deserialize '{}' as date", value);

			Date dateValue = ((String) value).isEmpty() ? null : DatatypeConverter.parseDateTime((String) value).getTime();

			property = new PropertyDateImpl(name, dateValue);
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

	@SuppressWarnings({"unchecked","squid:S1199"})
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

			if (prop.getValue() == null)
			{
				// TODO Do we need to handle special values such as creators or
				// tags in a special way?
				data.put(key, false);
			}

			switch (key)
			{
				case Document.CREATORS:
				{
					Creators creators = ((PropertyObject<Creators>) prop).getValue();
					data.put(Document.CREATORS, CreatorsImpl.toRest(creators));
					break;
				}
				case Item.TAGS:
				{
					Tags tags = ((PropertyList<Tag, Tags>) prop).getValue();
					data.put(Item.TAGS, TagsImpl.toRest(tags));
					break;
				}
				case Item.COLLECTIONS:
				{
					CollectionsImpl collections = (CollectionsImpl) ((PropertyObject<Collections>) prop).getValue();
					data.put(Item.COLLECTIONS, CollectionsImpl.toRest(collections));
					break;
				}
				case Item.RELATIONS:
				{
					Relationships relationships = ((PropertyObject<Relationships>) prop).getValue();
					data.put(Item.RELATIONS, RelationshipsImpl.toRest(relationships));
					break;
				}
				default:
				{
					data.put(key, prop.getValue());
				}
			}
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

		properties.properties.put(Document.CREATORS, new PropertyListImpl<>(Document.CREATORS, Creator.class, new CreatorsImpl()));
	}

	static void initializeItemProperties(ItemType type, PropertiesImpl properties, PropertiesImpl current)
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
		ZoteroType zoteroType = null;

		properties.properties.put(Item.ITEM_TYPE, new PropertyEnumImpl<>(Item.ITEM_TYPE, ItemType.class, type));

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

	public void addToBuilder(DataBuilder db)
	{
		for (Property<?> prop : properties.values())
		{
			db.addProperty(prop);
		}
	}

	@Override
	public String toString()
	{
		return String.format("[Properties props:%s]", properties.toString());
	}
}