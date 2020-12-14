package zotero.api;

public interface Entry extends PropertiesItem
{
	int hashCode();

	boolean equals(Object obj);

	String getKey();

	int getVersion();

	Library getLibrary();

	Links getLinks();

	void refresh() throws Exception;

	void save() throws Exception;

	void delete() throws Exception;
}