package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.ItemType;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.iterators.CollectionIteratorImpl;
import zotero.apiimpl.iterators.ZoteroItemIteratorImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.builders.Builder;
import zotero.apiimpl.rest.builders.DeleteBuilder;
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.builders.PatchBuilder;
import zotero.apiimpl.rest.builders.PostBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.search.CollectionSearchImpl;
import zotero.apiimpl.search.ItemSearchImpl;

public final class LibraryImpl extends Library
{
	private final ZoteroAPIKey apiKey;
	private final String id;

	public static final Library create(String userId, ZoteroAPIKey apiKey)
	{
		return new LibraryImpl(apiKey, userId);
	}

	LibraryImpl(ZoteroAPIKey apiKey, String id)
	{
		this.apiKey = apiKey;
		this.id = id;
	}

	@Override
	public String getUserId()
	{
		return id;
	}

	@Override
	public Collection fetchCollection(String key)
	{
		GetBuilder<ZoteroRestItem> req = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem.class);

		req.url(ZoteroRestPaths.COLLECTION).urlParam("key", key);

		RestResponse<ZoteroRestItem> response = performGet(req);

		return CollectionImpl.fromItem(response.getResponse(), this);
	}

	@Override
	public Collection createCollection(Collection parent)
	{
		return CollectionImpl.create(this, parent);
	}

	@Override
	public ItemIterator fetchCollectionItems(String key)
	{
		return fetchItems(ZoteroRestPaths.COLLECTION_ITEMS, key);
	}

	@Override
	public ItemIterator fetchCollectionItemsTop(String key)
	{
		return fetchItems(ZoteroRestPaths.COLLECTION_ITEMS_TOP, key);
	}

	@Override
	public CollectionIterator fetchCollectionsAll()
	{
		return fetchCollections(ZoteroRestPaths.COLLECTIONS, null);
	}

	@Override
	public CollectionIterator fetchCollectionsTop()
	{
		return fetchCollections(ZoteroRestPaths.COLLECTIONS_TOP, null);
	}

	CollectionIterator fetchCollections(String url, String key)
	{
		GetBuilder<ZoteroRestItem[]> builder = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem[].class);
		builder.url(url);

		if (key != null)
		{
			builder.urlParam("key", key);
		}

		RestResponse<ZoteroRestItem[]> response = performGet(builder);

		return new CollectionIteratorImpl(response, this);
	}

	@Override
	public ItemIterator fetchItemsAll()
	{
		return fetchItems(ZoteroRestPaths.ITEMS_ALL, null);
	}

	@Override
	public ItemIterator fetchItemsTop()
	{
		return fetchItems(ZoteroRestPaths.ITEMS_TOP, null);
	}

	@Override
	public ItemIterator fetchItemsTrash()
	{
		return fetchItems(ZoteroRestPaths.ITEMS_TRASH, null);
	}

	@Override
	public Item fetchItem(String key)
	{
		GetBuilder<ZoteroRestItem> builder = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem.class);

		builder.url(ZoteroRestPaths.ITEM).urlParam("key", key);

		RestResponse<ZoteroRestItem> response = performGet(builder);

		return ItemImpl.fromItem(response.getResponse(), this);
	}

	ItemIterator fetchItems(String url, String key)
	{
		GetBuilder<ZoteroRestItem[]> builder = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem[].class);
		builder.url(url);

		if (key != null)
		{
			builder.urlParam("key", key);
		}

		RestResponse<ZoteroRestItem[]> response = performGet(builder);

		return new ZoteroItemIteratorImpl(response, this);
	}

	@Override
	public Item createItem(ItemType type)
	{
		return new ItemImpl(type, this);
	}

	public <T> RestResponse<T> performGet(GetBuilder<T> builder)
	{
		RestResponse<T> resp = builder.apiKey(apiKey).id(id).setUsers().build().execute();

		if (resp.wasSuccessful())
		{
			return resp;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	public void performPost(PostBuilder builder)
	{
		RestResponse<Boolean> resp = applyCommon(builder).build().execute();

		if (resp.wasSuccessful())
		{
			return;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	private <T extends Builder<T>> T applyCommon(T builder)
	{
		return builder.apiKey(apiKey).id(id).setUsers();
	}

	public void performPatch(PatchBuilder builder)
	{
		RestResponse<Boolean> resp = applyCommon(builder).build().execute();

		if (resp.wasSuccessful())
		{
			return;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	public void performDelete(DeleteBuilder builder)
	{
		RestResponse<Boolean> resp = applyCommon(builder).build().execute();

		if (resp.wasSuccessful())
		{
			return;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	@Override
	public ItemSearch createItemSearch()
	{
		return new ItemSearchImpl(this);
	}

	@Override
	public CollectionSearch createCollectionSearch()
	{
		return new CollectionSearchImpl(this);
	}
}