package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyString;

public class PropertyStringImpl extends PropertyImpl<String> implements PropertyString
{
	public PropertyStringImpl(String key, String value)
	{
		this(key, value, false);
	}

	public PropertyStringImpl(String key, String value, boolean readOnly)
	{
		super(PropertyType.STRING, key, value, readOnly);
	}
}
