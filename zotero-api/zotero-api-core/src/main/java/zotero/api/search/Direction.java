package zotero.api.search;

import zotero.api.constants.ZoteroEnum;

public enum Direction implements ZoteroEnum
{
	ASCENDING("asc"),
	DESCENDING("desc");

	private final String zoteroName;

	private Direction(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	@Override
	public String getZoteroName()
	{
		return zoteroName;
	}
}
