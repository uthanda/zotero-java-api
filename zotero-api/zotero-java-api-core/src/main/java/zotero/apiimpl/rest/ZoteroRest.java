package zotero.apiimpl.rest;

import zotero.api.constants.ZoteroEnum;

public final class ZoteroRest
{
	// In the future, this should be replaced with a Swagger/OpenAPI-based
	// client

	@SuppressWarnings({ "squid:S1192" })
	public static final class Collections
	{
		private Collections()
		{
		}

		/**
		 * Collections in the library<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/collections</code>
		 */
		public static final String ALL = "/collections";
		/**
		 * Top-level collections in the library<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/collections/top</code>
		 */
		public static final String TOP = ALL + "/top";
		/**
		 * A specific collection in the library<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/collections/&lt;collectionKey&gt;</code>
		 */
		public static final String SPECIFIC = ALL + "/{" + URLParameter.COLLECTION_KEY.getZoteroName() + "}";
		/**
		 * Subcollections within a specific collection in the library<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/collections/&lt;collectionKey&gt;/collections</code>
		 */
		public static final String SUBCOLLECTIONS = SPECIFIC + "/collections";
	}

	@SuppressWarnings({ "squid:S1192" })
	public static final class Items
	{
		private Items()
		{
		}

		/**
		 * All items in the library, excluding trashed items<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/items</code>
		 */
		public static final String ALL = "/items";
		/**
		 * Top-level items in the library, excluding trashed items<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/items/top</code>
		 */
		public static final String TOP = ALL + "/top";
		/**
		 * Items in the trash<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/items/trash</code>
		 */
		public static final String TRASH = ALL + "/trash";
		/**
		 * A specific item in the library<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/items/&lt;itemKey&gt;</code>
		 */
		public static final String SPECIFIC = ALL + "/{" + URLParameter.ITEM_KEY.getZoteroName() + "}";
		/**
		 * Child items under a specific item<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/items/&lt;itemKey&gt;/children</code>
		 */
		public static final String CHILDREN = SPECIFIC + "/children";
		/**
		 * Child items under a specific item<br>
		 * Endpoint: <code>&lt;userOrGroupPrefix&gt;/publications/items</code>
		 */
		public static final String MY_PUBLICATIONS = "/publications/items";
		/**
		 * Items within a specific collection in the library<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/collections/&lt;collectionKey&gt;/items</code>
		 */
		public static final String COLLECTION_ITEMS = Collections.SPECIFIC + "/items";
		/**
		 * Top-level items within a specific collection in the library<br>
		 * Endpoint:
		 * <code>&lt;userOrGroupPrefix&gt;/collections/&lt;collectionKey&gt;/items/top</code>
		 */
		public static final String COLLECTION_ITEMS_TOP = Collections.SPECIFIC + "/items/top";

		public static final String FILE = Items.SPECIFIC + "/file";
	}

	@SuppressWarnings({ "squid:S1192" })
	public static final class Tags
	{
		private Tags()
		{
		}

		/**
		 * All tags in the library<br>
		 * Endpoint: <code>/tags</code>
		 */
		public static final String ALL = "/tags";

		/**
		 * Tags of all types matching a specific name<br>
		 * Endpoint: <code>/tags/&lt;url+encoded+tag&gt; </code>
		 */
		public static final String SPECIFIC = ALL + "/{" + URLParameter.TAG_NAME.getZoteroName() + "}";

		/**
		 * Tags associated with a specific item<br>
		 * Endpoint: <code>/items/&lt;itemKey&gt;/tags </code>
		 */
		public static final String ITEMS = Items.SPECIFIC + "/tags";

		/**
		 * Tags within a specific collection in the library<br>
		 * Endpoint: <code>/collections/&lt;collectionKey&gt;/tags </code>
		 */
		public static final String COLLECTIONS = Collections.SPECIFIC + "/tags";

		/**
		 * All tags in the library, with the ability to filter based on the
		 * items <br>
		 * Endpoint: <code>/items/tags </code>
		 */
		public static final String ITEMS_ALL = Items.ALL + "/tags";

		/**
		 * Tags assigned to top-level items<br>
		 * Endpoint: <code>/items/top/tags </code>
		 */
		public static final String ITEMS_TOP = Items.SPECIFIC + "/tags/top";

		/**
		 * Tags assigned to items in the trash <br>
		 * Endpoint: <code>/items/trash/tags </code>
		 */
		public static final String ITEMS_TRASH = Items.SPECIFIC + "/tags/trash";

		/**
		 * Tags assigned to items in a given collection<br>
		 * Endpoint: <code>/items/&lt;collectionKey&gt;/items/tags</code>
		 */
		public static final String COLLECTION_TAGS = Items.SPECIFIC + "/tags";

		/**
		 * Tags assigned to top-level items in a given collection<br>
		 * Endpoint: <code>/items/&lt;collectionKey&gt;/items/top/tags </code>
		 */
		public static final String COLLECTION_TAGS_TOP = Items.SPECIFIC + "/tags/top";

		/**
		 * Tags assigned to items in My Publications<br>
		 * Endpoint: <code>/publications/items/tags </code>
		 */
		public static final String MY_PUBLICATIONS_TAGS = Items.MY_PUBLICATIONS + "/tags/top";
	}

	public static final class Searches
	{
		private Searches()
		{
		}

		/**
		 * All saved searches in the library<br>
		 * Endpoint: <code>/searches</code>
		 */
		public static final String ALL = "/searches";
		/**
		 * A specific saved search in the library<br>
		 * Endpoint: <code>/searches/&lt;searchKey&gt;</code>
		 */
		public static final String SPECIFIC = ALL + "/{" + URLParameter.SEARCH_KEY.getZoteroName() + "}";
	}

	@SuppressWarnings({ "squid:S1192" })
	public static final class Other
	{
		private Other()
		{
		}

		/**
		 * The user id and privileges of the given API key.<br>
		 * Use the DELETE HTTP method to delete the key. This should generally
		 * be done only by a client that created the key originally using
		 * OAuth.<br>
		 * Endpoint: <code>/keys/&lt;key&gt;</code>
		 */
		public static final String KEYS = "/keys/{" + URLParameter.KEY + "}";
		/**
		 * A specific saved search in the library<br>
		 * Endpoint: <code>/searches/&lt;searchKey&gt;</code>
		 */
		public static final String GROUPS = "/users/{" + URLParameter.USERID.getZoteroName() + "}/groups";
	}

	public enum URLParameter implements ZoteroEnum
	{
		KEY("key"),
		ITEM_KEY("itemKey"),
		SEARCH_KEY("searchKey"),
		COLLECTION_KEY("collectionKey"),
		TAG_NAME("tagName"),
		USERID("userId");

		private final String zoteroName;

		private URLParameter(String zoteroName)
		{
			this.zoteroName = zoteroName;
		}

		@Override
		public String getZoteroName()
		{
			return zoteroName;
		}
	}

	public static final class Headers
	{
		public static final String ZOTERO_WRITE_TOKEN = "Zotero-Write-Token";
		public static final String ZOTERO_API_VERSION = "Zotero-API-Version";
		public static final String ZOTERO_API_KEY = "Zotero-API-Key";
		public static final String IF_UNMODIFIED_SINCE_VERSION = "If-Unmodified-Since-Version";
		public static final String USER_AGENT = "User-Agent";
		public static final String IF_MODIFIED_SINCE_VERSION = "If-Modified-Since-Version";
		public static final String LAST_MODIFIED_VERSION = "Last-Modified-Version";
		public static final String TOTAL_RESULTS = "Total-Results";
		public static final String LINK = "Link";
		public static final String RETRY_AFTER = "Retry-After";
		public static final String IF_MATCH = "If-Match";
		public static final String IF_NONE_MATCH = "If-None-Match";
		public static final String BACKOFF = "Backoff";

		private Headers()
		{
		}
	}

	public static final class API
	{
		public static final String HOST = "api.zotero.org";
		public static final String USERS_BASE = "/users/";
		public static final String GROUPS_BASE = "/groups/";
		public static final String VERSION = "3";

		private API()
		{
		}
	}

	public static final class Link
	{
		public static final String NEXT = "next";
		public static final String PREVIOUS = "previous";
		public static final String RELATIVE = "rel";

		private Link()
		{
		}
	}
	
	public static final class Batching
	{
		private Batching() {}
		
		public static final int MAX_BATCH_COUNT = 50;
	}

	private ZoteroRest()
	{
	}
}
