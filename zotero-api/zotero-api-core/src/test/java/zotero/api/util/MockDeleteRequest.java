package zotero.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import zotero.api.ZoteroAPIKey;
import zotero.apiimpl.rest.RestDeleteRequest;
import zotero.apiimpl.rest.RestRequest;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.builders.DeleteBuilder;

public class MockDeleteRequest implements RestDeleteRequest
{
	private Map<String, String> urlParams = new HashMap<>();
	private Map<String, String> queryParams = new HashMap<>();
	private MockBaseParams params = new MockBaseParams();
	private Function<MockDeleteRequest, Boolean> callback;
	private String key;

	public MockBaseParams getParams()
	{
		return params;
	}

	public String getKey()
	{
		return key;
	}

	@Override
	public RestResponse<Boolean> execute()
	{
		final Boolean success = callback.apply(this);
		
		return new RestResponse<Boolean>() {

			@Override
			public boolean wasSuccessful()
			{
				return success;
			}

			@Override
			public String getErrorMessage()
			{
				return null;
			}

			@Override
			public Boolean getResponse()
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
			}};
	}

	public Map<String, String> getUrlParams()
	{
		return urlParams;
	}

	public Map<String, String> getQueryParams()
	{
		return queryParams;
	}

	public static class MockRequestBuilder implements DeleteBuilder
	{
		private MockDeleteRequest request = new MockDeleteRequest();

		public MockRequestBuilder(Function<MockDeleteRequest, Boolean> callback)
		{
			request.callback = callback;
		}

		@Override
		public DeleteBuilder id(String id)
		{
			request.params.id = id;
			return this;
		}

		@Override
		public DeleteBuilder setUsers()
		{
			request.params.isUsers = Boolean.TRUE;
			return this;
		}

		@Override
		public DeleteBuilder setGroups()
		{
			request.params.isUsers  = Boolean.FALSE;
			return this;
		}

		@Override
		public DeleteBuilder apiKey(ZoteroAPIKey key)
		{
			request.params.key  = key;
			return this;
		}

		@Override
		public DeleteBuilder url(String url)
		{
			request.params.url = url;
			return this;
		}

		@Override
		public DeleteBuilder urlParam(String param, String value)
		{
			request.urlParams.put(param, value);
			return this;
		}

		@Override
		public void apply(RestRequest<?> request)
		{
		}

		@Override
		public DeleteBuilder applyCurrent(RestRequest<?> req)
		{
			return this;
		}

		@Override
		public DeleteBuilder lastVersion(Integer lastVersion)
		{
			return this;
		}

		@Override
		public DeleteBuilder queryParam(String param, String value)
		{
			request.queryParams.put(param, value);
			return this;
		}

		@Override
		public RestDeleteRequest build()
		{
			// Just for sanity sake, we can test that the key items are added to the request
			// They should be:
			// - User/Group ID
			// - Is Users/Groups
			// - API Key
			// - API URL
			request.params.validate();
			
			return request;
		}

		@Override
		public DeleteBuilder type(Class<?> type)
		{
			return this;
		}

		@Override
		public DeleteBuilder itemKey(String key)
		{
			request.key = key;
			request.urlParams.put(ZoteroRestPaths.URL_PARAM_KEY, key);
			return this;
		}
	}
}