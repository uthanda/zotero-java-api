package zotero.api;

import zotero.api.constants.TagType;
import zotero.api.meta.Linked;

public interface Tag extends Linked
{
	String getTag();
	
	TagType getType();
	
	int getNumberItems();

	void refresh();
}
