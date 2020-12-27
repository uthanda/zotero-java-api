package zotero.apiimpl.rest;

public final class ZoteroRestPaths
{
	public static final String ITEMS = "/items";
	public static final String ITEMS_ALL = ITEMS;
	public static final String ITEMS_TOP = "/items/top";
	public static final String ITEMS_TRASH = "/items/trash";
	public static final String ITEM = "/items/{key}";
	public static final String ITEM_CHILDREN = "/items/{key}/children";
	public static final String ITEM_TAGS = "/items/{key}/tags";
	public static final String ITEM_FILE = ITEM + "/file";

	public static final String URL_PARAM_KEY = "key";
	public static final String COLLECTIONS = "/collections";
	public static final String COLLECTIONS_SUBCOLLECTIONS = "/collections/{key}/collections";
	public static final String COLLECTIONS_TOP = "/collections/top";
	public static final String COLLECTION = "/collections/{key}";
	public static final String COLLECTION_ITEMS = "/collections/{key}/items";
	public static final String COLLECTION_ITEMS_TOP = "/collections/{key}/items/top";
	public static final String ZOTERO_API_HOST = "api.zotero.org";
	public static final String ZOTERO_API_USERS_BASE = "/users/";
	public static final String ZOTERO_API_GROUPS_BASE = "/groups/";
	public static final String ZOTERO_API_VERSION = "3";

	public static final String HEADER_ZOTERO_WRITE_TOKEN = "Zotero-Write-Token";
	public static final String HEADER_ZOTERO_API_VERSION = "Zotero-API-Version";
	public static final String HEADER_ZOTERO_API_KEY = "Zotero-API-Key";
	public static final String HEADER_IF_UNMODIFIED_SINCE_VERSION = "If-Unmodified-Since-Version";
	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";
	public static final String HEADER_LAST_MODIFIED_VERSION = "Last-Modified-Version";
	public static final String HEADER_TOTAL_RESULTS = "Total-Results";
	public static final String HEADER_LINK = "Link";
	public static final String HEADER_RETRY_AFTER = "Retry-After";

	private ZoteroRestPaths() {}
}
