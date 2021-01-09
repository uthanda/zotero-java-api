package zotero.api;

import zotero.api.iterators.AttachmentIterator;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.DocumentIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.iterators.TagIterator;
import zotero.api.meta.Entry;

/**
 * A collection is a folder within the Zotero interface that can be used to 
 * group together similar or related items.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Collection extends Entry
{
	// Item information
	
	/**
	 * Gets the collection name.
	 * 
	 * @return Collection name
	 */
	String getName();
	
	/**
	 * sets the collection name.
	 * 
	 * @param name Collection name
	 */
	void setName(String name);

	/**
	 * Gets the number of sub-collections
	 * 
	 * @return Number of sub-collections
	 */
	int getNumberOfCollections();

	/**
	 * Gets the number of items contained in the collection
	 * 
	 * @return Items in the collection
	 */
	int getNumberOfItems();

	// Related item methods
	
	/**
	 * Fetches the documents contained in the collection
	 * 
	 * @return Document iterator
	 */
	DocumentIterator fetchDocuments();
	
	/**
	 * Fetches the sub-collections
	 * 
	 * @return Collection iterator
	 */
	CollectionIterator fetchSubCollections();
	
	/**
	 * Fetches the parent collection
	 * 
	 * @return Parent collection
	 */
	Collection fetchParentCollection();

	/**
	 * Fetches the tags for all items contained in this collection.
	 * 
	 * @param top True means return on the top tags
	 * @return Tag iterator
	 */
	TagIterator fetchTags(boolean top);

	AttachmentIterator fetchAttachments();
	
	ItemIterator fetchItems();
}