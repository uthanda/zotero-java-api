package zotero.api.constants;

public enum Sort implements ZoteroEnum
{
	DATE_ADDED("dateAdded"),
	DATE_MODIFIED("dateModified"),
	TITLE("title"),
	CREATOR("creator"),
	ITEM_TYPE("itemType"),
	DATE("date"),
	PUBLISHER("publisher"),
	PUBLICATION_TITLE("publicationTitle"),
	JOURNAL_ABBREVIATION("journalAbbreviation"),
	LANGAUGE("language"),
	ACCESS_DATE("accessDate"),
	LIBRARY_CATALOG("libraryCatalog"),
	CALL_NUMBER("callNumber"),
	RIGHTS("rights"),
	ADDED_BY("addedBy"),
	/**
	 * Not valid for Collection searches
	 */
	NUM_ITEMS("numItems");

	private final String zoteroName;

	private Sort(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	@Override
	public String getZoteroName()
	{
		return zoteroName;
	}
}
