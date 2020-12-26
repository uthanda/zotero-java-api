package zotero.api;

public interface Link extends PropertiesItem
{
	String getHref();

	void setHref(String uri);

	String getType();

	void setType(String type);
}