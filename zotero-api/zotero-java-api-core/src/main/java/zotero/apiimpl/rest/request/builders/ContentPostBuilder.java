package zotero.apiimpl.rest.request.builders;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;

import zotero.apiimpl.rest.request.ContentPost;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

public class ContentPostBuilder extends BaseBuilder<Void, ContentPostBuilder, SuccessResponseBuilder>
{
	private HttpEntity entity;
	
	public ContentPostBuilder entity(HttpEntity entity)
	{
		this.entity = entity;
		this.responseBuilder(new SuccessResponseBuilder());
		return this;
	}

	@Override
	public RestRequest<Void> createRequest() throws UnsupportedEncodingException
	{
		return new ContentPost(entity);
	}

	public static ContentPostBuilder createBuilder()
	{
		return new ContentPostBuilder();
	}
}
