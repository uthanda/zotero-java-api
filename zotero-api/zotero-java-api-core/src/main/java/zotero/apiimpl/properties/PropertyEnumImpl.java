package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.constants.ZoteroEnum;
import zotero.api.properties.PropertyEnum;

public class PropertyEnumImpl<T extends Enum<T> & ZoteroEnum> extends PropertyImpl<T> implements PropertyEnum<T>
{
	private Class<T> type;

	public PropertyEnumImpl(String key, Class<T> type, T value)
	{
		this(key,type,value,false);
	}

	public PropertyEnumImpl(String key, Class<T> type, T value, boolean readOnly)
	{
		super(PropertyType.ENUM, key, value, readOnly);
		this.type = type;
	}

	@Override
	public Class<T> getType()
	{
		return type;
	}
	
	@Override
	public Object toRestValue()
	{
		Object value = super.toRestValue();
		
		return value instanceof ZoteroEnum ? ((ZoteroEnum)value).getZoteroName() : value;
	}
	
	@Override
	public void clearValue()
	{
		super.clearValue();
		super.setValue(null);
	}
}
