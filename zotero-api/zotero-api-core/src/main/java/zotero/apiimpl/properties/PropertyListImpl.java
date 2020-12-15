package zotero.apiimpl.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyList;

public class PropertyListImpl<T> extends PropertyTypedImpl<T> implements PropertyList<T>
{
	private List<T> value = new ObservableList<>(this);
	private boolean isDirty = false;

	public PropertyListImpl(String key, Class<T> type, List<T> values)
	{
		super(key, type, PropertyType.LIST);

		if (value != null)
		{
			value.addAll(values);
			isDirty = false;
		}
	}

	@Override
	public List<T> getValue()
	{
		return value;
	}

	public final boolean isDirty()
	{
		return isDirty;
	}

	private static class ObservableList<T> extends ArrayList<T>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7277287177675698114L;

		private PropertyListImpl<T> property;

		private ObservableList(PropertyListImpl<T> property)
		{
			this.property = property;
		}

		@Override
		public void clear()
		{
			this.property.isDirty = true;
			super.clear();
		}

		@Override
		public void add(int index, T element)
		{
			this.property.isDirty = true;
			super.add(index, element);
		}

		@Override
		public void ensureCapacity(int minCapacity)
		{
			this.property.isDirty = true;
			super.ensureCapacity(minCapacity);
		}

		@Override
		public void trimToSize()
		{
			this.property.isDirty = true;
			super.trimToSize();
		}

		@Override
		public void sort(Comparator<? super T> c)
		{
			this.property.isDirty = true;
			super.sort(c);
		}

		@Override
		public T set(int index, T element)
		{
			this.property.isDirty = true;
			return super.set(index, element);
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			this.property.isDirty = true;
			return super.retainAll(c);
		}

		@Override
		public void replaceAll(UnaryOperator<T> operator)
		{
			this.property.isDirty = true;
			super.replaceAll(operator);
		}

		@Override
		public boolean removeIf(Predicate<? super T> filter)
		{
			this.property.isDirty = true;
			return super.removeIf(filter);
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			this.property.isDirty = true;
			return super.removeAll(c);
		}

		@Override
		public boolean remove(Object o)
		{
			this.property.isDirty = true;
			return super.remove(o);
		}

		@Override
		public T remove(int index)
		{
			this.property.isDirty = true;
			return super.remove(index);
		}

		@Override
		public boolean add(T e)
		{
			this.property.isDirty = true;
			return super.add(e);
		}

		@Override
		public boolean addAll(int index, Collection<? extends T> c)
		{
			this.property.isDirty = true;
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(Collection<? extends T> c)
		{
			this.property.isDirty = true;
			return super.addAll(c);
		}
	}
}
