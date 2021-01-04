package zotero.apiimpl.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import zotero.api.collections.RelationSet;
import zotero.api.constants.RelationshipType;
import zotero.apiimpl.LibraryImpl;

public class RelationSetImpl implements RelationSet
{
	private final Set<String> urls;
	@SuppressWarnings("unused")
	private final LibraryImpl library;
	private final RelationshipType type;
	
	private boolean isDirty = false;

	public RelationSetImpl(LibraryImpl library, RelationshipType type)
	{
		this(library, type, new LinkedHashSet<>());
	}

	public RelationSetImpl(LibraryImpl library, RelationshipType type, Set<String> urls)
	{
		this.type = type;
		this.urls = urls;
		this.library = library;
	}

	@Override
	public boolean isDirty()
	{
		return isDirty;
	}

	@Override
	public boolean contains(String item)
	{
		return urls.contains(item);
	}

	@Override
	public int size()
	{
		return urls.size();
	}

	@Override
	public void addRelation(String item)
	{
		urls.add(item);
		this.isDirty = true;
	}

	@Override
	public Iterator<String> iterator()
	{
		return urls.iterator();
	}

	@Override
	public void removeRelation(String item)
	{
		urls.remove(item);
		this.isDirty = true;
	}

	@Override
	public RelationshipType getType()
	{
		return type;
	}

	public Collection<String> getKeys()
	{
		return urls;
	}
}
