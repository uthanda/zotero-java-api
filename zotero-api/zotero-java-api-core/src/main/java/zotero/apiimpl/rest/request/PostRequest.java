package zotero.apiimpl.rest.request;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class PostRequest<T> extends ContentRequest<T>
{
	@Override
	public HttpRequestBase prepare(String url)
	{
		HttpPost post = new HttpPost(url);
		
		post.setEntity(getEntity());

		return post;
	}
}
