package zotero.apiimpl.batch;

import java.util.ArrayList;
import java.util.List;

import zotero.api.Item;
import zotero.api.batch.BatchItemHandle;
import zotero.api.batch.BatchItemResponse;
import zotero.api.batch.BatchResult;
import zotero.api.batch.ItemBatch;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public abstract class ItemBatchImpl implements ItemBatch
{
	private final List<BatchItemHandleImpl> items = new ArrayList<>();

	private final LibraryImpl library;

	protected ItemBatchImpl(LibraryImpl library)
	{
		this.library = library;
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public BatchItemHandle add(Item item)
	{
		if (items.size() >= ZoteroRest.Batching.MAX_BATCH_COUNT)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.BATCH_SIZE_LIMIT_EXCEEDED, "Too many items in the batch. Maximum allowed is " + ZoteroRest.Batching.MAX_BATCH_COUNT);
		}

		ItemImpl ii = (ItemImpl) item;

		// Would be nice if we could check if the item is dirty before
		// committing here. Avoid API calls if we can

		BatchItemHandleImpl handle = new BatchItemHandleImpl(ii);

		this.items.add(handle);

		return handle;
	}

	@Override
	public BatchItemResponse commit()
	{
		List<ZoteroRestItem> list = buildRestItems();

		BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> builder = getBuilder(list);

		builder.url(ZoteroRest.Items.ALL);

		RestResponse<ZoteroPostResponse> response = library.performRequest(builder);

		return BatchItemResponseImpl.create(items, response.getResponse());
	}

	private List<ZoteroRestItem> buildRestItems()
	{
		SerializationMode mode = getSerializationMode();

		List<ZoteroRestItem> list = new ArrayList<>();

		for (int i = 0; i < items.size(); i++)
		{
			buildItem(list, items.get(i), mode);
		}

		return list;
	}

	private void buildItem(List<ZoteroRestItem> list, BatchItemHandleImpl handle, SerializationMode mode)
	{
		ItemImpl item = (ItemImpl) handle.getItem();

		String restKey = Integer.toString(list.size());

		// Do we want to try this on insert into the batch?

		try
		{
			// Try building the rest item
			ZoteroRestItem restItem = item.buildRestItem(mode);
			// If there are no errors, add the item to the things to be
			// processed
			list.add(restItem);
			// Store the key (string representation of the index in the list)
			handle.setRestItemKey(restKey);
		}
		catch (ZoteroRuntimeException e)
		{
			// We have an invalid item (for some reason)
			handle.setResult(BatchResult.INVALID);
			// Note the error
			handle.setMessage(e.getLocalizedMessage());
		}

	}

	public abstract BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list);

	protected abstract SerializationMode getSerializationMode();
}
