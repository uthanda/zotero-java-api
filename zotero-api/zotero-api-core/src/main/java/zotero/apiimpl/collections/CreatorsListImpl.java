package zotero.apiimpl.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import zotero.api.Creator;
import zotero.api.collections.CreatorsList;
import zotero.api.constants.CreatorType;
import zotero.apiimpl.CreatorImpl;

public final class CreatorsListImpl implements CreatorsList
{
	private List<Creator> list;
	private boolean isDirty = false;

	public CreatorsListImpl()
	{
		this.list = new ArrayList<>();
	}
	
	public CreatorsListImpl(List<?> list)
	{
		this.list = list.stream().map(e -> CreatorImpl.fromMap(e)).collect(Collectors.toList());
	}
	
	boolean isDirty()
	{
		return isDirty;
	}

	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return list.contains(o);
	}

	@Override
	public Iterator<Creator> iterator()
	{
		return list.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return list.toArray(a);
	}

	@Override
	public boolean add(Creator e)
	{
		this.isDirty = true;
		return list.add(e);
	}

	@Override
	public boolean remove(Object o)
	{
		this.isDirty = true;
		return list.remove(o);
	}

	@Override
	public boolean containsAll(java.util.Collection<?> c)
	{
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(java.util.Collection<? extends Creator> c)
	{
		this.isDirty = true;
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, java.util.Collection<? extends Creator> c)
	{
		this.isDirty = true;
		return list.addAll(index, c);
	}

	@Override
	public boolean removeAll(java.util.Collection<?> c)
	{
		this.isDirty = true;
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(java.util.Collection<?> c)
	{
		this.isDirty = true;
		return list.retainAll(c);
	}

	@Override
	public void clear()
	{
		this.isDirty = true;
		list.clear();
	}

	@Override
	public Creator get(int index)
	{
		return list.get(index);
	}

	@Override
	public Creator set(int index, Creator element)
	{
		this.isDirty = true;
		return list.set(index, element);
	}

	@Override
	public void add(int index, Creator element)
	{
		this.isDirty = true;
		list.add(index, element);
	}

	@Override
	public Creator remove(int index)
	{
		this.isDirty = true;
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Creator> listIterator()
	{
		return list.listIterator();
	}

	@Override
	public ListIterator<Creator> listIterator(int index)
	{
		return list.listIterator(index);
	}

	@Override
	public List<Creator> subList(int fromIndex, int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public void add(CreatorType type, String firstName, String lastName)
	{
		CreatorImpl creator = new CreatorImpl(null);
		creator.setFirstName(firstName);
		creator.setLastName(lastName);
		creator.setType(type);

		this.add(creator);
	}
}