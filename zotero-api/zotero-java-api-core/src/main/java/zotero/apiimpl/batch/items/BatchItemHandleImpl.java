package zotero.apiimpl.batch.items;

import zotero.api.Item;
import zotero.api.batch.BatchResult;
import zotero.api.batch.items.BatchItemHandle;

public class BatchItemHandleImpl implements BatchItemHandle
{
	private Item item;
	private String restItemKey = null;
	private BatchResult result = BatchResult.UNEXECUTED;
	private String message = null;
	private int restItemIndex;

	public BatchItemHandleImpl(Item item)
	{
		this.item = item;
	}

	public String getRestItemKey()
	{
		return restItemKey;
	}

	public void setRestItemKey(int restItemIndex)
	{
		this.restItemIndex = restItemIndex;
		this.restItemKey = Integer.toString(restItemIndex);
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

	@Override
	public int getIndex()
	{
		return restItemIndex;
	}

	void replaceItem(Item replacement)
	{
		this.item = replacement;
	}
}
