package zotero.api.internal.rest.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotero.api.internal.rest.RestDeleteRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.builders.DeleteBuilder;
import zotero.api.internal.rest.impl.ZoteroRestResponse.ZoteroRestResponseBuilder;

public class ZoteroRestDeleteRequest extends ZoteroRestRequest<ZoteroRestDeleteRequest> implements RestDeleteRequest
{
	private static Logger logger = LogManager.getLogger(ZoteroRestDeleteRequest.class);
	
	private static final String HEADER_IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";

	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Integer lastVersion;
	private Map<String, String> queryParams;
	private ZoteroRestResponseBuilder<Boolean> builder;

	private ZoteroRestDeleteRequest()
	{
	}
	
	@Override
	public RestResponse<Boolean> delete()
	{
		logger.debug("Entering get()");

		builder = new ZoteroRestResponse.ZoteroRestResponseBuilder<>();
		
		try
		{
			this.doGet();
		}
		catch (IOException ex)
		{
			builder.errorMessage(ex.getLocalizedMessage());
		}

		return builder.request(this).build();
	}

	private void doGet() throws IOException
	{
		HttpDelete get = new HttpDelete(this.buildURL());
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
			case HttpURLConnection.HTTP_NO_CONTENT:
			case HttpURLConnection.HTTP_OK:
			{
				builder.response(Boolean.TRUE);
				break;
			}
			
			default:
			{
				builder.response(Boolean.FALSE);
				builder.errorMessage(super.readResponse(entity));
			}
		}

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

		// TODO manage query params

		return "";
	}

	public static class Builder extends ZoteroRestRequest.BaseBuilder<DeleteBuilder> implements DeleteBuilder
	{
		private Integer lastVersion;
		private Map<String, String> queryParams;

		@Override
		public Builder lastVersion(Integer lastVersion)
		{
			this.lastVersion = lastVersion;
			return this;
		}

		@Override
		public Builder queryParam(String param, String value)
		{
			if (this.queryParams == null)
			{
				this.queryParams = new HashMap<>();
			}

			this.queryParams.put(param, value);

			return this;
		}
		
		@Override
		public RestDeleteRequest build()
		{
			ZoteroRestDeleteRequest delete = new ZoteroRestDeleteRequest();
			
			delete.lastVersion = lastVersion;
			delete.queryParams = queryParams;
			
			super.apply(delete);
			
			return delete;
		}
		
		public static DeleteBuilder createBuilder()
		{
			return new Builder();
		}

		@Override
		public DeleteBuilder itemKey(String key)
		{
			super.urlParam(ZoteroRestPaths.URL_PARAM_KEY, key);
			return this;
		}
	}
}
