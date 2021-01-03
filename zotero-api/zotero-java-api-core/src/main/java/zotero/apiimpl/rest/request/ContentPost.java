package zotero.apiimpl.rest.request;

import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class ContentPost extends RestRequest<Void>
{
	private HttpEntity entity;

	public ContentPost(HttpEntity entity)
	{
		this.entity = entity;
	}

	@Override
	public HttpRequestBase prepare(String url)
	{
		HttpPost post = new HttpPost(url);
		
		post.setEntity(entity);
		
		return post;
	}
	
	protected String buildURL() throws URISyntaxException
	{
		return getApiUrl();
	}
	
	@Override
	public void addHeaders(HttpRequestBase request)
	{
		// This becomes a NOOP as there are no headers to add
	}
}
