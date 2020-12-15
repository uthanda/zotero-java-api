package zotero.api.properties;

import zotero.api.constants.PropertyType;

public interface Property
{
	String getKey();
	
	PropertyType getPropertyType();
}
