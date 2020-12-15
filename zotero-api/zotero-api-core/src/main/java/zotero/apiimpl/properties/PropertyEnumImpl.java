package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyEnum;

public class PropertyEnumImpl<T extends Enum<T>> extends PropertyTypedImpl<T> implements PropertyEnum<T>
{
	private T value;
	private boolean isDirty = false;
	
	public PropertyEnumImpl(String key, Class<T> type, T value)
	{
		super(key, type, PropertyType.ENUM);
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
		this.isDirty = true;
	}

	public final boolean isDirty()
	{
		return isDirty;
	}
}
