package zotero.apiimpl.rest.request;

import org.apache.http.HttpEntity;


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
}
