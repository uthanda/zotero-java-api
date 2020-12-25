package zotero.api.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import zotero.api.ZoteroAPIKey;
import zotero.apiimpl.rest.RestGetRequest;
import zotero.apiimpl.rest.RestRequest;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.builders.GetBuilder;

public class MockGetRequest<T> implements RestGetRequest<T>
{
	private Map<String, String> urlParams = new HashMap<>();
	private Params queryParams = new Params();
	private Class<?> type;
	private JsonObject data;
	private MockBaseParams params = new MockBaseParams();

	@Override
	public RestResponse<T> execute()
	{
		if (params.isSpecial)
		{
			// Handle the get next case where the next URL is
			// <basepath>|<start>|<key>
			String[] elements = params.url.split("\\|");
			params.url = elements[0];
			urlParams.put("key", elements[2]);
			queryParams.addParam("start", elements[1]);
		}

		String urlParamsKey = buildUrlParams();
		String queryParamsKey = queryParams.buildQueryParams();

		if (!data.has(params.url))
		{
			return new ErrorRestResponse<T>(String.format("URL %s not found in test data set", params.url));
		}

		JsonObject urlObj = data.get(params.url).getAsJsonObject();

		if (!urlObj.has(urlParamsKey))
		{
			return new ErrorRestResponse<T>(String.format("URL '%s'.'%s' not found in test data set", params.url, urlParamsKey));
		}

		JsonObject urlParamsObject = urlObj.get(urlParamsKey).getAsJsonObject();

		if (!urlParamsObject.has(queryParamsKey))
		{
			return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s' not found in test data set", params.url, urlParamsKey, queryParamsKey));
		}

		JsonObject queryParamsObject = urlParamsObject.get(queryParamsKey).getAsJsonObject();

		if (!queryParamsObject.has("headers"))
		{
			return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s'.'headers' not found in test data set", params.url, urlParamsKey, queryParamsKey));
		}

		if (!queryParamsObject.has("item"))
		{
			return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s'.'item' not found in test data set", params.url, urlParamsKey, queryParamsKey));
		}

		JsonObject headers = queryParamsObject.get("headers").getAsJsonObject();
		JsonElement item = queryParamsObject.get("item");

		return new MockRestGetResponse<T>(headers, item, type);
	}

	private String buildUrlParams()
	{
		if (this.urlParams.isEmpty())
		{
			return "<empty>";
		}

		String[] keys = this.urlParams.keySet().toArray(new String[this.urlParams.size()]);
		Arrays.sort(keys);

		StringBuffer sb = new StringBuffer();

		for (String param : keys)
		{
			sb.append("/");
			sb.append(param);
			sb.append("=");
			sb.append(this.urlParams.get(param));
		}

		return sb.toString();
	}

	public static class MockRequestBuilder<T> implements GetBuilder<T>
	{
		private MockGetRequest<T> request = new MockGetRequest<>();

		public MockRequestBuilder(Class<T> type, JsonObject data)
		{
			request.type = type;
			request.data = data;
		}

		@Override
		public GetBuilder<T> id(String id)
		{
			request.params.id = id;
			return this;
		}

		@Override
		public GetBuilder<T> setUsers()
		{
			request.params.isUsers = Boolean.TRUE;
			return this;
		}

		@Override
		public GetBuilder<T> setGroups()
		{
			request.params.isUsers = Boolean.FALSE;
			return this;
		}

		@Override
		public GetBuilder<T> apiKey(ZoteroAPIKey key)
		{
			request.params.key = key;
			return this;
		}

		@Override
		public GetBuilder<T> url(String url)
		{
			request.params.url = url;
			return this;
		}

		@Override
		public GetBuilder<T> urlParam(String param, String value)
		{
			request.urlParams.put(param, value);
			return this;
		}

		@Override
		public GetBuilder<T> type(Class<?> type)
		{
			request.type = type;
			return this;
		}

		@Override
		public void apply(RestRequest<?> request)
		{
		}

		@Override
		public GetBuilder<T> applyCurrent(RestRequest<?> req)
		{
			return this;
		}

		@Override
		public GetBuilder<T> lastVersion(Integer lastVersion)
		{
			return this;
		}

		@Override
		public GetBuilder<T> applyCurrent(RestGetRequest<T> req)
		{
			MockGetRequest<T> current = (MockGetRequest<T>) req;

			request.data = current.data;
			request.queryParams = current.queryParams;
			request.type = current.type;
			request.urlParams = current.urlParams;
			request.params.url = current.params.url;
			request.params.id = current.params.id;
			request.params.isUsers = current.params.isUsers;
			request.params.key = current.params.key;

			return this;
		}

		@Override
		public GetBuilder<T> queryParam(String param, String value)
		{
			request.queryParams.addParam(param, value);
			return this;
		}

		@Override
		public RestGetRequest<T> build()
		{
			// Just for sanity sake, we can test that the key items are added to
			// the request
			// They should be:
			// - User/Group ID
			// - Is Users/Groups
			// - API Key
			// - API URL
			request.params.validate();

			return request;
		}

		@Override
		public GetBuilder<T> specialUrl(String url)
		{
			request.params.url = url;
			request.params.isSpecial = true;
			return this;
		}
	}
}