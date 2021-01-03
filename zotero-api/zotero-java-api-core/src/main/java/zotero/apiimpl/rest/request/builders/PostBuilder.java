package zotero.apiimpl.rest.request.builders;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import zotero.apiimpl.rest.request.PostRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class PostBuilder<T, R extends ResponseBuilder<T>> extends ContentBuilder<T, PostBuilder<T, R>, R>
{
	private List<NameValuePair> formParams;
	private String contentType;
	private InputStream is;

	private PostBuilder()
	{
	}

	public PostBuilder<T, R> inputStream(String contentType, InputStream is)
	{
		if (formParams != null || getJsonObject() != null)
		{
			throw new IllegalStateException("Cannot have both input stream content and form params/content");
		}

		this.is = is;
		this.contentType = contentType;
		return this;
	}

	public static <T, R extends ResponseBuilder<T>> PostBuilder<T, R> createBuilder(R builder)
	{
		return new PostBuilder<T, R>().responseBuilder(builder);
	}

	public PostBuilder<T, R> formParam(String key, String value)
	{
		if (getJsonObject() != null || is != null)
		{
			throw new IllegalStateException("Cannot have both content and form params");
		}

		if (formParams == null)
		{
			formParams = new ArrayList<>();
		}

		formParams.add(new BasicNameValuePair(key, value));

		return this;
	}

	@Override
	public RestRequest<T> createRequest() throws UnsupportedEncodingException
	{
		PostRequest<T> post = new PostRequest<>();

		HttpEntity entity;

		if (getJsonObject() != null)
		{
			entity = super.serializeJson();
		}
		else if (formParams != null)
		{
			entity = new UrlEncodedFormEntity(this.formParams);
		}
		else if (is != null)
		{
			entity = new org.apache.http.entity.InputStreamEntity(is, ContentType.create(contentType));
		}
		else
		{
			throw new IllegalStateException("FormParams, JSONContent or InputStream must be set");
		}

		post.setEntity(entity);

		return post;
	}
}