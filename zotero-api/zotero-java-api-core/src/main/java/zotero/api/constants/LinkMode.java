package zotero.api.constants;

public enum LinkMode implements ZoteroEnum
{
	/**
	 * 
	 */
	IMPORTED_FILE("imported_file"),
	/**
	* 
	*/
	IMPORTED_URL("imported_url"),
	/**
	* 
	*/
	LINKED_FILE("linked_file"),
	/**
	* 
	*/
	LINKED_URL("linked_url");

	private final String zoteroName;

	private LinkMode(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	@Override
	public String getZoteroName()
	{
		return zoteroName;
	}
	
	public static LinkMode fromZoteroType(String zoteroType)
	{
		for(LinkMode type : LinkMode.values())
		{
			if(type.zoteroName.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(LinkMode.class, zoteroType);
	}
}
