package zotero.api.internal.rest.impl;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

import zotero.api.internal.rest.RestPutRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.builders.PutBuilder;
import zotero.api.internal.rest.impl.ZoteroRestResponse.ZoteroRestResponseBuilder;

public class ZoteroRestPutRequest extends ZoteroRestRequest<ZoteroRestPutRequest> implements RestPutRequest
{
	private Gson gson = new Gson();
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private Object content;

	@Override
	public RestResponse<Boolean> post()
	{
		ZoteroRestResponseBuilder<Boolean> builder = new ZoteroRestResponse.ZoteroRestResponseBuilder<>();
		
		try
		{
			doPost(content, builder);
		}
		catch (Exception e)
		{
			builder.errorMessage(e.getLocalizedMessage());
		}
		
		return builder.build();
	}

	private void doPost(Object object, ZoteroRestResponseBuilder<Boolean> builder) throws IOException
	{
		HttpPost post = new HttpPost(super.buildURL());
		super.addHeaders(post);
		
		String json = gson.toJson(object);
		
		StringEntity entity = new StringEntity(json);
		entity.setContentType("application/json");
		
		post.setEntity(entity);
		
		CloseableHttpResponse response = httpClient.execute(post);
		
		switch(response.getStatusLine().getStatusCode())
		{
			case HttpURLConnection.HTTP_NO_CONTENT:
			{
				builder.response(true);
				builder.lastModifyVersion(Integer.valueOf(response.getFirstHeader(ZoteroRestResponse.HEADER_LAST_MODIFIED_VERSION).getValue()));
				break;
			}
			
			default:
			{
				String message = super.readResponse(response.getEntity());
				builder.errorMessage(message);
			}
		}
	}
	
	public static class Builder extends ZoteroRestRequest.BaseBuilder<PutBuilder> implements PutBuilder
	{
		private Object content;
		
		private Builder()
		{
		}
		
		public PutBuilder content(Object content)
		{
			this.content = content;
			return this;
		}
		
		public RestPutRequest build()
		{
			ZoteroRestPutRequest request = new ZoteroRestPutRequest();
			request.content = content;
			
			super.apply(request);
			
			return request;
		}
		
		public static PutBuilder createBuilder()
		{
			return new Builder();
		}
	}
}
