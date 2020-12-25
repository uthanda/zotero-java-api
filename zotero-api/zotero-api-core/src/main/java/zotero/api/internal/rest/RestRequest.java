package zotero.api.internal.rest;

public interface RestRequest<T>
{
	RestResponse<T> execute();
}