package zotero.apiimpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.attachments.AttachmentImpl;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.builders.PatchBuilder;
import zotero.apiimpl.rest.builders.PostBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestPatchRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPostRequest;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class ItemImpl extends EntryImpl implements Item
{
	private ZoteroRestItem jsonItem;

	protected ItemImpl(ZoteroRestItem item, Library library)
	{
		super(item, library);
	}

	protected ItemImpl(ItemType type, Library library)
	{
		super(type, library);
	}

	protected ItemImpl(LinkMode mode, Library library)
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
	public final CollectionIterator getCollections()
	{
		checkDeletionStatus();

		return new CollectionIterator()
		{
			@SuppressWarnings("unchecked")
			private List<String> set = (List<String>) getProperties().getProperty(ZoteroKeys.COLLECTIONS).getValue();
			private Iterator<String> i = set.iterator();

			@Override
			public boolean hasNext()
			{
				return i.hasNext();
			}

			@Override
			public Collection next()
			{
				return getLibrary().fetchCollection(i.next());
			}

			@Override
			public int getTotalCount()
			{
				return set.size();
			}
		};
	}

	@Override
	public final Tags getTags()
	{
		checkDeletionStatus();

		return (Tags) getProperties().getProperty(ZoteroKeys.TAGS).getValue();
	}

	public static Item fromItem(ZoteroRestItem jsonItem, Library library)
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

		// This is a new item, so we do a post
		LibraryImpl libraryImpl = (LibraryImpl) getLibrary();

		if (this.jsonItem == null)
		{
			ZoteroRestItem item = buildRestItem(false);

			PostBuilder builder = ZoteroRestPostRequest.Builder.createBuilder();
			builder.content(item).url(ZoteroRestPaths.ITEMS);

			libraryImpl.performPost((builder));
		}
		else
		{
			ZoteroRestItem item = buildRestItem(true);

			PatchBuilder builder = ZoteroRestPatchRequest.Builder.createBuilder();
			builder.content(item).itemKey(this.getKey()).url(ZoteroRestPaths.ITEM);

			libraryImpl.performPatch((builder));
		}
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