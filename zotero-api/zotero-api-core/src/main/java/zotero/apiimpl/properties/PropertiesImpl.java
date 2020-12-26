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
import zotero.api.Relationships;
import zotero.api.collections.Creators;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.Attachment;
import zotero.api.constants.ZoteroKeys.Entity;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.api.properties.Property;
import zotero.api.properties.PropertyDate;
import zotero.api.properties.PropertyInteger;
import zotero.api.properties.PropertyList;
import zotero.api.properties.PropertyObject;
import zotero.api.properties.PropertyString;
import zotero.apiimpl.RelationshipsImpl;
import zotero.apiimpl.collections.CreatorsImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.model.ZoteroRestData.DataBuilder;
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
		return properties.get(key);
	}

	@SuppressWarnings("unchecked")
	public static PropertiesImpl fromRest(ZoteroRestItem item)
	{
		PropertiesImpl properties = new PropertiesImpl();
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();

		logger.debug("Starting to deserialize {}", item);

		for (Map.Entry<String, Object> e : item.getData().entrySet())
		{
			String name = e.getKey();
			Object value = e.getValue();

			logger.debug("Processing {} of type {} and value {}", name, value.getClass().getCanonicalName(), value);

			Property<?> property = null;

			if (value instanceof List)
			{
				List<?> listValue = (List<?>) value;

				switch (e.getKey())
				{
					case ZoteroKeys.CREATORS:
					{
						// Convert to a list of Creators
						property = new PropertyListImpl<>(ZoteroKeys.CREATORS, Creator.class, CreatorsImpl.fromRest(listValue));
						break;
					}
					case ZoteroKeys.TAGS:
					{
						List<Map<String, Object>> jsonTags = (List<Map<String, Object>>) value;

						Tags tags = TagsImpl.from(jsonTags);

						property = new PropertyListImpl<>(ZoteroKeys.TAGS, String.class, tags);
						break;
					}
					case ZoteroKeys.COLLECTIONS:
					{
						property = new PropertyListImpl<>(ZoteroKeys.COLLECTIONS, String.class, (List<String>) listValue);
						break;
					}
					default:
					{
						logger.error("Unknown List key {} for value {}", e.getKey(), e.getValue());
					}
				}
			}
			else if (value instanceof Map)
			{
				switch (e.getKey())
				{
					case ZoteroKeys.RELATIONS:
					{
						Relationships relationships = RelationshipsImpl.fromMap((Map<String, Object>) value);
						property = new PropertyObjectImpl<>(ZoteroKeys.RELATIONS, Relationships.class, relationships);
						break;
					}
					default:
					{
						logger.error("Unknown Map key {} for value {}", e.getKey(), e.getValue());
					}
				}
			}
			else if (value instanceof String && schema.getDateKeys().contains(name))
			{
				logger.debug("Attempting to deserialize '{}' as date", value);

				Date dateValue = ((String) value).isEmpty() ? null : DatatypeConverter.parseDateTime((String) value).getTime();

				property = new PropertyDateImpl(name, dateValue);
			}
			else if (ZoteroKeys.ITEM_TYPE.equals(e.getKey()))
			{
				property = new PropertyEnumImpl<>(e.getKey(), ItemType.class, ItemType.fromZoteroType((String) e.getValue()));
			}
			else if (value instanceof Double)
			{
				property = new PropertyIntegerImpl(name, ((Double) value).intValue());
			}
			else if (value instanceof Boolean)
			{
				property = new PropertyBooleanImpl(name, (Boolean) value);
			}
			else
			{
				property = new PropertyStringImpl(name, (String) value);
			}

			properties.properties.put(name, property);
		}

		return properties;

	}

	@SuppressWarnings("unchecked")
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

			switch (key)
			{
				case ZoteroKeys.CREATORS:
				{
					Creators creators = ((PropertyList<Creator, Creators>) prop).getValue();
					data.put(ZoteroKeys.CREATORS, CreatorsImpl.toRest(creators));
					break;
				}
				case ZoteroKeys.TAGS:
				{
					Tags tags = ((PropertyList<String, Tags>) prop).getValue();
					data.put(ZoteroKeys.TAGS, TagsImpl.toRest(tags));
					break;
				}
				case ZoteroKeys.COLLECTIONS:
				{
					List<String> collections = ((PropertyList<String, List<String>>) prop).getValue();
					data.put(ZoteroKeys.COLLECTIONS, collections);
					break;
				}
				case ZoteroKeys.RELATIONS:
				{
					Relationships relationships = ((PropertyObject<Relationships>) prop).getValue();
					data.put(ZoteroKeys.RELATIONS, RelationshipsImpl.toRest(relationships));
					break;
				}
				default:
				{
					data.put(key, prop != null ? prop.getValue() : null);
				}
			}
		}
	}

	public static void initializeCollectionProperties(PropertiesImpl properties)
	{

	}

	public static void initializeDocumentProperties(ItemType type, PropertiesImpl properties, PropertiesImpl current)
	{
		if (type == ItemType.ATTACHMENT)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE,
					"Cannot initalize an attachment using initalize document properties");
		}

		initializeItemProperties(type, properties, current);

		properties.properties.put(ZoteroKeys.CREATORS, new PropertyListImpl<>(ZoteroKeys.CREATORS, Creator.class, new CreatorsImpl()));
		properties.properties.put(ZoteroKeys.ITEM_TYPE, new PropertyEnumImpl<>(ZoteroKeys.ITEM_TYPE, ItemType.class, type));
	}

	static void initializeItemProperties(ItemType type, PropertiesImpl properties, PropertiesImpl current)
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
		ZoteroType zoteroType = null;

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

	public static void initializeAttachmentProperties(ItemType type, LinkMode mode, PropertiesImpl properties)
	{
		if (type != ItemType.ATTACHMENT)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE,
					"Cannot initalize an document using initalize attachment properties");
		}

		initializeItemProperties(type, properties, null);

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
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.UNSUPPORTED_ENUM_VALUE,
						"Cannot initalize an attachment of mode " + mode + ".  Mode not implemented.");
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
		if (!properties.containsKey(key))
		{
			properties.put(key, new PropertyStringImpl(key, value));
		}
		else
		{
			((PropertyString) properties.get(key)).setValue(value);
		}
	}

	public void addToBuilder(DataBuilder db)
	{
		for (Property<?> prop : properties.values())
		{
			db.addProperty(prop);
		}
	}
}