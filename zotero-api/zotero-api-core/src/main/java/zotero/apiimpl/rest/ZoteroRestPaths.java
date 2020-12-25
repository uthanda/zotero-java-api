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

	public static final String URL_PARAM_KEY = "key";
	public static final String COLLECTIONS = "/collections";
	public static final String COLLECTIONS_SUBCOLLECTIONS = "/collections/{key}/collections";
	public static final String COLLECTIONS_TOP = "/collections/top";
	public static final String COLLECTION = "/collections/{key}";
	public static final String COLLECTION_ITEMS = "/collections/{key}/items";
	public static final String COLLECTION_ITEMS_TOP = "/collections/{key}/items/top";

	private ZoteroRestPaths() {}
}
