package zotero.api.util;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.RestPutRequest;
import zotero.api.internal.rest.RestRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.builders.PutBuilder;

public class MockPutRequest implements RestPutRequest
{
	private String id;
	private boolean isUsers = false;
	private ZoteroAPIKey key;
	private String url;
	private Map<String,String> urlParams;
	public Class<?> type;
	public Object content;
	
	public static class MockRequestBuilder implements PutBuilder
	{
		private MockPutRequest request = new MockPutRequest();
		
		public MockRequestBuilder(JsonObject data, Function<MockPutRequest,Boolean> putCallback)
		{
			request.putCallback = putCallback;
		}

		@Override
		public PutBuilder id(String id)
		{
			request.id = id;
			return this;
		}

		@Override
		public PutBuilder setUsers()
		{
			request.isUsers = true;
			return this;
		}

		@Override
		public PutBuilder setGroups()
		{
			request.isUsers = false;
			return this;
		}

		@Override
		public PutBuilder apiKey(ZoteroAPIKey key)
		{
			request.key = key;
			return this;
		}

		@Override
		public PutBuilder url(String url)
		{
			request.url = url;
			return this;
		}

		@Override
		public PutBuilder urlParam(String param, String value)
		{
			request.urlParams.put(param, value);
			return this;
		}

		@Override
		public PutBuilder type(Class<?> type)
		{
			request.type = type;
			return this;
		}

		@Override
		public void apply(RestRequest<?> request)
		{
		}

		@Override
		public PutBuilder applyCurrent(RestRequest<?> current)
		{
			return this;
		}

		@Override
		public PutBuilder content(Object content)
		{
			request.content = content;
			return this;
		}

		@Override
		public RestPutRequest build()
		{
			return request;
		}

	}

	private Function<MockPutRequest,Boolean> putCallback;

	@Override
	public RestResponse<Boolean> post()
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
			public boolean hasNext()
			{
				return false;
			}

			@Override
			public RestResponse<Boolean> next() throws IOException
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
