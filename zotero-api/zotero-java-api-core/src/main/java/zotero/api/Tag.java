package zotero.api;

import zotero.api.constants.TagType;

public interface Tag extends Linked
{
	String getTag();
	
	TagType getType();
	
	int getNumberItems();

	void refresh();
}
