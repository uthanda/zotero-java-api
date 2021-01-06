package zotero.apiimpl;

import java.util.Date;

import zotero.api.Item;
import zotero.api.collections.Collections;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.ItemKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.collections.RelationshipsImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyEnumImpl;
import zotero.apiimpl.properties.PropertyRelationshipsImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.properties.PropertyTagsImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.schema.ZoteroSchema;
import zotero.apiimpl.rest.schema.ZoteroSchema.ZoteroType;

public class ItemImpl extends EntryImpl implements Item
{
	protected ItemImpl(LibraryImpl library)
	{
		super(library);
	}

	public void initialize(LibraryImpl library, ItemType type) throws ZoteroRuntimeException
	{
		ZoteroSchema schema = ZoteroSchema.getCurrentSchema();
		ZoteroType zoteroType = null;
		
		PropertiesImpl properties = (PropertiesImpl) getProperties();
		
		properties.addProperty(new PropertyEnumImpl<>(ItemKeys.ITEM_TYPE, ItemType.class, type));

		properties.addProperty(new PropertyRelationshipsImpl(new RelationshipsImpl(library)));
		
		properties.addProperty(new PropertyTagsImpl(new TagsImpl()));

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
			properties.addProperty(new PropertyStringImpl(key, null));
		}
	}

	@Override
	public final ItemType getItemType()
	{
		checkDeletionStatus();

		return (ItemType) getProperties().getProperty(ZoteroKeys.ItemKeys.ITEM_TYPE).getValue();
	}

	@Override
	public final Date getAccessDate()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.ItemKeys.ACCESS_DATE);
	}

	@Override
	public final Collections getCollections()
	{
		checkDeletionStatus();

		return (Collections) getProperties().getProperty(ZoteroKeys.ItemKeys.COLLECTIONS).getValue();
	}

	@Override
	public final Tags getTags()
	{
		checkDeletionStatus();

		return (Tags) getProperties().getProperty(ZoteroKeys.ItemKeys.TAGS).getValue();
	}

	public static Item fromRest(LibraryImpl library, ZoteroRestItem jsonItem)
	{
		String zoteroType = (String) jsonItem.getData().get(ZoteroKeys.ItemKeys.ITEM_TYPE);

		ItemType itemType = ItemType.fromZoteroType(zoteroType);
		
		ItemImpl item;
		
		switch (itemType)
		{
			case ATTACHMENT:
			{
				item = AttachmentImpl.fromRest(library, jsonItem);
				break;
			}
			case NOTE:
			{
				item = NoteImpl.fromRest(library, jsonItem);
				break;
			}
			default:
			{
				item = DocumentImpl.fromRest(library, jsonItem);
				break;
			}
		}

		// Refresh the properties
		item.refresh(jsonItem);

		EntryImpl.loadLinks(library, item, jsonItem.getLinks());

		return item;
	}

	@Override
	public void refresh()
	{
		// TODO
	}

	@Override
	public void save()
	{
		checkDeletionStatus();
		validate();

		if (getVersion() == null)
		{
			createItem();
		}
		else
		{
			updateItem();
		}
	}

	private void updateItem()
	{
		ZoteroRestItem item = buildRestItem(SerializationMode.PARTIAL);
		item.setVersion(getVersion());

		super.executeUpdate(ZoteroRest.Items.SPECIFIC, URLParameter.ITEM_KEY, this.getKey(), item);
	}

	private void createItem()
	{
		ZoteroRestItem item = buildRestItem(SerializationMode.FULL);

		item = executeCreate(ZoteroRest.Items.ALL, item);

		this.refresh(item);
	}

	public void validate()
	{
		// Content and property validation goes here
	}

	private ZoteroRestItem buildRestItem(SerializationMode mode)
	{
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, getProperties(), mode);

		ZoteroRestItem item = new ZoteroRestItem();
		item.setKey(getKey());
		item.setData(data);
		
		if (getVersion() != null)
		{
			item.setVersion(getVersion());
		}

		return item;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Relationships getRelationships()
	{
		return ((PropertyObject<Relationships>) super.getProperties().getProperty(ZoteroKeys.ItemKeys.RELATIONS)).getValue();
	}

	public void reinitialize(ItemType type)
	{
		throw new UnsupportedOperationException("reinitialize");
	}
}