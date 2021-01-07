package zotero.api.batch;

public interface Batch<T,H extends BatchHandle<T>>
{
	BatchResponse<T> commit();
	
	int getCount();
	
	H add(T item);
}
