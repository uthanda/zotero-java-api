package zotero.apiimpl.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import zotero.api.Collection;
import zotero.api.collections.Collections;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.LibraryImpl;

public class CollectionsImpl implements Collections
{
	private Set<String> collections = new LinkedHashSet<>();
	private LibraryImpl library;
	private boolean isDirty = false;

	@Override
	public CollectionIterator iterator()
	{
		return new CollectionIterator()
		{
			private Iterator<String> i = collections.iterator();
			
			@Override
			public boolean hasNext()
			{
				return i.hasNext();
			}

			@Override
			public Collection next()
			{
				return library.fetchCollection(i.next());
			}

			@Override
			public int getTotalCount()
			{
				return collections.size();
			}
		};
	}

	@Override
	public void addToCollection(Collection collection)
	{
		collections.add(collection.getKey());
		this.isDirty = true;
	}

	@Override
	public void removeFromCollection(Collection collection)
	{
		collections.remove(collection.getKey());
		this.isDirty = true;
	}

	public boolean isDirty()
	{
		return isDirty;
	}
	
	public static Collections fromRest(LibraryImpl library, List<String> collectionList)
	{
		CollectionsImpl collections = new CollectionsImpl();
		collections.library = library;
		collections.collections.addAll(collectionList);
		
		return collections;
	}
	
	public static List<String> toRest(CollectionsImpl collections)
	{
		return Arrays.asList(collections.collections.toArray(new String[collections.collections.size()]));
	}
}
