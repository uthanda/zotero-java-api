package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyString;

public class PropertyStringImpl extends PropertyImpl<String> implements PropertyString
{
	public PropertyStringImpl(String key, String value)
	{
		super(PropertyType.STRING, key, value);
	}
}
