package zotero.api;

public interface Entry extends PropertiesItem
{
	int hashCode();

	boolean equals(Object obj);

	String getKey();

	int getVersion();

	Library getLibrary();

	Links getLinks();

	void refresh();

	void save();

	void delete();
	
	Relationships getRelationships();
}