package zotero.apiimpl.rest.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import zotero.api.Library;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.rest.response.ResponseBuilder;

public abstract class RestRequest<T>
{
	private static final String USER_AGENT = String.format("com.uthanda::zotero-api v%s (%s Java %s on %s/%s)", Library.API_VERSION,
			System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"));

	private String id;
	private boolean isUser;
	private String apiKey;

	private String apiUrl;
	private Map<String, String> urlParams;

	private URIBuilder builder = new URIBuilder();
	private Map<String, List<String>> queryParams;
	private Integer lastVersion;

	private ResponseBuilder<T> responseBuilder;

	public RestRequest()
	{
		builder.setScheme("https");
		builder.setHost(ZoteroRestPaths.ZOTERO_API_HOST);
	}

	public void setLastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setUser(boolean isUser)
	{
		this.isUser = isUser;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public void setApiUrl(String apiUrl)
	{
		this.apiUrl = apiUrl;
	}

	public void setUrlParams(Map<String, String> urlParams)
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

	private void doExecute() throws URISyntaxException, IOException, ClientProtocolException
	{
		HttpRequestBase httpRequest = prepare(buildURL());
		this.addHeaders(httpRequest);

		CloseableHttpClient client = HttpClients.createDefault();
		
		CloseableHttpResponse httpResponse = client.execute(httpRequest);

		this.responseBuilder.response(httpResponse);
	}

	private String readTextResponse(HttpEntity entity)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			InputStream is = entity.getContent();

			IOUtils.copy(is, bos);

			is.close();

			return new String(bos.toByteArray());
		}
		catch (UnsupportedOperationException | IOException e)
		{
			return e.getLocalizedMessage();
		}
	}

	String buildURL() throws URISyntaxException
	{
		if (apiUrl == null)
		{
			throw new IllegalStateException("apiUrl cannot be null");
		}

		String finalUri = String.format("%s%s%s", isUser ? ZoteroRestPaths.ZOTERO_API_USERS_BASE : ZoteroRestPaths.ZOTERO_API_GROUPS_BASE, id, apiUrl);

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

	private void addHeaders(HttpRequestBase request)
	{
		request.addHeader(ZoteroRestPaths.HEADER_ZOTERO_API_KEY, apiKey);
		request.addHeader(ZoteroRestPaths.HEADER_ZOTERO_API_VERSION, ZoteroRestPaths.ZOTERO_API_VERSION);
		request.addHeader(ZoteroRestPaths.HEADER_USER_AGENT, USER_AGENT);

		if (lastVersion != null)
		{
			request.addHeader(ZoteroRestPaths.HEADER_IF_MODIFIED_SINCE_VERSION, lastVersion.toString());
		}
	}
}