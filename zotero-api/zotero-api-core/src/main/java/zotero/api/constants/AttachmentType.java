package zotero.api.constants;

public enum AttachmentType implements ZoteroEnum
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

	private AttachmentType(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	@Override
	public String getZoteroName()
	{
		return zoteroName;
	}
	
	public static AttachmentType fromZoteroType(String zoteroType)
	{
		for(AttachmentType type : AttachmentType.values())
		{
			if(type.zoteroName.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(AttachmentType.class, zoteroType);
	}
}
