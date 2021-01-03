package zotero.apiimpl.rest.request.builders;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zotero.api.ZoteroAPIKey;
import zotero.api.ZoteroAuth;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.request.RestRequest;
import zotero.apiimpl.rest.response.ResponseBuilder;

@SuppressWarnings("unchecked")
public abstract class BaseBuilder<T, B extends BaseBuilder<T, B, R>, R extends ResponseBuilder<T>>
{
	private boolean isUser;
	private ZoteroAuth auth;
	private String apiUrl;
	private Map<URLParameter, String> urlParams;
	private String id;
	private Map<String, List<String>> queryParams;
	private R responseBuilder;

	public B responseBuilder(R responseBuilder)
	{
		this.responseBuilder = responseBuilder;
		return (B) this;
	}

	public B id(String id)
	{
		this.id = id;
		return (B) this;
	}

	public B setUsers()
	{
		this.isUser = true;
		return (B) this;
	}

	public B setGroups()
	{
		this.isUser = false;
		return (B) this;
	}

	public B auth(ZoteroAuth auth)
	{
		this.auth = auth;
		return (B) this;
	}

	public B url(String url)
	{
		this.apiUrl = url;
		return (B) this;
	}

	public B urlParam(URLParameter param, String value)
	{
		if (urlParams == null)
		{
			urlParams = new EnumMap<>(URLParameter.class);
		}

		urlParams.put(param, value);

		return (B) this;
	}

	public RestRequest<T> build()
	{
		RestRequest<T> req;
		try
		{
			req = createRequest();
		}
		catch (UnsupportedEncodingException ex)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, ex.getLocalizedMessage(), ex);
		}

		req.setAuth(auth);
		req.setUser(isUser);
		req.setId(id);
		req.setUrlParams(urlParams);
		req.setApiUrl(apiUrl);
		req.setQueryParams(queryParams);
		req.setResponseBuilder(responseBuilder);

		return req;
	}

	public abstract RestRequest<T> createRequest() throws UnsupportedEncodingException;

	public B queryParam(String param, String value)
	{
		if (this.queryParams == null)
		{
			this.queryParams = new HashMap<>();
		}

		this.queryParams.computeIfAbsent(param, name -> new ArrayList<>());

		this.queryParams.get(param).add(value);

		return (B) this;
	}
}