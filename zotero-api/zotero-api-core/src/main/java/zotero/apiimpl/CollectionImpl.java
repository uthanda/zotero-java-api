package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

@SuppressWarnings({ "squid:S2160" })
final class CollectionImpl extends EntryImpl implements Collection
{
	private int numItems;
	private int numCollections;

	private CollectionImpl(ZoteroRestItem item, Library library)
	{
		super(item, library);
		this.numCollections = ((Double) item.getMeta().get("numCollections")).intValue();
		this.numItems = ((Double) item.getMeta().get("numItems")).intValue();
	}

	@Override
	public ItemIterator fetchItems()
	{
		return getLibrary().fetchCollectionItems(this.getKey());
	}

	static CollectionImpl fromItem(ZoteroRestItem item, Library library)
	{
		return new CollectionImpl(item, library);
	}

	@Override
	public void save()
	{
	}

	@Override
	public void delete()
	{
	}

	@Override
	public void refresh()
	{
	}

	@Override
	public CollectionIterator fetchSubCollections()
	{
		return ((LibraryImpl) getLibrary()).fetchCollections(ZoteroRestPaths.COLLECTIONS_SUBCOLLECTIONS, this.getKey());
	}

	@Override
	public String getName()
	{
		return getProperties().getString(ZoteroKeys.NAME);
	}

	@Override
	public int getNumberOfCollections()
	{
		return numCollections;
	}

	@Override
	public int getNumberOfItems()
	{
		return numItems;
	}

	@Override
	public Collection fetchParentCollection()
	{
		String parentCollectionKey = getProperties().getString(ZoteroKeys.PARENT_COLLECTION);

		if (parentCollectionKey == null)
		{
			return null;
		}

		return ((LibraryImpl) getLibrary()).fetchCollection(parentCollectionKey);
	}
}