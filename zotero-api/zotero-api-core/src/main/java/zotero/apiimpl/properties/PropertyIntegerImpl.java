package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyInteger;

public class PropertyIntegerImpl extends PropertyImpl<Integer> implements PropertyInteger
{
	public PropertyIntegerImpl(String key, Integer value)
	{
		this(key,value,false);
	}

	public PropertyIntegerImpl(String key, Integer value, boolean readOnly)
	{
		super(PropertyType.INTEGER, key, value, readOnly);
	}
}
