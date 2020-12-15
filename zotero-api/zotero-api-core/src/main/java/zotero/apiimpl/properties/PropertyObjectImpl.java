package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyObject;

public class PropertyObjectImpl<T> extends PropertyTypedImpl<T> implements PropertyObject<T>
{
	private boolean isDirty = false;
	private T value;

	public PropertyObjectImpl(String key, Class<T> type, T value)
	{
		super(key, type, PropertyType.OBJECT);
		this.value = value;
	}

	@Override
	public T getValue()
	{
		return value;
	}

	@Override
	public void setValue(T value)
	{
		this.value = value;
	}

	@Override
	public boolean isDirty()
	{
		return isDirty;
	}

}
