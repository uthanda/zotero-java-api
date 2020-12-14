package zotero.api.internal.rest;

import java.util.HashMap;
import java.util.Map;

public class ZoteroAPIResponse<T> implements RestResponse<T>
{
	private Integer totalResults;
	private Integer lastModifyVersion;
	private T response;
	private Map<String,String> links;
	private String errorMessage;
	private ZoteroGetUserAPIRequest<T> request;
	
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
	public boolean hasNext()
	{
		return links.containsKey("next");
	}
	
	@Override
	public RestResponse<T> next()
	{
		return request.next(links.get("next"));
	}
	
	@Override
	public String getLink(String type)
	{
		if(links == null) {
			return null;
		} else {
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

	public static class ZoteroGetAPIResponseBuilder<T>
	{
		private ZoteroAPIResponse<T> response = new ZoteroAPIResponse<>();
		
		public ZoteroGetAPIResponseBuilder<T> response(T response)
		{
			this.response.response = response;
			return this;
		}
		
		public ZoteroGetAPIResponseBuilder<T> addLink(String type, String link)
		{
			if(response.links == null) {
				response.links = new HashMap<>();
			}
			
			response.links.put(type, link);
			return this;
		}
		
		public ZoteroGetAPIResponseBuilder<T> totalResults(Integer totalResults)
		{
			response.totalResults = totalResults;
			return this;
		}
		
		public ZoteroGetAPIResponseBuilder<T> lastModifyVersion(Integer lastModifyVersion)
		{
			response.lastModifyVersion = lastModifyVersion;
			return this;
		}
		
		public ZoteroGetAPIResponseBuilder<T> errorMessage(String errorMessage)
		{
			response.errorMessage = errorMessage;
			return this;
		}
		
		public ZoteroGetAPIResponseBuilder<T> request(ZoteroGetUserAPIRequest<T> request)
		{
			response.request = request;
			return this;
		}
		
		public ZoteroAPIResponse<T> build()
		{
			return response;
		}
	}
}
