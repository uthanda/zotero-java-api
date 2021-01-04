package zotero.apiimpl.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import zotero.api.constants.PropertyType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyList;

public class PropertyListImpl<T> extends PropertyImpl<PropertyListImpl.ObservableList<T>> implements PropertyList<T, PropertyListImpl.ObservableList<T>>
{
	private Class<T> type;

	public PropertyListImpl(String key, List<T> values)
	{
		super(PropertyType.LIST, key, new ObservableList<>(key, values, false), false);
	}

	@Override
	public final boolean isDirty()
	{
		return ((ObservableList<T>) getValue()).dirty;
	}

	@SuppressWarnings("squid:S2160") // Disabled because it doesn't change the core behavior
	public static class ObservableList<T> extends ArrayList<T>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7277287177675698114L;
		private boolean dirty;
		private boolean readOnly;
		private String key;

		public ObservableList(String key, List<T> values, boolean readOnly)
		{
			this.key = key;
			this.readOnly = readOnly;

			if (values != null)
			{
				super.addAll(values);
			}
		}

		public ObservableList(Collection<T> values, String key)
		{
			super.addAll(values);
			this.key = key;
		}

		@Override
		public void clear()
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.clear();
		}

		@Override
		public void add(int index, T element)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.add(index, element);
		}

		@Override
		public void ensureCapacity(int minCapacity)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.ensureCapacity(minCapacity);
		}

		@Override
		public void trimToSize()
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.trimToSize();
		}

		@Override
		public void sort(Comparator<? super T> c)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.sort(c);
		}

		@Override
		public T set(int index, T element)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.set(index, element);
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.retainAll(c);
		}

		@Override
		public void replaceAll(UnaryOperator<T> operator)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			super.replaceAll(operator);
		}

		@Override
		public boolean removeIf(Predicate<? super T> filter)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.removeIf(filter);
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.removeAll(c);
		}

		@Override
		public boolean remove(Object o)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.remove(o);
		}

		@Override
		public T remove(int index)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

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
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

			this.dirty = true;
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(Collection<? extends T> c)
		{
			if (readOnly)
			{
				throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read-only");
			}

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
	
	@Override
	public void clearValue()
	{
		super.clearValue();
		super.getValue().clear();
	}
}
