package zotero.api.internal.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import zotero.api.internal.rest.RestGetRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.impl.ZoteroRestResponse.ZoteroRestResponseBuilder;

public class ZoteroRestGetRequest<T> extends ZoteroRestRequest implements RestGetRequest<T>
{
	private static Logger logger = LogManager.getLogger(ZoteroRestGetRequest.class);

	private static final String HEADER_IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Integer lastVersion;
	private Map<String, List<String>> queryParams;
	private ZoteroRestResponseBuilder<T> builder;

	private String specialUrl;

	private ZoteroRestGetRequest()
	{
	}

	@Override
	public RestResponse<T> execute()
	{
		logger.debug("Entering get()");

		builder = new ZoteroRestResponseBuilder<>();

		try
		{
			this.doGet();
		}
		catch (IOException ex)
		{
			builder.errorMessage(ex.getLocalizedMessage());
		}

		return builder.build();
	}

	private void doGet() throws IOException
	{
		HttpGet get = new HttpGet(specialUrl == null ? this.buildURL() : specialUrl);
		super.addHeaders(get);

		// Allow for rate limiting
		if (lastVersion != null)
		{
			get.addHeader(HEADER_IF_MODIFIED_SINCE_VERSION, lastVersion.toString());
		}

		CloseableHttpResponse response = httpClient.execute(get);

		// Parse the various elements
		parseTotalResultsHeader(response);
		parseLastVersionHeader(response);
		parseLinks(response);

		HttpEntity entity = response.getEntity();

		switch (response.getStatusLine().getStatusCode())
		{
			case HttpURLConnection.HTTP_OK:
			{
				parseEntity(entity);
				break;
			}

			case HttpURLConnection.HTTP_NO_CONTENT:
			{
				builder.response(null);
				break;
			}

			default:
			{
				builder.errorMessage(super.readResponse(entity));
			}
		}

	}

	private void parseEntity(HttpEntity entity) throws IOException
	{
		InputStream is = entity.getContent();

		InputStreamReader reader = new InputStreamReader(is);

		@SuppressWarnings("unchecked")
		T result = (T) new Gson().fromJson(reader, super.getType());

		builder.response(result);

		reader.close();
	}

	private void parseLinks(CloseableHttpResponse response)
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
				String type = elements[1].substring(5, elements[1].length() - 1);
				String link = elements[0].replace("<", "").replace(">", "");

				builder.addLink(type, link);
			}
		}
	}

	private void parseLastVersionHeader(CloseableHttpResponse response)
	{
		Header header;
		header = response.getFirstHeader("Last-Modified-Version");
		if (header != null)
		{
			builder.lastModifyVersion(Integer.valueOf(header.getValue()));
		}
	}

	private void parseTotalResultsHeader(CloseableHttpResponse response)
	{
		Header header = response.getFirstHeader("Total-Results");
		if (header != null)
		{
			builder.totalResults(Integer.valueOf(header.getValue()));
		}
	}

	@Override
	public String buildQueryParams()
	{
		if (queryParams == null || queryParams.isEmpty())
		{
			return "";
		}

		StringBuilder query = new StringBuilder();

		for (Map.Entry<String, List<String>> e : this.queryParams.entrySet())
		{
			for (String value : e.getValue())
			{
				if (query.length() > 0)
				{
					query.append('&');
				}
				
				query.append(e.getKey());
				query.append('=');
				try
				{
					query.append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
				}
				catch (UnsupportedEncodingException e1)
				{
					// Should never happen
					e1.printStackTrace();
				}
			}
		}

		return query.toString();
	}

	public static class Builder<T> extends ZoteroRestRequest.BaseBuilder<GetBuilder<T>> implements GetBuilder<T>
	{
		private Integer lastVersion;
		private Map<String, List<String>> queryParams;
		private String specialUrl;

		@Override
		public Builder<T> lastVersion(Integer lastVersion)
		{
			this.lastVersion = lastVersion;
			return this;
		}

		@Override
		public GetBuilder<T> applyCurrent(RestGetRequest<T> request)
		{
			ZoteroRestGetRequest<T> current = (ZoteroRestGetRequest<T>) request;

			lastVersion = current.lastVersion;
			queryParams = current.queryParams;
			specialUrl = current.specialUrl;

			super.applyCurrent(current);

			return this;
		}

		public GetBuilder<T> specialUrl(String url)
		{
			this.specialUrl = url;
			return this;
		}

		@Override
		public GetBuilder<T> queryParam(String param, String value)
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

			return this;
		}

		@Override
		public RestGetRequest<T> build()
		{
			ZoteroRestGetRequest<T> get = new ZoteroRestGetRequest<>();

			get.lastVersion = lastVersion;
			get.queryParams = queryParams;
			get.specialUrl = specialUrl;

			super.apply(get);

			return get;
		}

		public static <T> GetBuilder<T> createBuilder(Class<T> type)
		{
			return new Builder<T>().type(type);
		}
	}
}
