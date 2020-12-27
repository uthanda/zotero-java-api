package zotero.apiimpl.rest.request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

public class DeleteRequest<T> extends RestRequest<T>
{
	@Override
	public HttpRequestBase prepare(String url)
	{
		return new HttpDelete(url);
	}
}
