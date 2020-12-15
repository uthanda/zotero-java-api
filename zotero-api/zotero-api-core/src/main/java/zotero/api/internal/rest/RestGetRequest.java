package zotero.api.internal.rest;

public interface RestGetRequest<T> extends RestRequest<T>
{
	RestResponse<T> get();

	RestResponse<T> next(String url);
}