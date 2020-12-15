package zotero.apiimpl.properties;

import java.util.Date;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyDate;

public class PropertyDateImpl extends PropertyImpl implements PropertyDate
{
	private Date value;
	private boolean isDirty = false;
	
	public PropertyDateImpl(String key, Date value)
	{
		super(key, PropertyType.DATE);
		this.value = value;
	}

	@Override
	public Date getValue()
	{
		return value;
	}

	@Override
	public void setValue(Date value)
	{
		this.value = value;
		this.isDirty = true;
	}

	public final boolean isDirty()
	{
		return isDirty;
	}
}
