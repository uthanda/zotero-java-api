package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.Property;

public class PropertyImpl<T> implements Property<T>
{
	private final String key;
	private final PropertyType propertyType;
	private T value;
	private boolean dirty;
	
	public PropertyImpl(PropertyType propertyType, String key, T value)
	{
		this.key = key;
		this.propertyType = propertyType;
		this.value = value;
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
		this.dirty = true;
		this.value = value;
	}
}
