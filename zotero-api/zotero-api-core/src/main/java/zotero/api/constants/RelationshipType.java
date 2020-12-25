package zotero.api.constants;

/**
 *
 * @author stran
 */
public enum RelationshipType implements ZoteroEnum
{
	/**
	 * 
	 */
	DC_REPLACES("dc:replaces");

	private final String zoteroName;

	RelationshipType(String zoteroName)
	{
		this.zoteroName = zoteroName;
	}

	public String getZoteroName()
	{
		return zoteroName;
	}

	public static RelationshipType fromZoteroType(String zoteroType)
	{
		for(RelationshipType type : RelationshipType.values())
		{
			if(type.zoteroName.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(RelationshipType.class, zoteroType);
	}
}
