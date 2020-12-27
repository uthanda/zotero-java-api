package zotero.api;

import java.io.InputStream;

import zotero.api.constants.LinkMode;

public interface Attachment extends Item
{
	LinkMode getType();

	String getContentType();

	void setContentType(String type);

	String getCharset();

	void setCharset(String charset);
	
	InputStream retrieveContent();
}