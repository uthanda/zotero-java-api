package zotero.apiimpl.rest.request;

import static zotero.apiimpl.rest.ZoteroRest.Headers.ZOTERO_WRITE_TOKEN;

import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;


public abstract class ContentRequest<T> extends RestRequest<T>
{
	private HttpEntity entity;
	
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public HttpEntity getEntity()
	{
		return entity;
	}

	protected void addWriteToken(HttpRequest request)
	{
		request.addHeader(ZOTERO_WRITE_TOKEN, UUID.randomUUID().toString().replace("-", ""));
	}
}
