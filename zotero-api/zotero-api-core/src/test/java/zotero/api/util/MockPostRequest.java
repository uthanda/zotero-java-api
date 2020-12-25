package zotero.api.util;

import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import zotero.api.ZoteroAPIKey;
import zotero.apiimpl.rest.RestPostRequest;
import zotero.apiimpl.rest.RestRequest;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.builders.PostBuilder;

public class MockPostRequest implements RestPostRequest
{
	private String id;
	private boolean isUsers = false;
	private ZoteroAPIKey key;
	private String url;
	private Map<String,String> urlParams;
	public Class<?> type;
	public Object content;
	
	public static class MockRequestBuilder implements PostBuilder
	{
		private MockPostRequest request = new MockPostRequest();
		
		public MockRequestBuilder(JsonObject data, Function<MockPostRequest,Boolean> putCallback)
		{
			request.putCallback = putCallback;
		}

		@Override
		public PostBuilder id(String id)
		{
			request.id = id;
			return this;
		}

		@Override
		public PostBuilder setUsers()
		{
			request.isUsers = true;
			return this;
		}

		@Override
		public PostBuilder setGroups()
		{
			request.isUsers = false;
			return this;
		}

		@Override
		public PostBuilder apiKey(ZoteroAPIKey key)
		{
			request.key = key;
			return this;
		}

		@Override
		public PostBuilder url(String url)
		{
			request.url = url;
			return this;
		}

		@Override
		public PostBuilder urlParam(String param, String value)
		{
			request.urlParams.put(param, value);
			return this;
		}

		@Override
		public PostBuilder type(Class<?> type)
		{
			request.type = type;
			return this;
		}

		@Override
		public void apply(RestRequest<?> request)
		{
		}

		@Override
		public PostBuilder applyCurrent(RestRequest<?> current)
		{
			return this;
		}

		@Override
		public PostBuilder content(Object content)
		{
			request.content = content;
			return this;
		}

		@Override
		public RestPostRequest build()
		{
			return request;
		}

	}

	private Function<MockPostRequest,Boolean> putCallback;

	@Override
	public RestResponse<Boolean> execute()
	{
		final Boolean success = putCallback.apply(this);
		
		return new RestResponse<Boolean>() {

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

			@Override
			public Boolean getResponse()
			{
				return success;
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

	public final String getId()
	{
		return id;
	}

	public final boolean isUsers()
	{
		return isUsers;
	}

	public final ZoteroAPIKey getKey()
	{
		return key;
	}

	public final String getUrl()
	{
		return url;
	}

	public final Map<String, String> getUrlParams()
	{
		return urlParams;
	}

	public final Class<?> getType()
	{
		return type;
	}

	public final Object getContent()
	{
		return content;
	}
}
