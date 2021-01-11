package zotero.api;

import java.util.List;

import zotero.api.auth.ZoteroAuth;
import zotero.api.batch.item.CreateItemsBatch;
import zotero.api.batch.item.RetrieveItemsBatch;
import zotero.api.batch.item.UpdateItemsBatch;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.LibraryImpl;

/**
 * <p>
 * The library is the primary way of interacting with the Zotero API. This class
 * manages user access and provides methods for accessing key elements of the
 * Zotero API. This API needs the following items:
 * </p>
 * 
 * <ul>
 * <li>An API key (generated via the
 * <a href="https://www.zotero.org/settings/keys/new">web interface</a>)</li>
 * <li>The user ID</li>
 * </ul>
 * 
 * <p>
 * Once the above items are available, then a new library can be created by
 * creating the <code>ZoteroAPIKey</code> object and calling the
 * <code>createLibrary</code> method. Once initialized, the various methods can
 * be called to interact with the Zotero library.
 * </p>
 * 
 * <strong>Note:</strong> at this moment, the API only works with the
 * <code>/users/</code> endpoint and not the <code>/groups/</code> endpoint.
 * This may be added in the future.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public abstract class Library
{
	public static final String API_VERSION = "0.1-SNAPSHOT";

	/**
	 * Gets the user id for the current library.
	 * 
	 * @return User Id
	 */
	public abstract String getUserId();

	/**
	 * Creates a new collection as the child of the provided collection. The
	 * collection is not persisted in Zotero until the <code>save</code> method
	 * is called.
	 * 
	 * @param parent
	 *            Parent collection.
	 * @return Initialized collection
	 */
	public abstract Collection createCollection(Collection parent);

	/**
	 * Fetches the collection with the provided key from Zotero.
	 * 
	 * @param key
	 *            Collection key
	 * @return Retrieved collection
	 */
	public abstract Collection fetchCollection(String key);

	/**
	 * Fetches all the collections from Zotero.
	 * 
	 * @return Retrieved collection iterator
	 */
	public abstract CollectionIterator fetchCollectionsAll();

	/**
	 * Fetches the top-level collections from Zotero.
	 * 
	 * @return Retrieved collection iterator
	 */
	public abstract CollectionIterator fetchCollectionsTop();

	/**
	 * Creates a new item of the type provided. The item is not persisted in
	 * Zotero until the <code>save</code> method is called.
	 * 
	 * @param type
	 *            Item type
	 * @return Initialized item
	 */
	public abstract Document createDocument(ItemType type);

	/**
	 * Fetches a specific item from the library
	 * 
	 * @param key
	 *            Item key
	 * @return Retrieved item
	 */
	public abstract Item fetchItem(String key);

	/**
	 * Fetches a set of items from the library.
	 * 
	 * @param keys
	 *            Item keys
	 * @return Batch of retrieved items
	 */
	public abstract RetrieveItemsBatch fetchItems(List<String> keys);

	/**
	 * Fetches all items in the library.
	 * 
	 * @return Item iterator
	 */
	public abstract ItemIterator fetchItemsAll();

	/**
	 * Fetches top items in the library.
	 * 
	 * @return Item iterator
	 */
	public abstract ItemIterator fetchItemsTop();

	/**
	 * Fetches trash items in the library.
	 * 
	 * @return Item iterator
	 */
	public abstract ItemIterator fetchItemsTrash();

	/**
	 * Creates an item search object that call be completed
	 * to execute a search against the library.
	 * 
	 * @return Item search builder
	 */
	public abstract ItemSearch createItemSearch();

	/**
	 * Creates a collection search object that call be completed
	 * to execute a search against the library.
	 * 
	 * @return Collection search builder
	 */
	public abstract CollectionSearch createCollectionSearch();

	/**
	 * Creates a new root-level attachment (without a parent item).
	 * 
	 * @param mode Link mode for the attachment.
	 * @return Created attachment
	 */
	public abstract Attachment createAttachment(LinkMode mode);

	/**
	 * Creates an attachment as a child of the provided item.
	 * 
	 * @param parent Parent item
	 * @param mode Link mode for the attachment.
	 * @return Created attachment
	 */
	public abstract Attachment createAttachment(Item parent, LinkMode mode);
	
	/**
	 * Creates a new tag.
	 * 
	 * @param value Tag value
	 * @return Tag object
	 */
	public abstract Tag createTag(String value);
	
	/**
	 * Gets all the tags in the library
	 * 
	 * @return Tag iterator
	 */
	public abstract TagIterator fetchTagsAll();
	
	/**
	 * Fetches tags of all types matching a specific name. 
	 * 
	 * @param name Name
	 * @return Tag iterator
	 */
	public abstract TagIterator fetchTag(String name);

	/**
	 * Creates a new library with the provided API key and user
	 * 
	 * @param userId User Id of the library to access
	 * @param auth Authentication object to use when authenticating.
	 * @return
	 */
	public static final Library createLibrary(String userId, ZoteroAuth auth)
	{
		return LibraryImpl.create(userId, auth);
	}

	/**
	 * Create a new create item batch that is capable of committing
	 * a number of items (attachments, notes, documents) in a single call.
	 * 
	 * @return Prepared batch
	 */
	public abstract CreateItemsBatch createCreateItemsBatch();
	
	/**
	 * Create a new update item batch that is capable of committing
	 * a number of items (attachments, notes, documents) in a single call.
	 * 
	 * @return Prepared batch
	 */
	public abstract UpdateItemsBatch createUpdateItemsBatch();
}