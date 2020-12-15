package zotero.api.internal.rest.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.RestRequest;
import zotero.api.internal.rest.builders.Builder;

@SuppressWarnings("rawtypes")
public abstract class ZoteroRestRequest<R extends ZoteroRestRequest>
{
	public static final String ZOTERO_API_USERS_BASE = "https://api.zotero.org/users/";
	public static final String ZOTERO_API_GROUPS_BASE = "https://api.zotero.org/groups/";

	public static final String ZOTERO_API_VERSION = "3";
	public static final String HEADER_ZOTERO_API_VERSION = "Zotero-API-Version";
	public static final String HEADER_ZOTERO_API_KEY = "Zotero-API-Key";

	private String id;
	private boolean isUser;
	private String apiKey;

	private String apiUrl;
	private Map<String, String> urlParams;
	private Class<?> type;

	protected String buildURL()
	{
		if (apiUrl == null)
		{
			throw new IllegalStateException("apiUrl cannot be null");
		}

		String finalUri = String.format("%s%s%s", isUser ? ZOTERO_API_USERS_BASE : ZOTERO_API_GROUPS_BASE, id, apiUrl);

		finalUri = processUrlParams(finalUri);
		finalUri = finalUri + buildQueryParams();

		return finalUri;
	}

	@SuppressWarnings({ "squid:S3400" })
	public String buildQueryParams()
	{
		return "";
	}

	private String processUrlParams(String finalUri)
	{
		if (urlParams == null || urlParams.isEmpty())
		{
			return finalUri;
		}

		for (Map.Entry<String, String> e : urlParams.entrySet())
		{
			String key = e.getKey();
			String value = e.getValue();

			if (!key.startsWith("{"))
			{
				key = "{" + key;
			}

			if (!key.endsWith("}"))
			{
				key = key + "}";
			}

			finalUri = finalUri.replace(key, value);
		}
		return finalUri;
	}

	public void addHeaders(HttpRequestBase request)
	{
		request.addHeader(ZoteroRestRequest.HEADER_ZOTERO_API_KEY, apiKey);
		request.addHeader(ZoteroRestRequest.HEADER_ZOTERO_API_VERSION, ZoteroRestRequest.ZOTERO_API_VERSION);
	}

	public final Class<?> getType()
	{
		return type;
	}

	public String readResponse(HttpEntity entity) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		BufferedInputStream is = new BufferedInputStream(entity.getContent());

		byte[] buffer = new byte[1024];
		int length = 0;

		while ((length = is.read(buffer)) != -1)
		{
			bos.write(buffer, 0, length);
		}

		is.close();

		return new String(bos.toByteArray());
	}

	@SuppressWarnings("unchecked")
	public static class BaseBuilder<R> implements Builder<R>
	{
		private boolean isUser;
		private ZoteroAPIKey apiKey;
		private String apiUrl;
		private Map<String, String> urlParams;
		private Class<?> type;
		private String id;

		@Override
		public R id(String id)
		{
			this.id = id;
			return (R)this;
		}

		@Override
		public R setUsers()
		{
			this.isUser = true;
			return (R) this;
		}

		@Override
		public R setGroups()
		{
			this.isUser = false;
			return (R) this;
		}

		@Override
		public R apiKey(ZoteroAPIKey key)
		{
			this.apiKey = key;
			return (R) this;
		}
		
		@Override
		public R url(String url)
		{
			this.apiUrl = url;
			return (R) this;
		}

		@Override
		public R urlParam(String param, String value)
		{
			if (urlParams == null)
			{
				urlParams = new HashMap<>();
			}

			urlParams.put(param, value);

			return (R) this;
		}

		@Override
		public R type(Class<?> type)
		{
			this.type = type;
			return (R) this;
		}

		@Override
		public void apply(RestRequest<?> req)
		{
			ZoteroRestRequest request = (ZoteroRestRequest) req;
			
			request.apiKey = apiKey.getApiKey();
			request.isUser = isUser;
			request.id = id;
			request.type = type;
			request.urlParams = urlParams;
			request.apiUrl = apiUrl;
		}

		@Override
		public R applyCurrent(RestRequest request)
		{
			ZoteroRestRequest current = (ZoteroRestRequest) request;
			
			apiKey = new ZoteroAPIKey(current.apiKey);
			isUser = current.isUser;
			id = current.id;
			type = current.type;
			urlParams = current.urlParams;
			apiUrl = current.apiUrl;

			return (R)this;
		}
	}
}