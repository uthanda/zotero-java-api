package zotero.api.constants;

public enum TagType
{
	/**
	 * Tags applied automatically by import or other means
	 */
	SHARED(1),
	/**
	 * Tags created by users
	 */
	CUSTOM(0);

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
				return TagType.SHARED;
			case 0:
				return TagType.CUSTOM;
			default:
				throw new EnumConstantNotPresentException(RelationshipType.class, Integer.toString(zoteroType));
		}

	}
}
