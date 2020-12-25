package zotero.api;

import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

public interface Collection extends Entry
{
	// Item information
	
	String getName();
	
	void setName(String string);

	int getNumberOfCollections();

	int getNumberOfItems();

	// Item actions
	
	void refresh();

	void save();

	void delete();
	
	// Related item methods
	
	ItemIterator fetchItems();
	
	CollectionIterator fetchSubCollections();
	
	Collection fetchParentCollection();
}