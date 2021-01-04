package zotero.api.constants;

public enum TagType
{
	/**
	 * Tags applied automatically by import or other means
	 */
	AUTOMATIC(1),
	/**
	 * Tags created by users
	 */
	USER(0);

	private final int zoteroType;

	TagType(int type)
	{
		this.zoteroType = type;
	}

	public int getZoteroType()
	{
		return zoteroType;
	}

	public static TagType fromType(int zoteroType)
	{
		switch (zoteroType)
		{
			case 1:
				return TagType.AUTOMATIC;
			case 0:
				return TagType.USER;
			default:
				throw new EnumConstantNotPresentException(RelationshipType.class, Integer.toString(zoteroType));
		}

	}
}
