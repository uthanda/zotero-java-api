package zotero.apiimpl.properties;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.Creator;
import zotero.api.Relationships;
import zotero.api.Tag;
import zotero.api.collections.CreatorsList;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.model.ZoteroRestCreator;
import zotero.api.internal.rest.model.ZoteroRestData;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.internal.rest.schema.ZoteroSchema;
import zotero.api.internal.rest.schema.ZoteroSchema.ZoteroType;
import zotero.api.properties.Properties;
import zotero.api.properties.Property;
import zotero.api.properties.PropertyDate;
import zotero.api.properties.PropertyInteger;
import zotero.api.properties.PropertyString;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.RelationshipsImpl;
import zotero.apiimpl.TagImpl;
import zotero.apiimpl.collections.CreatorsListImpl;

public final class PropertiesImpl implements Properties
{
	private static final Logger logger = LogManager.getLogger(PropertiesImpl.class);

	private Map<String, Property> properties = new HashMap<>();

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
	public Property getProperty(String key)
	{
		return properties.get(key);
	}

	@SuppressWarnings("unchecked")
	public static Properties from(ZoteroRestItem jsonItem)
	{
		return from(jsonItem.getData());
	}

	@SuppressWarnings("unchecked")
	public static Properties from(Map<String, Object> values)
	{
		PropertiesImpl properties = new PropertiesImpl();
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();

		for (Map.Entry<String, Object> e : values.entrySet())
		{
			String name = e.getKey();
			Object value = e.getValue();

			logger.debug("Processing {} of type {} and value {}", name, value.getClass().getCanonicalName(), value);

			Property property = null;

			if (value instanceof List)
			{
				List<?> listValue = (List<?>) value;

				switch (e.getKey())
				{
					case ZoteroKeys.CREATORS:
					{
						// Convert to a list of Creators
						List<Creator> list = listValue.stream().map(CreatorImpl::fromMap).collect(Collectors.toList());
						property = new PropertyListImpl<Creator>(ZoteroKeys.CREATORS, Creator.class, list);
						break;
					}
					case ZoteroKeys.TAGS:
					{
						List<Map<String, Object>> jsonTags = (List<Map<String, Object>>) value;

						List<Tag> tags = jsonTags.stream().map(TagImpl::fromMap).collect(Collectors.toList());

						property = new PropertyListImpl<Tag>(ZoteroKeys.TAGS, Tag.class, tags);
						break;
					}
					case ZoteroKeys.COLLECTIONS:
					{
						property = new PropertyListImpl<String>(ZoteroKeys.COLLECTIONS, String.class, (List<String>) listValue);
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
						property = new PropertyObjectImpl<Relationships>(ZoteroKeys.RELATIONS, Relationships.class, relationships);
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
				property = new PropertyDateImpl(name, DatatypeConverter.parseDateTime((String) value).getTime());
			}
			else if (value instanceof Double)
			{
				property = new PropertyIntegerImpl(name, ((Double) value).intValue());
			}
			else
			{
				property = new PropertyStringImpl(name, (String) value);
			}

			properties.properties.put(name, property);
		}

		return properties;

	}

	public static void gatherProperties(ZoteroRestData data, Properties properties)
	{
		PropertiesImpl props = (PropertiesImpl) properties;

		for (Map.Entry<String, Property> e : props.properties.entrySet())
		{
			String key = e.getKey();
			Property prop = e.getValue();

			switch (key)
			{
				case ZoteroKeys.CREATORS:
				{
					CreatorsList creators = (CreatorsList) value;
					if (value == null)
					{
						continue;
					}

					List<ZoteroRestCreator> zrcs = creators.stream().map(CreatorImpl::to).collect(Collectors.toList());
					data.put(ZoteroKeys.CREATORS, zrcs);
					break;
				}
				case ZoteroKeys.TAGS:
				{
					data.put(ZoteroKeys.TAGS, value);
					break;
				}
				case ZoteroKeys.COLLECTIONS:
				{
					data.put(ZoteroKeys.COLLECTIONS, value);
					break;
				}
				case ZoteroKeys.RELATIONS:
				{
					data.put(ZoteroKeys.RELATIONS, value);
					break;
				}
				default:
				{
					data.put(key, value);
				}
			}
		}
	}

	public static Properties initialize(ItemType type)
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
		ZoteroType zoteroType = null;

		for (ZoteroType itemType : schema.getTypes())
		{
			if (itemType.getId().equals(type.getZoteroType()))
			{
				zoteroType = itemType;
				break;
			}
		}

		if (zoteroType == null)
		{
			throw new RuntimeException("Invalid type " + type.name());
		}

		PropertiesImpl properties = new PropertiesImpl();

		for (String key : zoteroType.getFields().keySet())
		{
			properties.properties.put(key, null);
			properties.changedValues.put(key, null);
		}

		properties.properties.put(ZoteroKeys.CREATORS, new CreatorsListImpl());

		return properties;
	}

	@Override
	public Set<String> getPropertyNames()
	{
		Set<String> set = new HashSet<>();
		set.addAll(this.properties.keySet());
		set.addAll(this.changedValues.keySet());
		return set;
	}
}