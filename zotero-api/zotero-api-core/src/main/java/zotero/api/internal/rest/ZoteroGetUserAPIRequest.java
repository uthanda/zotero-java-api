package zotero.api.internal.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.ZoteroAPIResponse.ZoteroGetAPIResponseBuilder;

public class ZoteroGetUserAPIRequest<T> implements RestGetRequest<T>
{
	private static final String HEADER_IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";
	private static final String ZOTERO_API_BASE = "https://api.zotero.org/users/";
	private static final String ZOTERO_API_VERSION = "3";
	private static final String HEADER_ZOTERO_API_VERSION = "Zotero-API-Version";
	private static final String HEADER_ZOTERO_API_KEY = "Zotero-API-Key";

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Gson gson = new Gson();
	private final String apiKey;
	private final String userId;
	private Integer lastVersion;
	private String apiUrl;
	private Map<String, String> urlParams;
	private Map<String, String> queryParams;
	private Class<T> type;

	/**
	 * Creates a Zotero API GET REST request
	 * 
	 * @param apiKey
	 *            API Key
	 * @param userId
	 *            User Id
	 * @param type
	 *            Expected object return type
	 */
	public ZoteroGetUserAPIRequest(ZoteroAPIKey apiKey, String userId, Class<T> type)
	{
		this.apiKey = apiKey.getApiKey();
		this.userId = userId;
		this.type = type;
	}

	@Override
	public ZoteroGetUserAPIRequest<T> apiUrl(String url)
	{
		if (!url.startsWith("/"))
		{
			throw new IllegalStateException("A Zotero API sub-URI must start with a '/'");
		}

		this.apiUrl = url;
		return this;
	}

	@Override
	public ZoteroGetUserAPIRequest<T> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	@Override
	public ZoteroGetUserAPIRequest<T> addUrlParam(String param, String value)
	{
		if (urlParams == null)
		{
			urlParams = new HashMap<>();
		}

		urlParams.put(param, value);

		return this;
	}

	@Override
	public ZoteroAPIResponse<T> get()
	{
		if (apiUrl == null)
		{
			throw new IllegalStateException("apiUrl cannot be null");
		}

		return this.get(this.buildUri());
	}

	private ZoteroAPIResponse<T> get(String uri)
	{
		ZoteroGetAPIResponseBuilder<T> builder = new ZoteroAPIResponse.ZoteroGetAPIResponseBuilder<>();

		try
		{
			InputStreamReader reader = doGet(uri, lastVersion, builder);

			T result = gson.fromJson(reader, type);

			reader.close();
			
			builder.response(result);
		}
		catch (IOException ex)
		{
			builder.errorMessage(ex.getLocalizedMessage());
		}
		
		return builder.build();
	}

	public InputStreamReader doGet(String url, Integer lastVersion, ZoteroGetAPIResponseBuilder<?> builder) throws IOException
	{
		HttpGet get = new HttpGet(url);
		get.addHeader(HEADER_ZOTERO_API_KEY, apiKey);
		get.addHeader(HEADER_ZOTERO_API_VERSION, ZOTERO_API_VERSION);

		// Allow for rate limiting
		if (lastVersion != null)
		{
			get.addHeader(HEADER_IF_MODIFIED_SINCE_VERSION, lastVersion.toString());
		}

		CloseableHttpResponse response = httpClient.execute(get);

		// Parse the various elements
		parseTotalResultsHeader(builder, response);
		parseLastVersionHeader(builder, response);
		parseLinks(builder, response);

		HttpEntity entity = response.getEntity();

		InputStream is = entity.getContent();

		return new InputStreamReader(is);
	}

	private static void parseLinks(ZoteroGetAPIResponseBuilder<?> builder, CloseableHttpResponse response)
	{
		Header header;
		header = response.getFirstHeader("Link");
		if (header != null)
		{
			String[] links = header.getValue().split(",");
			for (int i = 0; i < links.length; i++)
			{
				String[] elements = links[i].split(";");
				elements[1] = elements[1].trim();
				String type = elements[1].substring(6, elements[1].length() - 1);
				String link = elements[0].replace("<", "").replace(">", "");

				builder.addLink(type, link);
			}
		}
	}

	private static void parseLastVersionHeader(ZoteroGetAPIResponseBuilder<?> builder, CloseableHttpResponse response)
	{
		Header header;
		header = response.getFirstHeader("Last-Modified-Version");
		if (header != null)
		{
			builder.lastModifyVersion(Integer.valueOf(header.getValue()));
		}
	}

	private static void parseTotalResultsHeader(ZoteroGetAPIResponseBuilder<?> builder, CloseableHttpResponse response)
	{
		Header header = response.getFirstHeader("Total-Results");
		if (header != null)
		{
			builder.totalResults(Integer.valueOf(header.getValue()));
		}
	}

	private String buildUri()
	{
		String finalUri = ZOTERO_API_BASE + userId + apiUrl;

		finalUri = processUrlParams(finalUri);
		finalUri = finalUri + buildQueryParams();

		return finalUri;
	}

	private String buildQueryParams()
	{
		if (queryParams == null || queryParams.isEmpty())
		{
			return "";
		}

		// TODO manage query params

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

	@Override
	public RestResponse<T> next(String url)
	{
		if (queryParams != null)
		{
			this.queryParams.clear();
		}

		return this.get(url);
	}

	// This method exists to for mocking (Both literally for PowerMock and
	// figuratively for someone
	// who knows more about PowerMock than I do to explain how this should be
	// done
	public static <T> RestGetRequest<T> create(ZoteroAPIKey apiKey, String userId, Class<T> type)
	{
		return new ZoteroGetUserAPIRequest<>(apiKey, userId, type);
	}

	@Override
	public RestGetRequest<T> addQueryParam(String param, String value)
	{
		if (this.queryParams == null)
		{
			this.queryParams = new HashMap<>();
		}

		this.queryParams.put(param, value);

		return this;
	}
}
