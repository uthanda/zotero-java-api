package zotero.apiimpl.batch.items;

import java.util.ArrayList;
import java.util.List;

import zotero.api.batch.BatchHandle;
import zotero.api.batch.BatchResponse;

public class BatchResponseImpl<R extends BatchHandle<T>, T> implements BatchResponse<T>
{
	private List<R> results = new ArrayList<>();
	private boolean hasErrors;
	
	public BatchResponseImpl(List<R> results, boolean hasErrors)
	{
		this.results = results;
		this.hasErrors = hasErrors;
	}

	@Override
	public boolean hasErrors()
	{
		return hasErrors;
	}

	@Override
	public R getHandle(int index)
	{
		return results.get(index);
	}

	@Override
	public int getCount()
	{
		return results.size();
	}
}
