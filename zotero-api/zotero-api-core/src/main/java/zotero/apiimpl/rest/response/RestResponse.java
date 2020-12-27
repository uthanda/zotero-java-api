package zotero.apiimpl.rest.response;

import java.util.Map;

public class RestResponse<T>
{
	Integer totalResults;
	Integer lastModifyVersion;
	T response;
	Map<String, String> links;
	String errorMessage;

	public final boolean wasSuccessful()
	{
		return errorMessage == null;
	}

	public final String getErrorMessage()
	{
		return errorMessage;
	}

	public T getResponse()
	{
		return response;
	}

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

	public final Integer getTotalResults()
	{
		return totalResults;
	}

	public final Integer getLastModifyVersion()
	{
		return lastModifyVersion;
	}
}
