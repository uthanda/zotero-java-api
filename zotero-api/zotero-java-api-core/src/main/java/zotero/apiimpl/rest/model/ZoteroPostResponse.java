package zotero.apiimpl.rest.model;

import java.util.Map;

public class ZoteroPostResponse
{
	private Map<String, ZoteroRestItem> successful;
	private Map<String, String> success;
	private Map<String, ZoteroFailedItem> failed;
	private Map<String, String> unchanged;

	public Map<String, String> getUnchanged()
	{
		return unchanged;
	}

	public void setUnchanged(Map<String, String> unchanged)
	{
		this.unchanged = unchanged;
	}

	public Map<String, ZoteroRestItem> getSuccessful()
	{
		return successful;
	}

	public void setSuccessful(Map<String, ZoteroRestItem> successful)
	{
		this.successful = successful;
	}

	public Map<String, String> getSuccess()
	{
		return success;
	}

	public void setSuccess(Map<String, String> success)
	{
		this.success = success;
	}

	public Map<String, ZoteroFailedItem> getFailed()
	{
		return failed;
	}

	public void setFailed(Map<String, ZoteroFailedItem> failed)
	{
		this.failed = failed;
	}
}
