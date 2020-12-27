package zotero.apiimpl.rest.request.builders;

import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.request.DeleteRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class DeleteBuilder<T,R extends ResponseBuilder<T>> extends BaseBuilder<T, DeleteBuilder<T,R>,R>
{
	private Integer lastVersion;

	private DeleteBuilder() {}
	
	public DeleteBuilder<T,R> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	public static <T,R extends ResponseBuilder<T>> DeleteBuilder<T,R> createBuilder(R responseBuilder)
	{
		return new DeleteBuilder<T,R>().responseBuilder(responseBuilder);
	}

	public DeleteBuilder<T,R> itemKey(String key)
	{
		super.urlParam(ZoteroRestPaths.URL_PARAM_KEY, key);
		return this;
	}

	@Override
	public RestRequest<T> createRequest()
	{
		DeleteRequest<T> request = new DeleteRequest<>();
		request.setLastVersion(lastVersion);
		
		return request;
	}
}