package zotero.apiimpl.batch.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.Item;
import zotero.api.batch.BatchHandle;
import zotero.api.batch.items.BatchItemHandle;
import zotero.api.batch.items.BatchItemResponse;
import zotero.api.batch.items.RetrieveItemsBatch;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.PlaceholderItem;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public class RetrieveItemsBatchImpl extends ItemBatchImpl implements RetrieveItemsBatch
{
	private LibraryImpl library;

	public RetrieveItemsBatchImpl(LibraryImpl library)
	{
		super(ZoteroRest.Batching.MAX_BATCH_RETRIEVE_COUNT);
		this.library = library;
	}

	public BatchItemResponse commit()
	{
		Map<String, BatchItemHandle> handles = new HashMap<>();

		iterator().forEachRemaining(bih -> handles.put(bih.getItem().getKey(), bih));

		GetBuilder<ZoteroRestItem[], ?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class)).url(ZoteroRest.Items.ALL).queryParam("itemKey", handles.keySet().stream().collect(Collectors.joining(",")));

		RestResponse<ZoteroRestItem[]> response = library.performRequest(builder);

		ZoteroRestItem[] items = response.getResponse();

		for (ZoteroRestItem item : items)
		{
			Item i = handles.get(item.getKey()).getItem();

			if (i instanceof PlaceholderItem)
			{
				i = ItemImpl.fromRest(library, item);
				((BatchItemHandleImpl) handles.get(item.getKey())).replaceItem(i);
			}
			else
			{
				ItemImpl ii = (ItemImpl) handles.get(item.getKey()).getItem();
				ii.refresh(item);
			}
		}

		return new BatchItemResponse()
		{

			List<BatchItemHandleImpl> items = getItems();

			@Override
			public boolean hasErrors()
			{
				return false;
			}

			@Override
			public BatchHandle<Item> getHandle(int index)
			{
				return items.get(index);
			}

			@Override
			public int getCount()
			{
				return items.size();
			}
		};
	}

	@Override
	public BatchItemHandle add(String key)
	{
		return this.add(new PlaceholderItem(key));
	}
}