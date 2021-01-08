package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyLong;

public class PropertyLongImpl extends PropertyImpl<Long> implements PropertyLong
{
	public PropertyLongImpl(String key, Long value, boolean readOnly)
	{
		super(PropertyType.LONG, key, value, readOnly);
	}
}
