package zotero.apiimpl.rest.impl;

import java.util.HashMap;
import java.util.Map;

import zotero.apiimpl.rest.RestResponse;

public class ZoteroRestResponse<T> implements RestResponse<T>
{
	public static final String HEADER_LAST_MODIFIED_VERSION = "Last-Modified-Version";

	private Integer totalResults;
	private Integer lastModifyVersion;
	private T response;
	private Map<String, String> links;
	private String errorMessage;

	@Override
	public final boolean wasSuccessful()
	{
		return errorMessage == null;
	}

	@Override
	public final String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public T getResponse()
	{
		return response;
	}

	@Override
	public String getLink(String type)
	{
		if (links == null)
		{
			return null;
		}
		else
		{
			return links.get(type);
		}
	}

	@Override
	public final Integer getTotalResults()
	{
		return totalResults;
	}

	@Override
	public final Integer getLastModifyVersion()
	{
		return lastModifyVersion;
	}

	public static class ZoteroRestResponseBuilder<T>
	{
		private ZoteroRestResponse<T> response = new ZoteroRestResponse<>();

		public ZoteroRestResponseBuilder<T> response(T response)
		{
			this.response.response = response;
			return this;
		}

		public ZoteroRestResponseBuilder<T> addLink(String type, String link)
		{
			if (response.links == null)
			{
				response.links = new HashMap<>();
			}

			response.links.put(type, link);
			return this;
		}

		public ZoteroRestResponseBuilder<T> totalResults(Integer totalResults)
		{
			response.totalResults = totalResults;
			return this;
		}

		public ZoteroRestResponseBuilder<T> lastModifyVersion(Integer lastModifyVersion)
		{
			response.lastModifyVersion = lastModifyVersion;
			return this;
		}

		public ZoteroRestResponseBuilder<T> errorMessage(String errorMessage)
		{
			response.errorMessage = errorMessage;
			return this;
		}

		public ZoteroRestResponse<T> build()
		{
			return response;
		}
	}
}
