package zotero.apiimpl.rest;

/**
 * Represents a response from the Zotero REST api.
 * 
 * @author stran
 *
 * @param <T> Response payload type
 */
public interface RestResponse<T>
{
	/**
	 * Gets whether the request was successful
	 * 
	 * @return True if successful
	 */
	boolean wasSuccessful();

	/**
	 * Gets the error message provided by the call.
	 * 
	 * @return Error message
	 */
	String getErrorMessage();

	/**
	 * Gets the response
	 * 
	 * @return Response
	 */
	T getResponse();

	String getLink(String type);

	Integer getTotalResults();

	Integer getLastModifyVersion();
}