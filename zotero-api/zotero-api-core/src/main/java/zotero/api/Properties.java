package zotero.api;

public interface Properties
{

	String getString(String key);

	Integer getInteger(String key);

	Double getDouble(String key);

	Object getRaw(String key);

}