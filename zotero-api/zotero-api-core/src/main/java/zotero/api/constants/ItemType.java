package zotero.api.constants;

public enum ItemType
{
	ARTWORK("artwork"),
	ATTACHMENT("attachment"),
	AUDIO_RECORDING("audioRecording"),
	BILL("bill"),
	BLOG_POST("blogPost"),
	BOOK("book"),
	BOOK_SECTION("bookSection"),
	CASE("case"),
	COMPUTER_PROGRAM("computerProgram"),
	CONFERENCE_PAPER("conferencePaper"),
	DICTIONARY_ENTRY("dictionaryEntry"),
	DOCUMENT("document"),
	EMAIL("email"),
	ENCYCLOPEDIA_ARTICLE("encyclopediaArticle"),
	FILM("film"),
	FORUM_POST("forumPost"),
	HEARING("hearing"),
	INSTANT_MESSAGE("instantMessage"),
	INTERVIEW("interview"),
	JOURNAL_ARTICLE("journalArticle"),
	LETTER("letter"),
	MAGAZINE_ARTICLE("magazineArticle"),
	MANUSCRIPT("manuscript"),
	MAP("map"),
	NEWSPAPER_ARTICLE("newspaperArticle"),
	NOTE("note"),
	PATENT("patent"),
	PODCAST("podcast"),
	PRESENTATION("presentation"),
	RADIO_BROADCAST("radioBroadcast"),
	REPORT("report"),
	STATUTE("statute"),
	THESIS("thesis"),
	TV_BROADCAST("tvBroadcast"),
	VIDEO_RECORDING("videoRecording"),
	WEB_PAGE("webpage");

	private String zoteroType;

	private ItemType(String zoteroType)
	{
		this.zoteroType = zoteroType;
	}
	
	public final String getZoteroType()
	{
		return zoteroType;
	}
	
	public static ItemType fromZoteroType(String zoteroType)
	{
		for(ItemType type : ItemType.values())
		{
			if(type.zoteroType.equals(zoteroType)) {
				return type;
			}
		}
		
		throw new EnumConstantNotPresentException(ItemType.class, zoteroType);
	}
}
