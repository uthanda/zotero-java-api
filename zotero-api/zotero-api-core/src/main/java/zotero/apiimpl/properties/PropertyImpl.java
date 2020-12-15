package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.Property;

public abstract class PropertyImpl implements Property
{
	private final String key;
	private final PropertyType propertyType;
	
	public PropertyImpl(String key, PropertyType propertyType)
	{
		this.key = key;
		this.propertyType = propertyType;
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

	public abstract boolean isDirty();
}
