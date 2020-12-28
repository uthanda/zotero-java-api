package zotero.apiimpl.rest.request.builders;

import zotero.apiimpl.rest.request.GetRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class GetBuilder<T, R extends ResponseBuilder<T>> extends BaseBuilder<T,GetBuilder<T,R>,R>
{
	private Integer lastVersion;
	private String specialUrl;

	public GetBuilder<T,R> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	public GetBuilder<T,R> specialUrl(String url)
	{
		this.specialUrl = url;
		return this;
	}

	public static <T, R extends ResponseBuilder<T>> GetBuilder<T,R> createBuilder(R builder)
	{
		return new GetBuilder<T,R>().responseBuilder(builder);
	}

	@Override
	public RestRequest<T> createRequest()
	{
		GetRequest<T> request = new GetRequest<>();
		
		request.setSpecialUrl(specialUrl);
		request.setLastVersion(lastVersion);
		
		return request;
	}
}