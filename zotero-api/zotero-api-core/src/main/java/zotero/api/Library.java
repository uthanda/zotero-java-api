package zotero.api;

import zotero.api.constants.ItemType;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.LibraryImpl;

@SuppressWarnings({ "squid:S1610" })
public abstract class Library
{
	public abstract String getUserId();
	
	public abstract Collection createCollection(Collection parent);

	public abstract Collection fetchCollection(String key);

	public abstract CollectionIterator fetchCollectionsAll();

	public abstract CollectionIterator fetchCollectionsTop();

	public abstract ItemIterator fetchCollectionItems(String key);

	public abstract ItemIterator fetchCollectionItemsTop(String key);

	public abstract Item fetchItem(String key);

	public abstract Item createItem(ItemType type);

	public abstract ItemIterator fetchItemsAll();

	public abstract ItemIterator fetchItemsTop();

	public abstract ItemIterator fetchItemsTrash();
	
	public abstract ItemSearch createItemSearch();
	
	public abstract CollectionSearch createCollectionSearch();

	public static final Library createLibrary(String userId, ZoteroAPIKey apiKey)
	{
		return LibraryImpl.create(userId, apiKey);
	}
}