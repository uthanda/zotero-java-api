package zotero.apiimpl.iterators;

import java.util.Set;

import zotero.api.Collection;
import zotero.apiimpl.LibraryImpl;

public class CollectionSetIterator extends SetIterator<Collection>
{
	public CollectionSetIterator(LibraryImpl library, Set<String> set)
	{
		super(set, library::fetchCollection);
	}
}
