package zotero.apiimpl.rest.request.builders;

import org.apache.http.entity.StringEntity;

import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.request.PatchRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class PatchBuilder<T, R extends ResponseBuilder<T>> extends ContentBuilder<T,PatchBuilder<T,R>,R>
{
	Integer lastVersion;

	private PatchBuilder()
	{
	}

	public PatchBuilder<T,R> itemKey(String key)
	{
		super.urlParam(ZoteroRestPaths.URL_PARAM_KEY, key);
		return this;
	}

	public PatchBuilder<T,R> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	@Override
	public RestRequest<T> createRequest()
	{
		PatchRequest<T> request = new PatchRequest<>();
		
		StringEntity entity = serializeJson();
		
		request.setEntity(entity);
		
		return request;
	}

	public static <T,R extends ResponseBuilder<T>> PatchBuilder<T,R> createBuilder(R responseBuilder)
	{
		return new PatchBuilder<T,R>().responseBuilder(responseBuilder);
	}
}