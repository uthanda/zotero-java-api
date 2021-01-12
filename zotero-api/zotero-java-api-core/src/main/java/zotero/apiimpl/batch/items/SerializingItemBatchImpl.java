package zotero.apiimpl.batch.items;

import java.util.ArrayList;
import java.util.List;

import zotero.api.batch.BatchResult;
import zotero.api.batch.items.BatchItemResponse;
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

public abstract class SerializingItemBatchImpl extends ItemBatchImpl
{
	private LibraryImpl library;

	protected SerializingItemBatchImpl(LibraryImpl library, int maxCount)
	{
		super(maxCount);
		this.library = library;
	}

	protected abstract BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list);

	protected abstract SerializationMode getSerializationMode();

	@Override
	public BatchItemResponse commit()
	{
		List<ZoteroRestItem> list = buildRestItems();
	
		BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> builder = getBuilder(list);
	
		builder.url(ZoteroRest.Items.ALL);
	
		RestResponse<ZoteroPostResponse> response = library.performRequest(builder);
	
		return BatchItemResponseImpl.create(getItems(), response.getResponse());
	}

	private List<ZoteroRestItem> buildRestItems()
	{
		SerializationMode mode = getSerializationMode();
	
		List<ZoteroRestItem> list = new ArrayList<>();
	
		List<BatchItemHandleImpl> items = getItems();
		
		for (int i = 0; i < items.size(); i++)
		{
			buildItem(list, items.get(i), mode);
		}
	
		return list;
	}

	private void buildItem(List<ZoteroRestItem> list, BatchItemHandleImpl handle, SerializationMode mode)
	{
		ItemImpl item = (ItemImpl) handle.getItem();
	
		int restKey = list.size();
	
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

}
