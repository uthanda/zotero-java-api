package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.ItemType;
import zotero.api.internal.rest.RestGetRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.builders.PatchBuilder;
import zotero.api.internal.rest.builders.PostBuilder;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

public final class LibraryImpl extends Library
{
	private final ZoteroAPIKey apiKey;
	private final String id;

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
		applyBaseRequestInfo(req);

		req.url(ZoteroRestPaths.COLLECTION).urlParam("key", key);

		RestResponse<ZoteroRestItem> response = performGet(req.build());

		return CollectionImpl.fromItem(response.getResponse(), this);
	}

	private void applyBaseRequestInfo(zotero.api.internal.rest.builders.Builder<?> req)
	{
		req.apiKey(apiKey);
		req.id(id);
		req.setUsers();
	}

	private <T> RestResponse<T> performGet(RestGetRequest<T> req)
	{
		RestResponse<T> resp = req.get();

		if (resp.wasSuccessful())
		{
			return resp;
		}

		throw new RuntimeException(resp.getErrorMessage());
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
		return fetchCollections(ZoteroRestPaths.COLLECTIONS_ALL, null);
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

		this.applyBaseRequestInfo(builder);

		if (key != null)
		{
			builder.urlParam("key", key);
		}

		RestResponse<ZoteroRestItem[]> response = performGet(builder.build());

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

		this.applyBaseRequestInfo(builder);

		builder.url(ZoteroRestPaths.ITEM).urlParam("key", key);

		RestResponse<ZoteroRestItem> response = performGet(builder.build());

		return ItemImpl.fromItem(response.getResponse(), this);
	}

	ItemIterator fetchItems(String url, String key)
	{
		GetBuilder<ZoteroRestItem[]> builder = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem[].class);
		builder.url(url);

		this.applyBaseRequestInfo(builder);

		if (key != null)
		{
			builder.urlParam("key", key);
		}

		RestResponse<ZoteroRestItem[]> response = performGet(builder.build());

		return new ZoteroItemIteratorImpl(response, this);
	}

	@Override
	public Item createItem(ItemType type)
	{
		return new ItemImpl(type, this);
	}

	public void performPost(PostBuilder builder)
	{
		RestResponse<Boolean> resp = builder.apiKey(apiKey).id(id).setUsers().build().post();

		if (resp.wasSuccessful())
		{
			return;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	public static final Library create(String userId, ZoteroAPIKey apiKey)
	{
		return new LibraryImpl(apiKey, userId);
	}

	public void performPatch(PatchBuilder builder)
	{
		RestResponse<Boolean> resp = builder.apiKey(apiKey).id(id).setUsers().build().patch();

		if (resp.wasSuccessful())
		{
			return;
		}

		throw new RuntimeException(resp.getErrorMessage());
	}

	public void performDelete()
	{
		// TODO Auto-generated method stub
		
	}
}