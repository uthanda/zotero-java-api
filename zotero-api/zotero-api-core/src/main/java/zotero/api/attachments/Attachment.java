package zotero.api.attachments;

import zotero.api.Item;
import zotero.api.constants.LinkMode;

public interface Attachment extends Item
{
	LinkMode getType();

	String getContentType();
	void setContentType(String type);
	
	String getCharset();
	void setCharset(String charset);
}