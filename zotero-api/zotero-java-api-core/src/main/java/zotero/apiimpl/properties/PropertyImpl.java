package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Property;

public class PropertyImpl<T> implements Property<T>
{
	private final String key;
	private final PropertyType propertyType;
	private T value;
	private boolean dirty;
	private boolean readOnly;

	public PropertyImpl(PropertyType propertyType, String key, T value, boolean readOnly)
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
		if (readOnly)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.PROPERTY_READ_ONLY, "Property " + key + " is read only");
		}

		this.dirty = true;
		this.value = value;
	}
}
