package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyInteger;

public class PropertyIntegerImpl extends PropertyImpl implements PropertyInteger
{
	private Integer value;
	private boolean isDirty = false;
	
	public PropertyIntegerImpl(String key, Integer value)
	{
		super(key, PropertyType.STRING);
		this.value = value;
	}

	@Override
	public Integer getValue()
	{
		return value;
	}

	@Override
	public void setValue(Integer value)
	{
		this.value = value;
		this.isDirty = true;
	}

	public final boolean isDirty()
	{
		return isDirty;
	}
}
