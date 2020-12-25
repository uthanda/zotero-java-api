package zotero.apiimpl.rest.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.apiimpl.rest.RestRequest;
import zotero.apiimpl.rest.builders.Builder;

@SuppressWarnings("rawtypes")
public abstract class ZoteroRestRequest
{
	public static final String ZOTERO_API_HOST = "api.zotero.org";
	public static final String ZOTERO_API_USERS_BASE = "/users/";
	public static final String ZOTERO_API_GROUPS_BASE = "/groups/";

	public static final String ZOTERO_API_VERSION = "3";
	
	public static final String HEADER_ZOTERO_WRITE_TOKEN = "Zotero-Write-Token";
	public static final String HEADER_ZOTERO_API_VERSION = "Zotero-API-Version";
	public static final String HEADER_ZOTERO_API_KEY = "Zotero-API-Key";
	public static final String HEADER_IF_UNMODIFIED_SINCE_VERSION = "If-Unmodified-Since-Version";
	public static final String HEADER_USER_AGENT = "User-Agent";
	
	private static final String USER_AGENT = String.format("com.uthanda::zotero-api v%s (%s Java %s on %s/%s)",
			Library.API_VERSION,
			System.getProperty("java.vendor"),
			System.getProperty("java.version"),
			System.getProperty("os.name"),
			System.getProperty("os.arch")
			);

	private String id;
	private boolean isUser;
	private String apiKey;

	private String apiUrl;
	private Map<String, String> urlParams;
	private Class<?> type;

	private URIBuilder builder = new URIBuilder();
	private Map<String, List<String>> queryParams;

	public ZoteroRestRequest()
	{
		builder.setScheme("https");
		builder.setHost(ZOTERO_API_HOST);
	}

	public URI buildURL() throws URISyntaxException
	{
		if (apiUrl == null)
		{
			throw new IllegalStateException("apiUrl cannot be null");
		}

		String finalUri = String.format("%s%s%s", isUser ? ZOTERO_API_USERS_BASE : ZOTERO_API_GROUPS_BASE, id, apiUrl);

		finalUri = processUrlParams(finalUri);

		builder.setPath(finalUri);

		if (queryParams != null)
		{
			queryParams.forEach((param, values) -> values.forEach(value -> builder.addParameter(param, value)));
		}

		return builder.build();
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
		request.addHeader(ZoteroRestRequest.HEADER_USER_AGENT, USER_AGENT);
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

	protected void addWriteToken(HttpRequest request)
	{
		request.addHeader(HEADER_ZOTERO_WRITE_TOKEN, UUID.randomUUID().toString().replace("-", ""));
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
		private Map<String, List<String>> queryParams;

		@Override
		public R id(String id)
		{
			this.id = id;
			return (R) this;
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
			request.queryParams = queryParams;
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

			return (R) this;
		}

		public R queryParam(String param, String value)
		{
			if (this.queryParams == null)
			{
				this.queryParams = new HashMap<>();
			}

			if (!this.queryParams.containsKey(param))
			{
				this.queryParams.put(param, new ArrayList<>());
			}

			this.queryParams.get(param).add(value);

			return (R) this;
		}
	}
}