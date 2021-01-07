package zotero.apiimpl.batch;

import java.util.List;

import zotero.api.Item;
import zotero.apiimpl.ItemImpl;
import zotero.api.batch.BatchItemResponse;
import zotero.api.batch.BatchResult;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class BatchItemResponseImpl extends BatchResponseImpl<BatchItemHandleImpl, Item> implements BatchItemResponse
{
	private BatchItemResponseImpl(List<BatchItemHandleImpl> results, boolean hasErrors)
	{
		super(results, hasErrors);
	}

	public static BatchItemResponse create(List<BatchItemHandleImpl> results, ZoteroPostResponse response)
	{
		boolean hasErrors = false;
		
		for (BatchItemHandleImpl handle : results)
		{
			hasErrors = hasErrors || processHandle(response, handle);
		}

		return new BatchItemResponseImpl(results, hasErrors);
	}

	public static boolean processHandle(ZoteroPostResponse response, BatchItemHandleImpl handle)
	{
		if(handle.getResult() == BatchResult.INVALID)
		{
			return true;
		}
		
		String restItemKey = handle.getRestItemKey();
		
		if(response.getFailed().containsKey(restItemKey))
		{
			handle.setMessage(response.getFailed().get(restItemKey).getMessage());
			handle.setResult(BatchResult.FAILED);
			return true;
		}
		
		if(response.getUnchanged().containsKey(restItemKey))
		{
			handle.setMessage(response.getFailed().get(restItemKey).getMessage());
			handle.setResult(BatchResult.UNCHANGED);
			return true;
		}
		
		handle.setResult(BatchResult.SUCCESS);
		ZoteroRestItem item = response.getSuccessful().get(restItemKey);
		((ItemImpl)handle.getItem()).refresh(item);

		return false;
	}
}
