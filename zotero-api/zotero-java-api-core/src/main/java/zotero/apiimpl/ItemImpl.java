package zotero.apiimpl;

import zotero.api.constants.ZoteroKeys;
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
import zotero.apiimpl.properties.PropertiesImpl;
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
	public final String getTitle()
	{
		checkDeletionStatus();

		return super.getProperties().getString(ZoteroKeys.Item.TITLE);
	}

	@Override
	public final void setTitle(String title)
	{
		checkDeletionStatus();

		super.getProperties().putValue(ZoteroKeys.Item.TITLE, title);
	}

	@Override
	public final ItemType getItemType()
	{
		checkDeletionStatus();

		return (ItemType) getProperties().getProperty(ZoteroKeys.Item.ITEM_TYPE).getValue();
	}

	@Override
	public final Date getAccessDate()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.Item.ACCESS_DATE);
	}

	@Override
	public final Collections getCollections()
	{
		checkDeletionStatus();

		return (Collections) getProperties().getProperty(ZoteroKeys.Item.COLLECTIONS).getValue();
	}

	@Override
	public final Tags getTags()
	{
		checkDeletionStatus();

		return (Tags) getProperties().getProperty(ZoteroKeys.Item.TAGS).getValue();
	}

	public static Item fromItem(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		ItemImpl item;
		if (jsonItem.getData().get(ZoteroKeys.Item.ITEM_TYPE).equals(ItemType.ATTACHMENT.getZoteroName()))
		{
			item = AttachmentImpl.fromRest(jsonItem, library);
		}
		else
		{
			item = DocumentImpl.fromRest(jsonItem, library);
		}

		EntryImpl.loadLinks(item, jsonItem.getLinks());

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
		ZoteroRestItem item = buildRestItem(true);
		
		super.executeUpdate(ZoteroRest.Items.SPECIFIC, URLParameter.ITEM_KEY, this.getKey(), item);
	}

	private void createItem()
	{
		ZoteroRestItem item = buildRestItem(false);

		item = executeCreate(ZoteroRest.Items.ALL, item);
		
		this.refresh(item);
	}

	protected void refresh(ZoteroRestItem refresh)
	{
		super.refresh(refresh);
	}

	public void validate()
	{
		// Content and property validation goes here
	}

	private ZoteroRestItem buildRestItem(boolean deltaMode)
	{
		ZoteroRestItem.ItemBuilder ib = new ZoteroRestItem.ItemBuilder(deltaMode);

		ib.key(getKey());

		ZoteroRestData.DataBuilder db = ib.dataBuilder();

		((PropertiesImpl) getProperties()).addToBuilder(db);

		if(getVersion() != null) {
			ib.version(getVersion());
		}
		
		return ib.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Relationships getRelationships()
	{
		return ((PropertyObject<Relationships>) super.getProperties().getProperty(ZoteroKeys.Item.RELATIONS)).getValue();
	}
}