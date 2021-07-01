package zotero.apiimpl.properties;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.Library;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.LinkType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.AttachmentKeys;
import zotero.api.constants.ZoteroKeys.DocumentKeys;
import zotero.api.constants.ZoteroKeys.ItemKeys;
import zotero.api.constants.ZoteroKeys.LinkKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.api.properties.Property;
import zotero.api.properties.PropertyDate;
import zotero.api.properties.PropertyInteger;
import zotero.api.properties.PropertyLong;
import zotero.api.properties.PropertyString;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.schema.ZoteroSchema;

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
	public Long getLong(String key)
	{
		return ((PropertyLong) getProperty(key)).getValue();
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
	public void putValue(String key, Long value)
	{
		((PropertyLong) getProperty(key)).setValue(value);
	}

	@Override
	public Library getLibrary()
	{
		return library;
	}

	@Override
	public String toString()
	{
		return String.format("[Properties props:%s]", properties.toString());
	}

	public void fromRest(LibraryImpl library, Map<String, Object> item)
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();

		logger.debug("Starting to deserialize {}", item);

		properties.clear();

		for (Map.Entry<String, Object> e : item.entrySet())
		{
			String name = e.getKey();
			Object value = e.getValue();

			logger.debug("Processing {} of type {} and value {}", name, value != null ? value.getClass().getCanonicalName() : "?", value);

			Property<?> property = null;

			// Deal with known properties
			switch (name)
			{
				case DocumentKeys.CREATORS:
				{
					logger.debug("Processing PropertyCreatorImpl for " + DocumentKeys.CREATORS);
					property = PropertyCreatorsImpl.fromRest(library, value);
					break;
				}
				case ItemKeys.TAGS:
				{
					logger.debug("Processing PropertyCreatorImpl for " + ItemKeys.TAGS);
					property = PropertyTagsImpl.fromRest(library, value);
					break;
				}
				case ItemKeys.COLLECTIONS:
				{
					logger.debug("Processing PropertyCreatorImpl for " + ItemKeys.COLLECTIONS);
					property = PropertyCollectionsImpl.fromRest(library, value);
					break;
				}
				case ItemKeys.RELATIONS:
				{
					logger.debug("Processing PropertyCreatorImpl for " + ItemKeys.RELATIONS);
					property = PropertyRelationshipsImpl.fromRest(library, value);
					break;
				}
				default:
				{
					property = createSimpleProperty(schema, e, name, value);
				}
			}

			properties.put(name, property);
		}
	}

	@SuppressWarnings("squid:S1452")
	protected static Property<?> createSimpleProperty(ZoteroSchema schema, Map.Entry<String, Object> e, String name, Object value)
	{
		Property<?> property;

		if (value instanceof String && schema.getDateKeys().contains(name))
		{
			property = PropertyDateImpl.fromRest(name, value);
		}
		else if (ItemKeys.ITEM_TYPE.equals(e.getKey()))
		{
			property = new PropertyEnumImpl<>(e.getKey(), ItemType.class, ItemType.fromZoteroType((String) e.getValue()));
		}
		else if (AttachmentKeys.LINK_MODE.equals(e.getKey()))
		{
			property = new PropertyEnumImpl<>(e.getKey(), LinkMode.class, LinkMode.fromZoteroType((String) e.getValue()));
		}
		else if (AttachmentKeys.MTIME.equals(e.getKey()))
		{
			property = new PropertyLongImpl(e.getKey(), value != null ? ((Double) value).longValue() : null, false);
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

	@SuppressWarnings({ "squid:S1199" })
	public static void toRest(ZoteroRestData data, Properties properties, SerializationMode mode)
	{
		PropertiesImpl props = (PropertiesImpl) properties;

		for (Map.Entry<String, Property<?>> e : props.properties.entrySet())
		{
			String key = e.getKey();
			PropertyImpl<?> prop = (PropertyImpl<?>) e.getValue();

			// In Delta mode, we only add the changed properties
			if (mode == SerializationMode.PARTIAL && !prop.isDirty())
			{
				continue;
			}

			data.put(key, prop.toRestValue());
		}
	}

	@Override
	public Iterator<Property<?>> iterator()
	{
		return new Iterator<Property<?>>()
		{
			private Iterator<Property<?>> i = properties.values().iterator();

			@Override
			public boolean hasNext()
			{
				return i.hasNext();
			}

			@Override
			public Property<?> next()
			{
				return i.next();
			}
		};
	}

	@Override
	public int hashCode()
	{
		HashCodeBuilder builder = new HashCodeBuilder();

		for (Property<?> prop : properties.values())
		{
			builder.append(prop.getValue());
		}

		return builder.hashCode();
	}
}