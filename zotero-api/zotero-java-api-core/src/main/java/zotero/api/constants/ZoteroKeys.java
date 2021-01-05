package zotero.api.constants;

/**
 * Known property/key names.
 *
 * @author Michael Oland
 */
public class ZoteroKeys
{
	private ZoteroKeys()
	{
	}
	
	public static final class SearchKeys
	{
		public static final String INCLUDE_TRASHED_FALSE = "0";
		public static final String INCLUDE_TRASHED_TRUE = "1";
		public static final String INCLUDE_TRASHED = "includeTrashed";

		private SearchKeys() {}
	}

	public static final class TagKeys
	{
		private TagKeys() {}
		
		public static final String TAG = "tag";
		public static final String TYPE = "type";
	}
	
	public static final class CreatorKeys
	{
		private CreatorKeys() {}
		
		public static final String LAST_NAME = "lastName";
		public static final String FIRST_NAME = "firstName";
		public static final String CREATOR_TYPE = "creatorType";
	}
	
	public static final class MetaKeys
	{
		public static final String CREATOR_SUMMARY = "creatorSummary";
		public static final String NUM_CHILDREN = "numChildren";
		public static final String PARSED_DATE = "parsedDate";
		public static final String NUM_COLLECTIONS = "numCollections";
		public static final String NUM_ITEMS = "numItems";
		public static final String TYPE = "type";

		private MetaKeys() {}
	}
	
	public static final class CollectionKeys extends EntityKeys
	{
		public static final String PARENT_COLLECTION = "parentCollection";
		public static final String NAME = "name";

		private CollectionKeys() {}
	}
	
	public static class EntityKeys
	{
		public static final String URL = "url";
		public static final String VERSION = "version";

		private EntityKeys() {}
		
		public static final String KEY = "key";
	}
	
	public static class ItemKeys extends EntityKeys
	{
		public static final String EXTRA = "extra";
		public static final String TITLE = "title";
		public static final String ITEM_TYPE = "itemType";
		public static final String RELATIONS = "relations";
		public static final String COLLECTIONS = "collections";
		public static final String TAGS = "tags";
		public static final String ACCESS_DATE = "accessDate";
		public static final String NOTE = "note";

		private ItemKeys() {}
	}
	
	public static final class DocumentKeys extends ItemKeys
	{
		public static final String WEBSITE_TITLE = "websiteTitle";
		public static final String BOOK_TITLE = "bookTitle";
		public static final String PRESENTATION_TYPE = "presentationType";
		public static final String PROCEEDINGS_TITLE = "proceedingsTitle";
		public static final String POST_TYPE = "postType";
		public static final String LIBRARY_CATALOG = "libraryCatalog";
		public static final String ISSUE_DATE = "issueDate";
		public static final String COMPANY = "company";
		public static final String SHORT_TITLE = "shortTitle";
		public static final String RIGHTS = "rights";
		public static final String INTERVIEW_MEDIUM = "interviewMedium";
		public static final String ISBN = "ISBN";
		public static final String NAME_OF_ACT = "nameOfAct";
		public static final String NETWORK = "network";
		public static final String JOURNAL_ABBREVIATION = "journalAbbreviation";
		public static final String WEBSITE_TYPE = "websiteType";
		public static final String SCALE = "scale";
		public static final String EDITION = "edition";
		public static final String PROGRAMMING_LANGUAGE = "programmingLanguage";
		public static final String REPORT_NUMBER = "reportNumber";
		/**
		 * The abstract for a document or item.<br><br>
		 * REST API Property: <code>&quot;abstractNote&quot;</code>
		 */
		public static final String ABSTRACT = "abstractNote";
		public static final String UNIVERSITY = "university";
		public static final String SESSION = "session";
		public static final String AUDIO_RECORDING_FORMAT = "audioRecordingFormat";
		public static final String FILING_DATE = "filingDate";
		public static final String APPLICATION_NUMBER = "applicationNumber";
		public static final String SERIES_TEXT = "seriesText";
		public static final String CODE = "code";
		public static final String VIDEO_RECORDING_FORMAT = "videoRecordingFormat";
		public static final String PUBLISHER = "publisher";
		public static final String FIRST_PAGE = "firstPage";
		public static final String VOLUME = "volume";
		public static final String PUBLICATION_TITLE = "publicationTitle";
		public static final String HISTORY = "history";
		public static final String RUNNING_TIME = "runningTime";
		public static final String ARCHIVE = "archive";
		public static final String PATENT_NUMBER = "patentNumber";
		public static final String LEGAL_STATUS = "legalStatus";
		public static final String LETTER_TYPE = "letterType";
		public static final String CASE_NAME = "caseName";
		public static final String PAGES = "pages";
		public static final String INSTITUTION = "institution";
		public static final String REPORT_TYPE = "reportType";
		public static final String CODE_VOLUME = "codeVolume";
		public static final String SECTION = "section";
		public static final String LANGUAGE = "language";
		public static final String FORUM_TITLE = "forumTitle";
		public static final String DOCUMENT_NUMBER = "documentNumber";
		public static final String SUBJECT = "subject";
		public static final String COUNTRY = "country";
		public static final String MANUSCRIPT_TYPE = "manuscriptType";
		public static final String DATE = "date";
		public static final String DOCKET_NUMBER = "docketNumber";
		public static final String SERIES = "series";
		public static final String CALL_NUMBER = "callNumber";
		public static final String ISSUING_AUTHORITY = "issuingAuthority";
		public static final String PROGRAM_TITLE = "programTitle";
		public static final String SERIES_NUMBER = "seriesNumber";
		public static final String DISTRIBUTOR = "distributor";
		public static final String DICTIONARY_TITLE = "dictionaryTitle";
		public static final String REPORTER = "reporter";
		public static final String MAP_TYPE = "mapType";
		public static final String NUMBER_OF_VOLUMES = "numberOfVolumes";
		public static final String ARTWORK_SIZE = "artworkSize";
		public static final String COMMITTEE = "committee";
		public static final String ARTWORK_MEDIUM = "artworkMedium";
		public static final String ENCYCLOPEDIA_TITLE = "encyclopediaTitle";
		public static final String PLACE = "place";
		public static final String GENRE = "genre";
		public static final String DATE_ENACTED = "dateEnacted";
		public static final String NUM_PAGES = "numPages";
		public static final String BLOG_TITLE = "blogTitle";
		public static final String SERIES_TITLE = "seriesTitle";
		public static final String EPISODE_NUMBER = "episodeNumber";
		public static final String DATE_DECIDED = "dateDecided";
		public static final String BILL_NUMBER = "billNumber";
		public static final String ASSIGNEE = "assignee";
		public static final String AUDIO_FILE_TYPE = "audioFileType";
		public static final String MEETING_NAME = "meetingName";
		public static final String SYSTEM = "system";
		public static final String PUBLIC_LAW_NUMBER = "publicLawNumber";
		public static final String COURT = "court";
		public static final String THESIS_TYPE = "thesisType";
		public static final String ISSN = "ISSN";
		public static final String ISSUE = "issue";
		public static final String CONFERENCE_NAME = "conferenceName";
		public static final String REPORTER_VOLUME = "reporterVolume";
		public static final String CODE_PAGES = "codePages";
		public static final String DOI = "DOI";
		public static final String LEGISLATIVE_BODY = "legislativeBody";
		public static final String ARCHIVE_LOCATION = "archiveLocation";
		public static final String CODE_NUMBER = "codeNumber";
		public static final String REFERENCES = "references";
		public static final String STUDIO = "studio";
		public static final String DATE_MODIFIED = "dateModified";
		public static final String DATE_ADDED = "dateAdded";
		public static final String CREATORS = "creators";
		public static final String PRIORITY_NUMBERS = "priorityNumbers";
		public static final String LABEL = "label";

		private DocumentKeys() {}
	}
	
	public static final class AttachmentKeys extends ItemKeys
	{
		public static final String LINK_MODE = "linkMode";
		public static final String UPLOAD = "upload";
		private AttachmentKeys() {}
		
		public static final String MD5 = "md5";
		public static final String MTIME = "mtime";
		public static final String FILENAME = "filename";
		public static final String PATH = "path";
		public static final String CONTENT_TYPE = "contentType";
		public static final String CHARSET = "charset";
		public static final String PARENT_ITEM = "parentItem";
		public static final String FILE_SIZE = "filesize";
	}
	
	public static final class LinkKeys
	{
		private LinkKeys() {}
		
		public static final String HREF = "href";
		public static final String TYPE = "type";
		public static final String LENGTH = "length";
		public static final String TITLE = "title";
	}
}
