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

	private final int type;

	TagType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static TagType fromType(int type)
	{
		switch (type)
		{
			case 1:
				return TagType.AUTOMATIC;
			case 0:
				return TagType.USER;
			default:
				throw new EnumConstantNotPresentException(RelationshipType.class, Integer.toString(type));
		}

	}
}
