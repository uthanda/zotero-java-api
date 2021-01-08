package zotero.api.properties;

import java.util.Date;
import java.util.Set;

import zotero.api.Library;

public interface Properties extends Iterable<Property<?>>
{
	String getString(String key);

	Integer getInteger(String key);
	
	Long getLong(String key);
	
	Date getDate(String key);

	Set<String> getPropertyNames();

	@SuppressWarnings("squid:S1452")
	Property<?> getProperty(String key);

	void putValue(String key, String value);

	void putValue(String key, Integer value);
	
	void putValue(String mtime, Long value);
	
	Library getLibrary();
}