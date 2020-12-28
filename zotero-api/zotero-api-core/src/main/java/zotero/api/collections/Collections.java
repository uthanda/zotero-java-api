package zotero.api.collections;

import zotero.api.Collection;
import zotero.api.iterators.CollectionIterator;

public interface Collections
{
	CollectionIterator iterator();
	
	void addToCollection(Collection collection);
	
	void removeFromCollection(Collection collection);
}
