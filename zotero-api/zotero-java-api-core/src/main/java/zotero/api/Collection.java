package zotero.api;

import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

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
	 * Fetches the items contained in the collection
	 * 
	 * @return Item iterator
	 */
	ItemIterator fetchItems();
	
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
}