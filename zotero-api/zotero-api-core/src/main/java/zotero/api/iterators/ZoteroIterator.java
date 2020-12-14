package zotero.api.iterators;

import java.util.Iterator;

public interface ZoteroIterator<T> extends Iterator<T>
{
	boolean hasNext();

	T next();

	int getTotalCount();
}