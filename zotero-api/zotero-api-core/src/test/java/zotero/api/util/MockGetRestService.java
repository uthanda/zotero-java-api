package zotero.api.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import zotero.api.internal.rest.RestGetRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroGetUserAPIRequest;
import zotero.api.internal.rest.model.ZoteroRestItem;

public class MockGetRestService
{
	private static final class MockGetRequest<T> implements RestGetRequest<T>
	{
		private Map<String, String> urlParams = new HashMap<>();
		private Map<String, String> queryParams = new HashMap<>();
		private String url;
		private Class<?> type;

		private MockGetRequest(Class<T> type)
		{
			this.type = type;
		}

		@Override
		public RestGetRequest<T> apiUrl(String url)
		{
			this.url = url;
			return this;
		}

		@Override
		public RestGetRequest<T> lastVersion(Integer lastVersion)
		{
			return this;
		}

		@Override
		public RestGetRequest<T> addUrlParam(String param, String value)
		{
			urlParams.put(param, value);
			return this;
		}

		@Override
		public RestResponse<T> get()
		{
			String urlParamsKey = buildUrlParams();
			String queryParamsKey = buildQueryParams();

			if (!data.has(url))
			{
				return new ErrorRestResponse<T>(String.format("URL %s not found in test data set", url));
			}

			JsonObject urlObj = data.get(url).getAsJsonObject();

			if (!urlObj.has(urlParamsKey))
			{
				return new ErrorRestResponse<T>(String.format("URL '%s'.'%s' not found in test data set", url, urlParamsKey));
			}

			JsonObject urlParamsObject = urlObj.get(urlParamsKey).getAsJsonObject();

			if (!urlParamsObject.has(queryParamsKey))
			{
				return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s' not found in test data set", url, urlParamsKey, queryParamsKey));
			}

			JsonObject queryParamsObject = urlParamsObject.get(queryParamsKey).getAsJsonObject();

			if (!queryParamsObject.has("headers"))
			{
				return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s'.'headers' not found in test data set", url, urlParamsKey, queryParamsKey));
			}

			if (!queryParamsObject.has("item"))
			{
				return new ErrorRestResponse<T>(String.format("URL '%s'.'%s'.'%s'.'item' not found in test data set", url, urlParamsKey, queryParamsKey));
			}

			JsonObject headers = queryParamsObject.get("headers").getAsJsonObject();
			JsonElement item = queryParamsObject.get("item");

			return new MockRestResponse<T>(headers, item, this, type);
		}

		private String buildQueryParams()
		{
			if (this.queryParams.isEmpty())
			{
				return "<empty>";
			}

			String[] keys = this.queryParams.keySet().toArray(new String[this.queryParams.size()]);
			Arrays.sort(keys);

			StringBuffer sb = new StringBuffer();

			for (String param : keys)
			{
				sb.append("/");
				sb.append(param);
				sb.append("=");
				sb.append(this.queryParams.get(param));
			}

			return sb.toString();
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

		@Override
		public RestResponse<T> next(String url)
		{
			return null;
		}

		@Override
		public RestGetRequest<T> addQueryParam(String param, String value)
		{
			this.queryParams.put(param, value);
			return this;
		}
	}

	private static final class MockRestResponse<T> implements RestResponse<T>
	{
		private JsonElement item;
		private JsonObject headers;
		private Class<?> type;

		public MockRestResponse(JsonObject headers, JsonElement item, RestGetRequest<T> request, Class<?> type)
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
		public boolean hasNext()
		{
			return headers.has("next");
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public RestResponse<T> next() throws IOException
		{
			String next = headers.get("next").getAsString();
			int start = headers.get("start").getAsInt();
			String key = headers.get("key").getAsString();

			return new MockGetRequest(type).apiUrl(next).addUrlParam("key", key).addQueryParam("start", Integer.toString(start)).get();
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

	private static final class ErrorRestResponse<T> implements RestResponse<T>
	{
		private String message;

		public ErrorRestResponse(String message)
		{
			this.message = message;
		}

		@Override
		public boolean wasSuccessful()
		{
			return false;
		}

		@Override
		public String getErrorMessage()
		{
			return message;
		}

		@Override
		public T getResponse()
		{
			return null;
		}

		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public RestResponse<T> next() throws IOException
		{
			return null;
		}

		@Override
		public String getLink(String type)
		{
			return null;
		}

		@Override
		public Integer getTotalResults()
		{
			return null;
		}

		@Override
		public Integer getLastModifyVersion()
		{
			return null;
		}
	}

	private static JsonObject data;

	public void initialize() throws NoSuchMethodException, SecurityException
	{
		if (data == null)
		{
			data = (JsonObject) JsonParser.parseReader(new InputStreamReader(MockGetRestService.class.getResourceAsStream("/zotero/testData.json")));
		}

		PowerMockito.mockStatic(ZoteroGetUserAPIRequest.class);

		when(ZoteroGetUserAPIRequest.create(any(), any(), any())).thenAnswer(new Answer<RestGetRequest<?>>()
		{
			@Override
			public RestGetRequest<?> answer(InvocationOnMock invocation) throws Throwable
			{
				if (invocation.getArgumentAt(2, Class.class) == ZoteroRestItem.class)
				{
					return prepareMockedRequestSingleItem();
				}
				else
				{
					return prepareMockedRequestMultipleItems();
				}
			}
		});
	}

	private RestGetRequest<ZoteroRestItem> prepareMockedRequestSingleItem() throws Exception
	{
		return new MockGetRequest<ZoteroRestItem>(ZoteroRestItem.class);
	}

	private RestGetRequest<ZoteroRestItem[]> prepareMockedRequestMultipleItems() throws Exception
	{
		return new MockGetRequest<ZoteroRestItem[]>(ZoteroRestItem[].class);
	}
}