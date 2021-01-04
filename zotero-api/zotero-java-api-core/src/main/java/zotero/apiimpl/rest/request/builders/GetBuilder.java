package zotero.apiimpl.rest.request.builders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.request.GetRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class GetBuilder<T, R extends ResponseBuilder<T>> extends BaseBuilder<T, GetBuilder<T, R>, R>
{
	private Integer lastVersion;
	private String specialUrl;

	public GetBuilder<T, R> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	public GetBuilder<T, R> specialUrl(String url)
	{
		this.specialUrl = url;
		return this;
	}

	public static <T, R extends ResponseBuilder<T>> GetBuilder<T, R> createBuilder(R builder)
	{
		return new GetBuilder<T, R>().responseBuilder(builder);
	}

	@Override
	public RestRequest<T> createRequest()
	{
		GetRequest<T> request = new GetRequest<>();

		request.setSpecialUrl(specialUrl);

		Map<String, List<String>> headers = new HashMap<>();

		if (lastVersion != null)
		{
			headers.put(ZoteroRest.Headers.IF_MODIFIED_SINCE_VERSION, Arrays.asList(lastVersion.toString()));
		}

		request.setHeaders(headers);

		return request;
	}

	public GetBuilder<T, R> entryUrl(String path)
	{
		this.specialUrl = "https://" + ZoteroRest.API.HOST + path;
		return this;
	}
}