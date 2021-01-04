package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Property;

public abstract class PropertyImpl<T> implements Property<T>
{
	private final String key;
	private final PropertyType propertyType;
	private T value;
	private boolean dirty;
	private boolean readOnly;
	private boolean cleared = false;

	protected PropertyImpl(PropertyType propertyType, String key, T value, boolean readOnly)
	{
		this.key = key;
		this.propertyType = propertyType;
		this.value = value;
		this.readOnly = readOnly;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public PropertyType getPropertyType()
	{
		return propertyType;
	}

	public boolean isDirty()
	{
		return this.dirty;
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue(T value)
	{
		checkReadOnly();

		this.dirty = true;
		this.cleared = false;
		this.value = value;
	}

	protected void checkReadOnly() throws ZoteroRuntimeException
	{
		if (readOnly)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read only");
		}
	}

	@Override
	public void clearValue()
	{
		checkReadOnly();

		this.value = null;
		this.dirty = true;
		this.cleared = true;
	}

	@Override
	public String toString()
	{
		return String.format("[%s key=%s, dirty=%b, value=%s]", this.getClass().getSimpleName(), key, dirty, value);
	}

	public Object toRestValue()
	{
		if (cleared)
		{
			return Boolean.FALSE;
		}
		else if (dirty)
		{
			return value;
		}
		else
		{
			return null;
		}
	}

	@FunctionalInterface
	public static interface RestSerializer
	{
		Object serialize(PropertyImpl<?> property);
	}
}
