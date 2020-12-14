package zotero.api.internal.rest;

import java.io.IOException;

public interface RestResponse<T>
{
	boolean wasSuccessful();

	String getErrorMessage();

	T getResponse();

	boolean hasNext();

	RestResponse<T> next() throws IOException;

	String getLink(String type);

	Integer getTotalResults();

	Integer getLastModifyVersion();
}