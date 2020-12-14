package zotero.api.internal.rest;

public interface RestGetRequest<T>
{
	RestGetRequest<T> apiUrl(String url);

	RestGetRequest<T> lastVersion(Integer lastVersion);

	RestGetRequest<T> addUrlParam(String param, String value);

	RestGetRequest<T> addQueryParam(String param, String value);

	RestResponse<T> get();

	RestResponse<T> next(String url);
}