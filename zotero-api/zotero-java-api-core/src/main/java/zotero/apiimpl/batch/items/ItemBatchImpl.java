package zotero.apiimpl.batch.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import zotero.api.Attachment;
import zotero.api.Item;
import zotero.api.batch.items.BatchItemHandle;
import zotero.api.batch.items.ItemsBatch;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.rest.ZoteroRest;

public abstract class ItemBatchImpl implements ItemsBatch
{
	private final List<BatchItemHandleImpl> items = new ArrayList<>();

	private final int maxCount;

	protected ItemBatchImpl(int maxCount)
	{
		this.maxCount = maxCount;
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public BatchItemHandle add(Item item)
	{
		if (item.getItemType() == ItemType.ATTACHMENT && ((Attachment) item).getLinkMode() == LinkMode.IMPORTED_FILE)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.API, ZoteroExceptionCodes.API.INVALID_BATCH_ITEM_TYPE, "Batch commital of imported file attachments is not supported.");
		}

		if (items.size() >= maxCount)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.API, ZoteroExceptionCodes.API.BATCH_SIZE_LIMIT_EXCEEDED, "Too many items in the batch. Maximum allowed is " + ZoteroRest.Batching.MAX_BATCH_COMMIT_COUNT);
		}

		ItemImpl ii = (ItemImpl) item;

		// Would be nice if we could check if the item is dirty before
		// committing here. Avoid API calls if we can

		BatchItemHandleImpl handle = new BatchItemHandleImpl(ii);

		this.items.add(handle);

		return handle;
	}

	public List<BatchItemHandleImpl> getItems()
	{
		return Collections.unmodifiableList(items);
	}

	public Iterator<BatchItemHandle> iterator()
	{
		return new 	Iterator<BatchItemHandle>() {

			Iterator<?> i = items.iterator();
			
			@Override
			public boolean hasNext()
			{
				return i.hasNext();
			}

			@Override
			public BatchItemHandle next()
			{
				return (BatchItemHandle) i.next();
			}};
	}
}
