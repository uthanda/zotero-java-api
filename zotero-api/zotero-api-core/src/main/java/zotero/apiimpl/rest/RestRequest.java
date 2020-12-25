package zotero.apiimpl.rest;

public interface RestRequest<T>
{
	RestResponse<T> execute();
}