package zotero.api.batch;

public interface BatchResponse<T>
{
	boolean hasErrors();
	
	BatchHandle<T> getHandle(int index);
	
	int count();
}
