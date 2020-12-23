package zotero.apiimpl.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyList;

public class PropertyListImpl<T> extends PropertyImpl<PropertyListImpl.ObservableList<T>> implements PropertyList<T,PropertyListImpl.ObservableList<T>>
{
	private Class<T> type;

	public PropertyListImpl(String key, Class<T> type, List<T> values)
	{
		super(PropertyType.LIST, key, buildList(values));
		this.type = type;
	}

	private static <T> ObservableList<T> buildList(List<T> values)
	{
		if(values instanceof ObservableList)
		{
			return (ObservableList<T>) values;
		}
		
		ObservableList<T> list = new ObservableList<>();
		list.addAll(values);
		list.dirty = false;
		
		return list;
	}

	@Override
	public final boolean isDirty()
	{
		return ((ObservableList<T>)getValue()).dirty;
	}

	public static class ObservableList<T> extends ArrayList<T>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7277287177675698114L;
		private boolean dirty;

		public ObservableList()
		{
		}

		public ObservableList(Collection<T> values)
		{
			super.addAll(values);
		}
		
		@Override
		public void clear()
		{
			this.dirty = true;
			super.clear();
		}

		@Override
		public void add(int index, T element)
		{
			this.dirty = true;
			super.add(index, element);
		}

		@Override
		public void ensureCapacity(int minCapacity)
		{
			this.dirty = true;
			super.ensureCapacity(minCapacity);
		}

		@Override
		public void trimToSize()
		{
			this.dirty = true;
			super.trimToSize();
		}

		@Override
		public void sort(Comparator<? super T> c)
		{
			this.dirty = true;
			super.sort(c);
		}

		@Override
		public T set(int index, T element)
		{
			this.dirty = true;
			return super.set(index, element);
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			this.dirty = true;
			return super.retainAll(c);
		}

		@Override
		public void replaceAll(UnaryOperator<T> operator)
		{
			this.dirty = true;
			super.replaceAll(operator);
		}

		@Override
		public boolean removeIf(Predicate<? super T> filter)
		{
			this.dirty = true;
			return super.removeIf(filter);
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			this.dirty = true;
			return super.removeAll(c);
		}

		@Override
		public boolean remove(Object o)
		{
			this.dirty = true;
			return super.remove(o);
		}

		@Override
		public T remove(int index)
		{
			this.dirty = true;
			return super.remove(index);
		}

		@Override
		public boolean add(T e)
		{
			this.dirty = true;
			return super.add(e);
		}

		@Override
		public boolean addAll(int index, Collection<? extends T> c)
		{
			this.dirty = true;
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(Collection<? extends T> c)
		{
			this.dirty = true;
			return super.addAll(c);
		}

		public boolean isDirty()
		{
			return dirty;
		}
	}

	@Override
	public Class<T> getType()
	{
		return type;
	}
}
