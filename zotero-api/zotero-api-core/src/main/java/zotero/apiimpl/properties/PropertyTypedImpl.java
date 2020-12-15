package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyObject;

public abstract class PropertyTypedImpl<T> extends PropertyImpl
{
	private final Class<T> type;

	public PropertyTypedImpl(String key, Class<T> type, PropertyType propertyType)
	{
		super(key, propertyType);
		this.type = type;
	}

	public final Class<T> getType()
	{
		return type;
	}
}