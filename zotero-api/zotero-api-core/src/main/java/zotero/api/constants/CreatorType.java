package zotero.api.constants;

public enum CreatorType
{
	ARTIST("artist"),
	ATTORNEY_AGENT("attorneyAgent"),
	AUTHOR("author"),
	BOOK_AUTHOR("bookAuthor"),
	CARTOGRAPHER("cartographer"),
	CAST_MEMBER("castMember"),
	COMMENTER("commenter"),
	COMPOSER("composer"),
	CONTRIBUTOR("contributor"),
	COSPONSOR("cosponsor"),
	COUNSEL("counsel"),
	DIRECTOR("director"),
	EDITOR("editor"),
	GUEST("guest"),
	INTERVIEW_WITH("interviewee"),
	INTERVIEWER("interviewer"),
	INVENTOR("inventor"),
	PERFORMER("performer"),
	PODCASTER("podcaster"),
	PRESENTER("presenter"),
	PRODUCER("producer"),
	PROGRAMMER("programmer"),
	RECIPIENT("recipient"),
	REVIEWED_AUTHOR("reviewedAuthor"),
	SCRIPTWRITER("scriptwriter"),
	SERIES_EDITOR("seriesEditor"),
	SPONSOR("sponsor"),
	TRANSLATOR("translator"),
	WORDS_BY("wordsBy");

	private final String zoteroName;

	private CreatorType(String name)
	{
		this.zoteroName = name;
	}
	
	public static CreatorType fromZoteroType(String zoteroType)
	{
		for(CreatorType type : CreatorType.values())
		{
			if(type.zoteroName.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(CreatorType.class, zoteroType);
	}

	public String getZoteroName()
	{
		return zoteroName;
	}
}
