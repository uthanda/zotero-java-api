package zotero.api;

public interface Links
{
	boolean has(String key);

	Link get(String key);

	Link create(String key);
}