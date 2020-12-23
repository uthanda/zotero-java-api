package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyEnum;

public class PropertyEnumImpl<T extends Enum<T>> extends PropertyImpl<T> implements PropertyEnum<T>
{
	private Class<T> type;

	public PropertyEnumImpl(String key, Class<T> type, T value)
	{
		super(PropertyType.ENUM, key, value);
		this.type = type;
	}

	@Override
	public Class<T> getType()
	{
		return type;
	}
}
