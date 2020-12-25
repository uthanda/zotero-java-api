package zotero.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.RestPatchRequest;
import zotero.api.internal.rest.RestRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.builders.PatchBuilder;

public class MockPatchRequest implements RestPatchRequest
{
	public String id;
	public boolean users;
	public ZoteroAPIKey apiKey;
	public String url;
	public Map<String,String> urlParams = new HashMap<>();
	public Class<?> type;
	public Object content;

	private Function<MockPatchRequest,Boolean> callback;
	public String itemKey;
	public Integer versionNumber;
	
	public String getItemKey()
	{
		return itemKey;
	}

	public String getId()
	{
		return id;
	}

	public boolean isUsers()
	{
		return users;
	}

	public ZoteroAPIKey getApiKey()
	{
		return apiKey;
	}

	public String getUrl()
	{
		return url;
	}

	public Map<String, String> getUrlParams()
	{
		return urlParams;
	}

	public Class<?> getType()
	{
		return type;
	}

	public Object getContent()
	{
		return content;
	}

	@Override
	public RestResponse<Boolean> execute()
	{
		final Boolean status = callback.apply(this);
		
		return new RestResponse<Boolean>() {

			@Override
			public boolean wasSuccessful()
			{
				return status;
			}

			@Override
			public String getErrorMessage()
			{
				return null;
			}

			@Override
			public Boolean getResponse()
			{
				return status;
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
	
	public static class MockPatchBuilder implements PatchBuilder
	{
		private MockPatchRequest request = new MockPatchRequest();
		
		public MockPatchBuilder(Object data, Function<MockPatchRequest, Boolean> callback)
		{
			request.content = data;
			request.callback = callback;
		}
		
		@Override
		public PatchBuilder id(String id)
		{
			request.id = id;
			return this;
		}

		@Override
		public PatchBuilder setUsers()
		{
			request.users = true;
			return this;
		}

		@Override
		public PatchBuilder setGroups()
		{
			request.users = false;
			return this;
		}

		@Override
		public PatchBuilder apiKey(ZoteroAPIKey key)
		{
			request.apiKey = key;
			return this;
		}

		@Override
		public PatchBuilder url(String url)
		{
			request.url = url;
			return this;
		}

		@Override
		public PatchBuilder urlParam(String param, String value)
		{
			request.urlParams .put(param,value);
			return this;
		}

		@Override
		public PatchBuilder type(Class<?> type)
		{
			request.type = type;
			return this;
		}

		@Override
		public void apply(RestRequest<?> request)
		{
		}

		@Override
		public PatchBuilder applyCurrent(RestRequest<?> current)
		{
			return this;
		}

		@Override
		public PatchBuilder content(Object content)
		{
			request.content = content;
			return this;
		}

		@Override
		public RestPatchRequest build()
		{
			return request;
		}

		@Override
		public PatchBuilder itemKey(String key)
		{
			request.itemKey = key;
			return this;
		}

		@Override
		public PatchBuilder versionNumber(Integer versionNumber)
		{
			request.versionNumber = versionNumber;
			return this;
		}
	}
}
