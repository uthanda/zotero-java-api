package zotero.apiimpl.rest.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

import zotero.apiimpl.rest.RestPatchRequest;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.builders.PatchBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestResponse.ZoteroRestResponseBuilder;

public class ZoteroRestPatchRequest extends ZoteroRestRequest implements RestPatchRequest
{
	private Gson gson = new Gson();
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Object content;
	private Integer versionNumber;

	@Override
	public RestResponse<Boolean> execute()
	{
		ZoteroRestResponseBuilder<Boolean> builder = new ZoteroRestResponse.ZoteroRestResponseBuilder<>();

		try
		{
			doPatch(content, builder);
		}
		catch (Exception e)
		{
			builder.errorMessage(e.getLocalizedMessage());
		}

		return builder.build();
	}

	private void doPatch(Object object, ZoteroRestResponseBuilder<Boolean> builder) throws IOException, URISyntaxException
	{
		HttpPatch post = new HttpPatch(super.buildURL());
		super.addHeaders(post);

		if (versionNumber != null)
		{
			post.addHeader(HEADER_IF_UNMODIFIED_SINCE_VERSION, versionNumber.toString());
		}
		else
		{
			addWriteToken(post);
		}

		String json = gson.toJson(object);

		StringEntity entity = new StringEntity(json);
		entity.setContentType("application/json");

		post.setEntity(entity);

		CloseableHttpResponse response = httpClient.execute(post);

		switch (response.getStatusLine().getStatusCode())
		{
			case HttpURLConnection.HTTP_NO_CONTENT:
			{
				builder.response(true);
				builder.lastModifyVersion(Integer.valueOf(response.getFirstHeader(ZoteroRestResponse.HEADER_LAST_MODIFIED_VERSION).getValue()));
				break;
			}

			case 429:
			{
				builder.errorMessage("Too many requests.  Please retry after " + response.getFirstHeader("Retry-After") + " seconds");
				break;
			}

			default:
			{
				String message = super.readResponse(response.getEntity());
				builder.errorMessage(message);
			}
		}
	}

	public static class Builder extends ZoteroRestRequest.BaseBuilder<PatchBuilder> implements PatchBuilder
	{
		private Object content;
		private Integer versionNumber;

		private Builder()
		{
		}

		public PatchBuilder content(Object content)
		{
			this.content = content;
			return this;
		}

		public RestPatchRequest build()
		{
			ZoteroRestPatchRequest request = new ZoteroRestPatchRequest();

			super.apply(request);

			request.content = content;
			request.versionNumber = versionNumber;

			return request;
		}

		public static PatchBuilder createBuilder()
		{
			return new Builder();
		}

		@Override
		public PatchBuilder itemKey(String key)
		{
			super.urlParam(ZoteroRestPaths.URL_PARAM_KEY, key);
			return this;
		}

		@Override
		public PatchBuilder versionNumber(Integer versionNumber)
		{
			this.versionNumber = versionNumber;
			return this;
		}
	}
}
