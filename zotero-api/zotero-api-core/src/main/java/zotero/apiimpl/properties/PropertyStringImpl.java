package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyString;

public class PropertyStringImpl extends PropertyImpl implements PropertyString
{
	private String value;
	private boolean isDirty = false;
	
	public PropertyStringImpl(String key, String value)
	{
		super(key, PropertyType.STRING);
		this.value = value;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void setValue(String value)
	{
		this.value = value;
		this.isDirty = true;
	}

	public final boolean isDirty()
	{
		return isDirty;
	}
}
