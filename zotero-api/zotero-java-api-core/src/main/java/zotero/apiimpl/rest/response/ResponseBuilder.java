package zotero.apiimpl.rest.response;

import static zotero.apiimpl.rest.ZoteroRest.Headers.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

public abstract class ResponseBuilder<T>
{
	protected RestResponse<T> response = new RestResponse<>();

	public abstract ResponseBuilder<T> entity(HttpEntity entity) throws UnsupportedOperationException, IOException;

	public abstract void noEntity();

	public ResponseBuilder<T> addLink(String type, String link)
	{
		if (response.links == null)
		{
			response.links = new HashMap<>();
		}

		response.links.put(type, link);
		return this;
	}

	public ResponseBuilder<T> totalResults(Integer totalResults)
	{
		response.totalResults = totalResults;
		return this;
	}

	public ResponseBuilder<T> lastModifyVersion(Integer lastModifyVersion)
	{
		response.lastModifyVersion = lastModifyVersion;
		return this;
	}

	public ResponseBuilder<T> errorMessage(String errorMessage)
	{
		response.errorMessage = errorMessage;
		return this;
	}

	public RestResponse<T> build()
	{
		return response;
	}

	public void response(CloseableHttpResponse response) throws UnsupportedOperationException, IOException
	{
		// Parse the various elements
		parseTotalResultsHeader(response);
		parseLastVersionHeader(response);
		parseLinks(response);

		HttpEntity entity = response.getEntity();

		switch (response.getStatusLine().getStatusCode())
		{
			case HttpURLConnection.HTTP_OK:
			{
				entity(entity);
				break;
			}

			case HttpURLConnection.HTTP_NO_CONTENT:
			{
				entity(null);
				break;
			}

			case 429:
			{
				errorMessage("Too many requests.  Please retry after " + response.getFirstHeader(RETRY_AFTER) + " seconds");
				break;
			}

			default:
			{
				errorMessage(readResponse(entity));
			}
		}
	}

	private void parseLinks(CloseableHttpResponse response)
	{
		Header header;
		header = response.getFirstHeader(LINK);
		if (header != null && header.getValue() != null)
		{
			String[] links = header.getValue().split(",");
			for (int i = 0; i < links.length; i++)
			{
				String[] elements = links[i].split(";");
				elements[1] = elements[1].trim();
				String type = elements[1].substring(5, elements[1].length() - 1);
				String link = elements[0].replace("<", "").replace(">", "");

				addLink(type, link);
			}
		}
	}

	private void parseLastVersionHeader(CloseableHttpResponse response)
	{
		Header header;
		header = response.getFirstHeader(LAST_MODIFIED_VERSION);
		if (header != null && header.getValue() != null)
		{
			lastModifyVersion(Integer.valueOf(header.getValue()));
		}
	}

	private void parseTotalResultsHeader(CloseableHttpResponse response)
	{
		Header header = response.getFirstHeader(TOTAL_RESULTS);
		if (header != null && header.getValue() != null)
		{
			totalResults(Integer.valueOf(header.getValue()));
		}
	}

	private String readResponse(HttpEntity entity) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		BufferedInputStream is = new BufferedInputStream(entity.getContent());

		IOUtils.copy(is, bos);

		is.close();

		return new String(bos.toByteArray());
	}
}