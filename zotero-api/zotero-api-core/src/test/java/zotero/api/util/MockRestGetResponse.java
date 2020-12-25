package zotero.api.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import zotero.apiimpl.rest.RestResponse;

public final class MockRestGetResponse<T> implements RestResponse<T>
{
	private JsonElement item;
	private JsonObject headers;
	private Class<?> type;

	public MockRestGetResponse(JsonObject headers, JsonElement item, Class<?> type)
	{
		this.item = item;
		this.headers = headers;
		this.type = type;
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
	public String getLink(String type)
	{
		return headers.has(type) ? headers.get(type).getAsString() : null;
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