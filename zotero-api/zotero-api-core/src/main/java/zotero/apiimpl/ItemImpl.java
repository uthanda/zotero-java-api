package zotero.apiimpl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.Relationships;
import zotero.api.collections.Creators;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.builders.DeleteBuilder;
import zotero.api.internal.rest.builders.PatchBuilder;
import zotero.api.internal.rest.builders.PostBuilder;
import zotero.api.internal.rest.impl.ZoteroRestDeleteRequest;
import zotero.api.internal.rest.impl.ZoteroRestPatchRequest;
import zotero.api.internal.rest.impl.ZoteroRestPostRequest;
import zotero.api.internal.rest.model.ZoteroRestData;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.internal.rest.model.ZoteroRestMeta;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.properties.PropertiesImpl;

@SuppressWarnings({ "squid:S2160" })
public final class ItemImpl extends EntryImpl implements Item
{
	private ZoteroRestItem jsonItem;

	private int numChildren;

	private boolean deleted = false;

	private ItemImpl(ZoteroRestItem item, Library library)
	{
		super(item, library);
	}

	ItemImpl(ItemType type, Library library)
	{
		super(type, library);
	}

	@Override
	public String getTitle()
	{
		checkDeletionStatus();

		return super.getProperties().getString(ZoteroKeys.TITLE);
	}

	private void checkDeletionStatus()
	{
		if (deleted)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.OBJECT_DELETED, "Object was deleted");
		}
	}

	@Override
	public void setTitle(String title)
	{
		checkDeletionStatus();

		super.getProperties().putValue(ZoteroKeys.TITLE, title);
	}

	@Override
	public Creators getCreators()
	{
		checkDeletionStatus();

		return (Creators) super.getProperties().getProperty(ZoteroKeys.CREATORS).getValue();
	}

	@Override
	public Date getDateAdded()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.DATE_ADDED);
	}

	@Override
	public Date getDateModified()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.DATE_MODIFIED);
	}

	@Override
	public ItemType getItemType()
	{
		checkDeletionStatus();

		return (ItemType) getProperties().getProperty(ZoteroKeys.ITEM_TYPE).getValue();
	}

	@Override
	public void changeItemType(ItemType type)
	{
		checkDeletionStatus();

		super.reinitialize(type);
	}

	@Override
	public String getRights()
	{
		checkDeletionStatus();

		return getProperties().getString(ZoteroKeys.RIGHTS);
	}

	@Override
	public void setRights(String rights)
	{
		checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.RIGHTS, rights);
	}

	@Override
	public String getURL()
	{
		checkDeletionStatus();

		return getProperties().getString(ZoteroKeys.URL);
	}

	@Override
	public void setURL(String url)
	{
		checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.URL, url);
	}

	@Override
	public String getShortTitle()
	{
		checkDeletionStatus();

		return getProperties().getString(ZoteroKeys.SHORT_TITLE);
	}

	@Override
	public void setShortTitle(String shortTitle)
	{
		checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.SHORT_TITLE, shortTitle);
	}

	@Override
	public Date getAccessDate()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.ACCESS_DATE);
	}

	@Override
	public String getExtra()
	{
		checkDeletionStatus();

		return getProperties().getString(ZoteroKeys.EXTRA);
	}

	@Override
	public void setExtra(String extra)
	{
		checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.EXTRA, extra);
	}

	@Override
	public String getAbstractNote()
	{
		checkDeletionStatus();

		return getProperties().getString(ZoteroKeys.ABSTRACT_NOTE);
	}

	@Override
	public void setAbstractNote(String abstractNote)
	{
		checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.ABSTRACT_NOTE, abstractNote);
	}

	@Override
	public CollectionIterator getCollections()
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
	public Tags getTags()
	{
		checkDeletionStatus();

		return (Tags) getProperties().getProperty(ZoteroKeys.TAGS).getValue();
	}

	static Item fromItem(ZoteroRestItem jsonItem, Library library)
	{
		ItemImpl item = new ItemImpl(jsonItem, library);
		item.jsonItem = jsonItem;

		EntryImpl.loadLinks(item, jsonItem.getLinks());

		Double numChildren = (Double) jsonItem.getMeta().get(ZoteroRestMeta.NUM_CHILDREN);
		item.numChildren = numChildren == null ? 0 : numChildren.intValue();
		return item;
	}

	@Override
	public ItemIterator fetchChildren()
	{
		checkDeletionStatus();

		return ((LibraryImpl) getLibrary()).fetchItems(ZoteroRestPaths.ITEM_CHILDREN, this.getKey());
	}

	@Override
	public void refresh()
	{
		// TODO
	}

	@Override
	public final int getNumberOfChilden()
	{
		return numChildren;
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
			builder.content(item);

			libraryImpl.performPost((builder));
		}
		else
		{
			ZoteroRestItem item = buildRestItem(true);

			PatchBuilder builder = ZoteroRestPatchRequest.Builder.createBuilder();
			builder.content(item).itemKey(this.getKey());

			libraryImpl.performPatch((builder));
		}
	}

	private ZoteroRestItem buildRestItem(boolean deltaMode)
	{
		ZoteroRestItem item = new ZoteroRestItem();
		item.setKey(this.getKey());

		ZoteroRestData data = new ZoteroRestData();
		PropertiesImpl.gatherProperties(data, this.getProperties(), deltaMode);
		item.setData(data);
		return item;
	}

	@Override
	public void delete()
	{
		checkDeletionStatus();

		DeleteBuilder builder = ZoteroRestDeleteRequest.Builder.createBuilder();
		builder.itemKey(this.getKey());
		
		((LibraryImpl)getLibrary()).performDelete();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Relationships getRelationships()
	{
		return ((PropertyObject<Relationships>) super.getProperties().getProperty(ZoteroKeys.RELATIONS)).getValue();
	}
}