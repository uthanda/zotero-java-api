package zotero.apiimpl;

import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;

import java.util.Date;

import zotero.api.Item;
import zotero.api.collections.Collections;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class ItemImpl extends EntryImpl implements Item
{
	protected ItemImpl(ZoteroRestItem item, LibraryImpl library)
	{
		super(item, library);
	}

	protected ItemImpl(ItemType type, LibraryImpl library)
	{
		super(type, library);
	}

	protected ItemImpl(LinkMode mode, LibraryImpl library)
	{
		super(mode, library);
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

	public static Item fromItem(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		ItemImpl item;
		String zoteroType = (String) jsonItem.getData().get(ZoteroKeys.ItemKeys.ITEM_TYPE);

		ItemType itemType = ItemType.fromZoteroType(zoteroType);
		
		switch (itemType)
		{
			case ATTACHMENT:
			{
				item = AttachmentImpl.fromRest(jsonItem, library);
				break;
			}
			case NOTE:
			{
				item = NoteImpl.fromRest(jsonItem, library);
				break;
			}
			default:
			{
				item = DocumentImpl.fromRest(jsonItem, library);
				break;
			}
		}

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
}