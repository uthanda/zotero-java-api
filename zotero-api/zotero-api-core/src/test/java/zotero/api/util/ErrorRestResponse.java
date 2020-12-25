package zotero.api.util;

import java.io.IOException;

import zotero.api.internal.rest.RestResponse;

final class ErrorRestResponse<T> implements RestResponse<T>
{
	private String message;

	public ErrorRestResponse(String message)
	{
		this.message = message;
	}

	@Override
	public boolean wasSuccessful()
	{
		return false;
	}

	@Override
	public String getErrorMessage()
	{
		return message;
	}

	@Override
	public T getResponse()
	{
		return null;
	}

	@Override
	public String getLink(String type)
	{
		return null;
	}

	@Override
	public Integer getTotalResults()
	{
		return null;
	}

	@Override
	public Integer getLastModifyVersion()
	{
		return null;
	}
}