package zotero.api.batch;

public interface BatchHandle<T>
{
	BatchResult getResult();
	
	String getMessage();
	
	T getItem();
}
