package zotero.apiimpl.collections;

import static zotero.api.constants.ZoteroKeys.Document.CREATORS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import zotero.api.Creator;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.apiimpl.CreatorImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.properties.PropertyListImpl.ObservableList;

public final class CreatorsImpl implements Creators
{
	private final ObservableList<Creator> list;
	private final LibraryImpl library;
	private boolean cleared = false;

	public CreatorsImpl(LibraryImpl library)
	{
		this(library, new ArrayList<>());
	}

	public CreatorsImpl(LibraryImpl library, List<Creator> list)
	{
		this.list = new ObservableList<>(CREATORS, list, false);
		this.library = library;
	}

	@Override
	public void add(CreatorType type, String firstName, String lastName)
	{
		CreatorImpl creator = new CreatorImpl(library);
		creator.setFirstName(firstName);
		creator.setLastName(lastName);
		creator.setType(type);

		this.list.add(creator);
		cleared = false;
	}

	public static CreatorsImpl fromRest(LibraryImpl library, Object value)
	{
		if (value instanceof Boolean && Boolean.FALSE.equals(value))
		{
			return new CreatorsImpl(library);
		}

		List<Creator> list = ((List<?>) value).stream().map(e -> CreatorImpl.fromRest(library, e)).collect(Collectors.toList());
		return new CreatorsImpl(library, list);
	}

	public static Object toRest(CreatorsImpl creators)
	{
		if (creators.cleared)
		{
			return Boolean.FALSE;
		}
		else
		{
			return creators.list.stream().map(CreatorImpl::toRest).collect(Collectors.toList());
		}
	}

	public boolean isDirty()
	{
		return this.list.isDirty() || this.list.parallelStream().anyMatch(e -> ((CreatorImpl) e).isDirty());
	}

	@Override
	public String toString()
	{
		return String.format("[Creators list:%s, dirty:%b]", super.toString(), isDirty());
	}

	@Override
	public Iterator<Creator> iterator()
	{
		return list.iterator();
	}

	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public Creator get(int index)
	{
		return list.get(index);
	}

	@Override
	public void remove(Creator creator)
	{
		list.remove(creator);
		cleared = false;
	}

	@Override
	public void remove(int index)
	{
		list.remove(index);
		cleared = false;
	}

	public void clear()
	{
		list.clear();
		cleared = true;
	}
}