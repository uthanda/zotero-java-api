package zotero.apiimpl.iterators;

import java.util.Set;

import zotero.api.Item;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.LibraryImpl;

public class ItemSetIterator extends SetIterator<Item> implements ItemIterator
{
	public ItemSetIterator(LibraryImpl library, Set<String> set)
	{
		super(set,library::fetchItem);
	}
}
