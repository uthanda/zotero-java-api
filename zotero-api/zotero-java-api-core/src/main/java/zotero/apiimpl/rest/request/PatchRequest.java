package zotero.apiimpl.rest.request;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpRequestBase;

public class PatchRequest<T> extends ContentRequest<T>
{
	@Override
	public HttpRequestBase prepare(String url)
	{
		HttpPatch patch = new HttpPatch(url);
		
		super.addWriteToken(patch);
		
		patch.setEntity(getEntity());
		
		return patch;
	}
}
