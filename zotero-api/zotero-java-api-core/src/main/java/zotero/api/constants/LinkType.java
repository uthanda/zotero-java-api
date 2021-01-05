package zotero.api.constants;

public enum LinkType implements ZoteroEnum
{
	SELF("self"),
	ALTERNATE("alternate"),
	ENCLOSURE("enclosure"),
	ATTACHMENT("attachment"),
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
