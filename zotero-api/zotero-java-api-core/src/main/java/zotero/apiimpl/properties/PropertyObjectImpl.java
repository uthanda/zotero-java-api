package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyObject;
import zotero.apiimpl.ChangeTracker;

public abstract class PropertyObjectImpl<T extends ChangeTracker> extends PropertyImpl<T> implements PropertyObject<T>
{
	private Class<T> type;

	protected PropertyObjectImpl(String key, Class<T> type, T value)
	{
		this(key,type,value,false);
	}

	protected PropertyObjectImpl(String key, Class<T> type, T value, boolean readOnly)
	{
		super(PropertyType.OBJECT, key, value, readOnly);
		this.type = type;
	}

	public Class<T> getType()
	{
		return type;
	}
	
	@Override
	public boolean isDirty()
	{
		return super.isDirty() || (getValue() != null && getValue().isDirty());
	}
}
