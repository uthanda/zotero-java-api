package zotero.apiimpl.properties;

import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyBoolean;

public class PropertyBooleanImpl extends PropertyImpl<Boolean> implements PropertyBoolean
{
	public PropertyBooleanImpl(String key, Boolean value)
	{
		super(PropertyType.BOOLEAN, key, value);
	}
}
