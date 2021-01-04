package zotero.api.iterators;

import java.util.Iterator;

public interface ZoteroIterator<T> extends Iterator<T>
{
	int getTotalCount();
}