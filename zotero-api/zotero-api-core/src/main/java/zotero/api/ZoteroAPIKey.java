package zotero.api;

public class ZoteroAPIKey
{
	private final String apiKey;
	
	public ZoteroAPIKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public final String getApiKey()
	{
		return apiKey;
	}
}
