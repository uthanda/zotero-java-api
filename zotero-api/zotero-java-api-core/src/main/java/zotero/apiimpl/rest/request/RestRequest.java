package zotero.apiimpl.rest.request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import zotero.api.Library;
import zotero.api.auth.ZoteroAuth;
import zotero.apiimpl.rest.ZoteroRest.API;
import zotero.apiimpl.rest.ZoteroRest.Headers;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.response.ResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public abstract class RestRequest<T>
{
	private static final String USER_AGENT = String.format("com.uthanda::zotero-api v%s (%s Java %s on %s/%s)", Library.API_VERSION, System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));

	private String id;
	private boolean isUser;
	private ZoteroAuth auth;

	private String apiUrl;

	private Map<URLParameter, String> urlParams;
	private Map<String, List<String>> queryParams;
	private Map<String, List<String>> headers;

	private URIBuilder builder = new URIBuilder();

	private ResponseBuilder<T> responseBuilder;

	protected RestRequest()
	{
		builder.setScheme("https");
		builder.setHost(API.HOST);
	}

	public void setHeaders(Map<String, List<String>> headers)
	{
		this.headers = headers;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setUser(boolean isUser)
	{
		this.isUser = isUser;
	}

	public void setAuth(ZoteroAuth auth)
	{
		this.auth = auth;
	}

	public void setApiUrl(String apiUrl)
	{
		this.apiUrl = apiUrl;
	}

	public void setUrlParams(Map<URLParameter, String> urlParams)
	{
		this.urlParams = urlParams;
	}

	public void setBuilder(URIBuilder builder)
	{
		this.builder = builder;
	}

	public void setQueryParams(Map<String, List<String>> queryParams)
	{
		this.queryParams = queryParams;
	}

	public void setResponseBuilder(ResponseBuilder<T> builder)
	{
		this.responseBuilder = builder;
	}

	public abstract HttpRequestBase prepare(String url);

	public RestResponse<T> execute()
	{
		try
		{
			doExecute();
		}
		catch (URISyntaxException | IOException e)
		{
			responseBuilder.errorMessage(e.getLocalizedMessage());
		}

		return responseBuilder.build();
	}

	private void doExecute() throws URISyntaxException, IOException
	{
		String url = this.buildURL();

		HttpRequestBase httpRequest = this.prepare(url);

		this.addHeaders(httpRequest);

		try (CloseableHttpClient client = HttpClients.createDefault())
		{
			CloseableHttpResponse httpResponse = client.execute(httpRequest);

			this.responseBuilder.response(httpResponse);
		}
	}

	protected String buildURL() throws URISyntaxException
	{
		if (apiUrl == null)
		{
			throw new IllegalStateException("apiUrl cannot be null");
		}

		String finalUri = String.format("%s%s%s", isUser ? API.USERS_BASE : API.GROUPS_BASE, id, apiUrl);

		finalUri = processUrlParams(finalUri);

		builder.setPath(finalUri);

		if (queryParams != null)
		{
			queryParams.forEach((param, values) -> values.forEach(value -> builder.addParameter(param, value)));
		}

		return builder.build().toString();
	}

	private String processUrlParams(String finalUri)
	{
		if (urlParams == null || urlParams.isEmpty())
		{
			return finalUri;
		}

		for (Map.Entry<URLParameter, String> e : urlParams.entrySet())
		{
			String key = e.getKey().getZoteroName();
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
		request.addHeader(Headers.ZOTERO_API_VERSION, API.VERSION);
		request.addHeader(Headers.USER_AGENT, USER_AGENT);

		// Add the authorization headers
		auth.applyHeaders(request);

		// Add any custom request headers (if available)
		if (headers != null)
		{
			headers.forEach((header, values) -> values.forEach(value -> request.addHeader(header, value)));
		}
	}

	public String getId()
	{
		return id;
	}

	public boolean isUser()
	{
		return isUser;
	}

	public String getApiUrl()
	{
		return apiUrl;
	}

	public Map<URLParameter, String> getUrlParams()
	{
		return urlParams;
	}

	public URIBuilder getBuilder()
	{
		return builder;
	}

	public Map<String, List<String>> getQueryParams()
	{
		return queryParams;
	}

	public ResponseBuilder<T> getResponseBuilder()
	{
		return responseBuilder;
	}
}