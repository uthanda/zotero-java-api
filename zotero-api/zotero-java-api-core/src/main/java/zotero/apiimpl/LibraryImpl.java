package zotero.apiimpl;

import zotero.api.Attachment;
import zotero.api.Collection;
import zotero.api.Document;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.Tag;
import zotero.api.ZoteroAuth;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.iterators.CollectionIteratorImpl;
import zotero.apiimpl.iterators.TagIteratorImpl;
import zotero.apiimpl.iterators.ItemIteratorImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.Collections;
import zotero.apiimpl.rest.ZoteroRest.Items;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.ResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.search.CollectionSearchImpl;
import zotero.apiimpl.search.ItemSearchImpl;

public final class LibraryImpl extends Library
{
	private final ZoteroAuth auth;
	private final String id;

	public static final Library create(String userId, ZoteroAuth auth)
	{
		return new LibraryImpl(auth, userId);
	}

	LibraryImpl(ZoteroAuth auth, String id)
	{
		this.auth = auth;
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
		GetBuilder<ZoteroRestItem,?> req = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem.class));

		req.url(Collections.SPECIFIC).urlParam(URLParameter.COLLECTION_KEY, key);

		RestResponse<ZoteroRestItem> response = performRequest(req);

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
		return fetchCollectionItems(Items.COLLECTION_ITEMS, key);
	}
	
	private ItemIterator fetchCollectionItems(String url, String key)
	{
		GetBuilder<ZoteroRestItem[],?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.COLLECTION_KEY, key);
		}

		RestResponse<ZoteroRestItem[]> response = performRequest(builder);

		return new ItemIteratorImpl(response, this);
	}

	@Override
	public ItemIterator fetchCollectionItemsTop(String key)
	{
		return fetchCollectionItems(Items.COLLECTION_ITEMS_TOP, key);
	}

	@Override
	public CollectionIterator fetchCollectionsAll()
	{
		return fetchCollections(Collections.ALL, null);
	}

	@Override
	public CollectionIterator fetchCollectionsTop()
	{
		return fetchCollections(Collections.TOP, null);
	}

	CollectionIterator fetchCollections(String url, String key)
	{
		GetBuilder<ZoteroRestItem[],?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.COLLECTION_KEY, key);
		}

		RestResponse<ZoteroRestItem[]> response = performRequest(builder);

		return new CollectionIteratorImpl(response, this);
	}

	@Override
	public ItemIterator fetchItemsAll()
	{
		return fetchItems(Items.ALL, null);
	}

	@Override
	public ItemIterator fetchItemsTop()
	{
		return fetchItems(Items.TOP, null);
	}

	@Override
	public ItemIterator fetchItemsTrash()
	{
		return fetchItems(Items.TRASH, null);
	}

	@Override
	public Item fetchItem(String key)
	{
		GetBuilder<ZoteroRestItem,?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem.class));

		builder.url(Items.SPECIFIC).urlParam(URLParameter.ITEM_KEY, key);

		RestResponse<ZoteroRestItem> response = performRequest(builder);

		return ItemImpl.fromItem(response.getResponse(), this);
	}

	ItemIterator fetchItems(String url, String key)
	{
		GetBuilder<ZoteroRestItem[],?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.ITEM_KEY, key);
		}
		
		RestResponse<ZoteroRestItem[]> response = performRequest(builder);

		return new ItemIteratorImpl(response, this);
	}

	@Override
	public Document createDocument(ItemType type)
	{
		return new DocumentImpl(type, this);
	}

	public <T,B extends BaseBuilder<T,B,R>,R extends ResponseBuilder<T>> RestResponse<T> performRequest(BaseBuilder<T,B,R> builder)
	{
		RestResponse<T> resp = builder.auth(auth).id(id).setUsers().build().execute();

		if (resp.wasSuccessful())
		{
			return resp;
		}

		throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, resp.getErrorMessage());
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

	@Override
	public Attachment createAttachment(LinkMode mode)
	{
		return new AttachmentImpl(mode, this);
	}

	@Override
	public Attachment createAttachment(Item parent, LinkMode mode)
	{
		return new AttachmentImpl(mode, this, parent.getKey());
	}

	@Override
	public Tag createTag(String tag)
	{
		return new TagImpl(tag, this);
	}

	@Override
	public TagIterator fetchTagsAll()
	{
		return fetchTags(ZoteroRest.Tags.ALL, null, null);
	}
	
	@Override
	public TagIterator fetchTag(String name)
	{
		return fetchTags(ZoteroRest.Tags.SPECIFIC, URLParameter.TAG_NAME, name);
	}

	private TagIterator fetchTags(String url, URLParameter parameter, String key)
	{
		GetBuilder<ZoteroRestItem[],?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(parameter, key);
		}

		RestResponse<ZoteroRestItem[]> response = performRequest(builder);

		return new TagIteratorImpl(response, this);
	}
}