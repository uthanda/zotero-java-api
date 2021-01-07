package zotero.apiimpl.batch;

import zotero.api.Item;
import zotero.api.batch.BatchItemHandle;
import zotero.api.batch.BatchResult;
import zotero.apiimpl.ItemImpl;

public class BatchItemHandleImpl implements BatchItemHandle
{
	private final ItemImpl item;
	private String restItemKey = null;
	private BatchResult result = BatchResult.UNEXECUTED;
	private String message = null;

	public BatchItemHandleImpl(ItemImpl item)
	{
		this.item = item;
	}

	public String getRestItemKey()
	{
		return restItemKey;
	}

	public void setRestItemKey(String restItemKey)
	{
		this.restItemKey = restItemKey;
	}

	@Override
	public BatchResult getResult()
	{
		return result;
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	public void setResult(BatchResult result)
	{
		this.result = result;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public Item getItem()
	{
		return item;
	}

}
