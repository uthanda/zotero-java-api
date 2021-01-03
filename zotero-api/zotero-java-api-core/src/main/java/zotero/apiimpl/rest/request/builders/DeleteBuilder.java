package zotero.apiimpl.rest.request.builders;

import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.request.DeleteRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class DeleteBuilder<T, R extends ResponseBuilder<T>> extends BaseBuilder<T, DeleteBuilder<T, R>, R>
{
	private Integer lastVersion;

	private DeleteBuilder()
	{
	}

	public DeleteBuilder<T, R> lastVersion(Integer lastVersion)
	{
		this.lastVersion = lastVersion;
		return this;
	}

	public static <T, R extends ResponseBuilder<T>> DeleteBuilder<T, R> createBuilder(R responseBuilder)
	{
		return new DeleteBuilder<T, R>().responseBuilder(responseBuilder);
	}

	public DeleteBuilder<T, R> itemKey(String key)
	{
		super.urlParam(URLParameter.ITEM_KEY, key);
		return this;
	}

	public DeleteBuilder<T, R> collectionKey(String key)
	{
		super.urlParam(URLParameter.COLLECTION_KEY, key);
		return this;
	}

	@Override
	public RestRequest<T> createRequest()
	{
		DeleteRequest<T> request = new DeleteRequest<>();

		if (lastVersion == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, "Last version must be specified for a DELETE request");
		}

		this.header(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION, lastVersion.toString());

		return request;
	}
}