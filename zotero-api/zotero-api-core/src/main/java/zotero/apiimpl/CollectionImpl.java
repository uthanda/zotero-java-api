package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

@SuppressWarnings({ "squid:S2160" })
final class CollectionImpl extends EntryImpl implements Collection
{
	static final String URI_COLLECTIONS_ALL = "/collections";
	private static final String URI_COLLECTIONS_SUBCOLLECTIONS = "/collections/{key}/collections";
	static final String URI_COLLECTIONS_TOP = "/collections/top";
	static final String URI_COLLECTION = "/collections/{key}";
	static final String URI_COLLECTION_ITEMS = "/collections/{key}/items";
	static final String URI_COLLECTION_ITEMS_TOP = "/collections/{key}/items/top";

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
		return ((LibraryImpl) getLibrary()).fetchCollections(URI_COLLECTIONS_SUBCOLLECTIONS, this.getKey());
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