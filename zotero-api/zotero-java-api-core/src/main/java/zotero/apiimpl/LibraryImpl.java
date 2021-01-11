package zotero.apiimpl;

import zotero.api.Attachment;
import zotero.api.Collection;
import zotero.api.Document;
import zotero.api.Item;
import zotero.api.Library;
import zotero.api.Tag;
import zotero.api.auth.ZoteroAuth;
import zotero.api.batch.item.CreateItemsBatch;
import zotero.api.batch.item.UpdateItemsBatch;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.api.iterators.ZoteroIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.batch.CreateItemsBatchImpl;
import zotero.apiimpl.batch.UpdateItemsBatchImpl;
import zotero.apiimpl.iterators.CollectionIteratorImpl;
import zotero.apiimpl.iterators.ItemIteratorImpl;
import zotero.apiimpl.iterators.TagIteratorImpl;
import zotero.apiimpl.iterators.ZoteroIteratorImpl;
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
		GetBuilder<ZoteroRestItem, ?> req = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem.class));

		req.url(Collections.SPECIFIC).urlParam(URLParameter.COLLECTION_KEY, key);

		RestResponse<ZoteroRestItem> response = performRequest(req);

		return CollectionImpl.fromItem(this, response.getResponse());
	}

	@Override
	public Collection createCollection(Collection parent)
	{
		return CollectionImpl.create(this, parent);
	}

	public ItemIterator fetchCollectionItems(String key)
	{
		return fetchCollectionItems(Items.COLLECTION_ITEMS, key, new ItemIteratorImpl(this));
	}

	public <T extends ZoteroIterator<?>> T fetchCollectionItems(String url, String key, T iterator)
	{
		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.COLLECTION_KEY, key);
		}
		
		@SuppressWarnings("unchecked")
		ZoteroIteratorImpl<T> it = (ZoteroIteratorImpl<T>) iterator;
		
		it.addQueryParams(builder);
		
		RestResponse<ZoteroRestItem[]> response = performRequest(builder);
		
		it.setResponse(response);
		
		return iterator;
	}

	public ItemIterator fetchCollectionItemsTop(String key)
	{
		return fetchCollectionItems(Items.COLLECTION_ITEMS_TOP, key, new ItemIteratorImpl(this));
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
		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.COLLECTION_KEY, key);
		}

		CollectionIteratorImpl it = new CollectionIteratorImpl(this);
		
		it.addQueryParams(builder);
		
		RestResponse<ZoteroRestItem[]> response = performRequest(builder);
		
		it.setResponse(response);
		
		return it;
		
	}

	@Override
	public ItemIterator fetchItemsAll()
	{
		return fetchItems(Items.ALL, null, new ItemIteratorImpl(this));
	}

	@Override
	public ItemIterator fetchItemsTop()
	{
		return fetchItems(Items.TOP, null, new ItemIteratorImpl(this));
	}

	@Override
	public ItemIterator fetchItemsTrash()
	{
		return fetchItems(Items.TRASH, null, new ItemIteratorImpl(this));
	}

	@Override
	public Item fetchItem(String key)
	{
		GetBuilder<ZoteroRestItem, ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem.class));

		builder.url(Items.SPECIFIC).urlParam(URLParameter.ITEM_KEY, key);

		RestResponse<ZoteroRestItem> response = performRequest(builder);

		return ItemImpl.fromRest(this, response.getResponse());
	}

	<T extends ZoteroIterator<?>> T fetchItems(String url, String key, T iterator)
	{
		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(URLParameter.ITEM_KEY, key);
		}
		
		ZoteroIteratorImpl<?> it = (ZoteroIteratorImpl<?>)iterator;
		
		it.addQueryParams(builder);
		
		RestResponse<ZoteroRestItem[]> response = performRequest(builder);
		
		it.setResponse(response);
		
		return iterator;
	}

	@Override
	public Document createDocument(ItemType type)
	{
		return new DocumentImpl(this, type);
	}

	public <T, B extends BaseBuilder<T, B, R>, R extends ResponseBuilder<T>> RestResponse<T> performRequest(BaseBuilder<T, B, R> builder)
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
		return new AttachmentImpl(this, mode);
	}

	@Override
	public Attachment createAttachment(Item parent, LinkMode mode)
	{
		Attachment attachment = createAttachment(mode);
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.PARENT_ITEM, parent.getKey());
		return attachment;
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
		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		builder.url(url);

		if (key != null)
		{
			builder.urlParam(parameter, key);
		}

		TagIteratorImpl ti = new TagIteratorImpl(this);
		
		RestResponse<ZoteroRestItem[]> response = performRequest(builder);
		ti.addQueryParams(builder);

		ti.setResponse(response);
		return ti;
	}

	@Override
	public CreateItemsBatch createCreateItemsBatch()
	{
		return new CreateItemsBatchImpl(this);
	}

	@Override
	public UpdateItemsBatch createUpdateItemsBatch()
	{
		return new UpdateItemsBatchImpl(this);
	}
}