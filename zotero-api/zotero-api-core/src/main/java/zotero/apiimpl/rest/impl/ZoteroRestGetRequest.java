package zotero.apiimpl.rest.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import zotero.apiimpl.rest.RestGetRequest;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestResponse.ZoteroRestResponseBuilder;

public class ZoteroRestGetRequest<T> extends ZoteroRestRequest implements RestGetRequest<T>
{
	private static Logger logger = LogManager.getLogger(ZoteroRestGetRequest.class);

	private static final String HEADER_IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Integer lastVersion;
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
		catch (IOException | URISyntaxException ex)
		{
			builder.errorMessage(ex.getLocalizedMessage());
		}

		return builder.build();
	}

	private void doGet() throws IOException, URISyntaxException
	{
		HttpGet get = specialUrl == null ? new HttpGet(this.buildURL()) : new HttpGet(specialUrl.trim());
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

			case 429:
			{
				builder.errorMessage("Too many requests.  Please retry after " + response.getFirstHeader("Retry-After") + " seconds");
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

	public static class Builder<T> extends ZoteroRestRequest.BaseBuilder<GetBuilder<T>> implements GetBuilder<T>
	{
		private Integer lastVersion;
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
		public RestGetRequest<T> build()
		{
			ZoteroRestGetRequest<T> get = new ZoteroRestGetRequest<>();

			get.lastVersion = lastVersion;
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
