package zotero.api.auth;

import org.apache.http.HttpRequest;

import zotero.apiimpl.rest.ZoteroRest;

/**
 * A Zotero API key is a wrapper around the API key that is generated via the
 * web interface. It currently provides the only authentication mechanism for
 * the Zotero API.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public class ZoteroAPIKey implements ZoteroAuth
{
	private final String apiKey;

	/**
	 * Creates a new API key with the provided key.
	 * 
	 * @param apiKey API key for accessing the REST API.
	 */
	public ZoteroAPIKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	/**
	 * Gets the API key
	 * 
	 * @return API key
	 */
	public final String getApiKey()
	{
		return apiKey;
	}

	@Override
	public void applyHeaders(HttpRequest request)
	{
		request.addHeader(ZoteroRest.Headers.ZOTERO_API_KEY, apiKey);
	}
}
