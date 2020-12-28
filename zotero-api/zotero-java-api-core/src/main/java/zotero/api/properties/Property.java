package zotero.api.properties;

import zotero.api.constants.PropertyType;

public interface Property<T>
{
	String getKey();
	
	PropertyType getPropertyType();
	
	T getValue();
	
	void setValue(T value);
}
