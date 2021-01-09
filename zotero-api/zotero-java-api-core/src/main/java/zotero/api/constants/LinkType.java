package zotero.api.constants;

/**
 * @author Michael Oland
 * @since 1.0
 */
public enum LinkType implements ZoteroEnum
{
	/**
	 * A link to the given object via the Zotero REST API
	 */
	SELF("self"),
	/**
	 * A web url for the same object
	 */
	ALTERNATE("alternate"),
	/**
	 * A link to the enclosure
	 */
	ENCLOSURE("enclosure"),
	/**
	 * A link to the content attachment
	 */
	ATTACHMENT("attachment"),
	/**
	 * A link to the parent object
	 */
	UP("up");

	private String zoteroName;
	
	LinkType(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	public String getZoteroName()
	{
		return zoteroName;
	}

	public static LinkType fromZoteroType(String zoteroType)
	{
		for(LinkType type : LinkType.values())
		{
			if(type.zoteroName.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(LinkType.class, zoteroType);
	}
}
