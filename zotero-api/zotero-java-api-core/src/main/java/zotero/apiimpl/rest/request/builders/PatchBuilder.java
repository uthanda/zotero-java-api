package zotero.apiimpl.rest.request.builders;

import static zotero.apiimpl.rest.ZoteroRest.URLParameter.KEY;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;

import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.request.PatchRequest;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

public class PatchBuilder<T, R extends ResponseBuilder<T>> extends ContentBuilder<T, PatchBuilder<T, R>, R>
{
	Integer lastVersion;

	private PatchBuilder()
	{
	}

	public PatchBuilder<T, R> itemKey(String key)
	{
		super.urlParam(KEY, key);
		return this;
	}

	public PatchBuilder<T, R> lastVersion(Integer lastVersion)
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

		if (lastVersion == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, "Last version must be specified for a PATCH request");
		}

		Map<String, String> headers = new HashMap<>();
		headers.put(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION, lastVersion.toString());

		request.setHeaders(headers);

		return request;
	}

	public static <T, R extends ResponseBuilder<T>> PatchBuilder<T, R> createBuilder(R responseBuilder)
	{
		return new PatchBuilder<T, R>().responseBuilder(responseBuilder);
	}
}