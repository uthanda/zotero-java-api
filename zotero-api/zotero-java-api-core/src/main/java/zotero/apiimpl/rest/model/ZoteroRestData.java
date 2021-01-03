package zotero.apiimpl.rest.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import zotero.api.Creator;
import zotero.api.Tag;
import zotero.api.collections.Creators;
import zotero.api.collections.Tags;
import zotero.api.constants.ZoteroEnum;
import zotero.api.constants.ZoteroKeys.Document;
import zotero.api.constants.ZoteroKeys.Item;
import zotero.api.properties.Property;
import zotero.api.properties.PropertyList;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.properties.PropertyImpl;

public class ZoteroRestData extends HashMap<String, Object>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5499856126066911646L;

	public static class DataBuilder
	{
		private ZoteroRestData data;
		private boolean delta;

		public DataBuilder(boolean delta, ZoteroRestData data)
		{
			this.delta = delta;
			this.data = data;
		}

		public DataBuilder addProperty(Property<?> prop)
		{
			PropertyImpl<?> property = (PropertyImpl<?>) prop;
			
			if (prop == null || (delta && !property.isDirty()))
			{
				return this;
			}

			switch (property.getPropertyType())
			{
				case BOOLEAN:
				case INTEGER:
				case STRING:
				{
					data.put(property.getKey(), property.getValue());
					break;
				}
				case DATE:
				{
					if (property.getValue() == null)
					{
						data.put(property.getKey(), null);
					}
					else
					{
						Calendar calendar = Calendar.getInstance();
						calendar.setTime((Date) property.getValue());
						data.put(property.getKey(), DatatypeConverter.printDateTime(calendar));
					}
					break;
				}
				case ENUM:
				{
					Enum<?> value = (Enum<?>) property.getValue();

					if (value instanceof ZoteroEnum)
					{
						data.put(property.getKey(), ((ZoteroEnum) value).getZoteroName());
					}
					else
					{
						data.put(property.getKey(), value == null ? null : value.toString());
					}

					break;
				}
				case LIST:
				{
					Class<?> type = ((PropertyList<?, ?>) property).getType();

					if (type == Creator.class)
					{
						@SuppressWarnings("unchecked")
						Creators list = ((PropertyList<Creator,Creators>)property).getValue();
						data.put(Document.CREATORS, list.stream().map(CreatorImpl::toRest).collect(Collectors.toList()));
					}
					else if (type == Tag.class)
					{
						@SuppressWarnings("unchecked")
						Tags list = ((PropertyList<Tag,Tags>)property).getValue();
						data.put(Item.TAGS, TagsImpl.toRest(list));
					}

					break;
				}
				case OBJECT:
					break;
				default:
					break;

			}

			return this;
		}
	}
}
