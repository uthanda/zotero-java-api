package zotero.api.collections;

import zotero.api.Collection;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.ChangeTracker;

/**
 * A Collections collection provides access to the collections for a given item.  It also provides
 * methods for adding and removing links to a collection for a given item.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Collections extends ChangeTracker
{
	/**
	 * Gets the iterator for the collections.
	 * 
	 * @return Collections iterator
	 */
	CollectionIterator iterator();
	
	/**
	 * Links an item to a new collection.
	 * 
	 * @param collection Collection to link
	 */
	void addToCollection(Collection collection);
	
	/**
	 * Removes the link to a collection.
	 * 
	 * @param collection Collection to unlink
	 */
	void removeFromCollection(Collection collection);

	void clear();
}
