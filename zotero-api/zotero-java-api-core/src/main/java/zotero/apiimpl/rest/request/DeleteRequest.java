package zotero.apiimpl.rest.request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import zotero.apiimpl.rest.ZoteroRest;

public class DeleteRequest<T> extends RestRequest<T>
{
	@Override
	public HttpRequestBase prepare(String url)
	{
		return new HttpDelete(url);
	}
	
	@Override
	public void addHeaders(HttpRequestBase request)
	{
		super.addHeaders(request);
		
		request.addHeader(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION, super.getLastVersion().toString());
	}
}
