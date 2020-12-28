package zotero.apiimpl;

import java.util.Date;

import zotero.api.Item;
import zotero.api.collections.Collections;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroKeys;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.PatchBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

public class ItemImpl extends EntryImpl implements Item
{
	private ZoteroRestItem jsonItem;

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

		return super.getProperties().getString(ZoteroKeys.TITLE);
	}

	@Override
	public final void setTitle(String title)
	{
		checkDeletionStatus();

		super.getProperties().putValue(ZoteroKeys.TITLE, title);
	}

	@Override
	public final ItemType getItemType()
	{
		checkDeletionStatus();

		return (ItemType) getProperties().getProperty(ZoteroKeys.ITEM_TYPE).getValue();
	}

	@Override
	public final Date getAccessDate()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.ACCESS_DATE);
	}

	@Override
	public final Collections getCollections()
	{
		checkDeletionStatus();

		return (Collections) getProperties().getProperty(ZoteroKeys.COLLECTIONS).getValue();
	}

	@Override
	public final Tags getTags()
	{
		checkDeletionStatus();

		return (Tags) getProperties().getProperty(ZoteroKeys.TAGS).getValue();
	}

	public static Item fromItem(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		ItemImpl item;
		if (jsonItem.getData().get(ZoteroKeys.ITEM_TYPE).equals(ItemType.ATTACHMENT.getZoteroName()))
		{
			item = AttachmentImpl.fromRest(jsonItem, library);
		}
		else
		{
			item = DocumentImpl.fromRest(jsonItem, library);
		}

		item.jsonItem = jsonItem;

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

		// This is a new item, so we do a post
		LibraryImpl libraryImpl = (LibraryImpl) getLibrary();

		if (this.jsonItem == null)
		{
			ZoteroRestItem item = buildRestItem(false);

			PostBuilder<?,?> builder = PostBuilder.createBuilder(new SuccessResponseBuilder());
			builder.jsonObject(item).url(ZoteroRestPaths.ITEMS);

			libraryImpl.performRequest((builder));
		}
		else
		{
			ZoteroRestItem item = buildRestItem(true);

			PatchBuilder<?,?> builder = PatchBuilder.createBuilder(new SuccessResponseBuilder());
			builder.jsonObject(item).itemKey(this.getKey()).url(ZoteroRestPaths.ITEM);

			libraryImpl.performRequest((builder));
		}
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

		return ib.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Relationships getRelationships()
	{
		return ((PropertyObject<Relationships>) super.getProperties().getProperty(ZoteroKeys.RELATIONS)).getValue();
	}

	@Override
	String getDeletePath()
	{
		return ZoteroRestPaths.ITEM;
	}
}