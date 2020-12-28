package zotero.api.search;

import zotero.api.constants.ZoteroEnum;

public enum QuickSearchMode implements ZoteroEnum
{
	TITLE_CREATOR_YEAR("titleCreatorYear"),
	EVERYTHING("everything");
	
	private final String zoteroName;
	
	private QuickSearchMode(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	@Override
	public String getZoteroName()
	{
		return zoteroName;
	}
}
