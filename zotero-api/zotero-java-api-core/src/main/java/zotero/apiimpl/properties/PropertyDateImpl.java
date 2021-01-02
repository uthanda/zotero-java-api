package zotero.apiimpl.properties;

import java.util.Date;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyDate;

public class PropertyDateImpl extends PropertyImpl<Date> implements PropertyDate
{
	public PropertyDateImpl(String key, Date value)
	{
		this(key,value,false);
	}

	public PropertyDateImpl(String key, Date value, boolean readOnly)
	{
		super(PropertyType.DATE, key, value, readOnly);
	}
}