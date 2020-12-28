package zotero.apiimpl.rest.request;

import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class GetRequest<T> extends RestRequest<T>
{
	private String specialUrl;

	@Override
	public HttpRequestBase prepare(String url)
	{
		return new HttpGet(url);
	}

	public void setSpecialUrl(String specialUrl)
	{
		this.specialUrl = specialUrl;
	}
	
	@Override
	String buildURL() throws URISyntaxException
	{
		return specialUrl != null ? specialUrl : super.buildURL();
	}
}
