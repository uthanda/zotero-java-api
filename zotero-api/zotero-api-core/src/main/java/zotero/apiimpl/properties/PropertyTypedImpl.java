package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;

public abstract class PropertyTypedImpl<T> extends PropertyImpl<T>
{
	private final Class<T> type;

	public PropertyTypedImpl(PropertyType propertyType, String key, Class<T> type, T value)
	{
		super(propertyType, key, value);
		this.type = type;
	}

	public final Class<T> getType()
	{
		return type;
	}
}