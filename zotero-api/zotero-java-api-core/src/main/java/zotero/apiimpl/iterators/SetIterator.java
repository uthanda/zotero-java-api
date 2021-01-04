package zotero.apiimpl.iterators;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import zotero.api.Item;
import zotero.apiimpl.LibraryImpl;

public class SetIterator<T>
{
	private final Set<String> set;
	private final Iterator<String> i;
	private Function<String, T> fetcher;

	public SetIterator(Set<String> set, Function<String,T> fetcher)
	{
		this.set = set;
		this.i = set.iterator();
	}

	public int getTotalCount()
	{
		return set.size();
	}

	public boolean hasNext()
	{
		return i.hasNext();
	}

	public T next()
	{
		return fetcher.apply(i.next());
	}
}