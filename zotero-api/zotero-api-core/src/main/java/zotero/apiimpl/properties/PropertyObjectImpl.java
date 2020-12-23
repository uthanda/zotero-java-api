package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyObject;

public class PropertyObjectImpl<T> extends PropertyTypedImpl<T> implements PropertyObject<T>
{
	public PropertyObjectImpl(String key, Class<T> type, T value)
	{
		super(PropertyType.OBJECT, key, type, value);
	}
}
