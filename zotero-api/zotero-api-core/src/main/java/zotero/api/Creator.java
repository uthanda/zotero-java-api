package zotero.api;

import zotero.api.constants.CreatorType;

public interface Creator extends PropertiesItem
{
	CreatorType getType();
	
	void setType(CreatorType type);
	
	String getFirstName();
	
	void setFirstName(String name);
	
	String getLastName();
	
	void setLastName(String name);
}