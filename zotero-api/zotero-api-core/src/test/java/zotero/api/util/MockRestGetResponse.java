package zotero.api.util;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import zotero.api.internal.rest.RestResponse;

public final class MockRestGetResponse<T> implements RestResponse<T>
{
	private JsonElement item;
	private JsonObject headers;
	private Class<?> type;
	private MockGetRequest<T> request;

	public MockRestGetResponse(JsonObject headers, JsonElement item, MockGetRequest<T> request, Class<?> type)
	{
		this.item = item;
		this.headers = headers;
		this.type = type;
		this.request = request;
	}

	@Override
	public boolean wasSuccessful()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getResponse()
	{
		return (T) new Gson().fromJson(item, type);
	}

	@Override
	public boolean hasNext()
	{
		return headers.has("next");
	}

	@Override
	public RestResponse<T> next() throws IOException
	{
		return request.next(headers.get("next").getAsString());
	}

	@Override
	public String getLink(String type)
	{
		return headers.get(type).getAsString();
	}

	@Override
	public Integer getTotalResults()
	{
		return headers.has("totalResults") ? headers.get("totalResults").getAsInt() : 0;
	}

	@Override
	public Integer getLastModifyVersion()
	{
		return null;
	}
}