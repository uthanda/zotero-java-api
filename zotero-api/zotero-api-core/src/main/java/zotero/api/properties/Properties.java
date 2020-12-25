package zotero.api.properties;

import java.util.Date;
import java.util.Set;

public interface Properties
{
	String getString(String key);

	Integer getInteger(String key);

	Set<String> getPropertyNames();

	Date getDate(String key);

	@SuppressWarnings({"squid:S1452"})
	Property<?> getProperty(String key);

	void putValue(String key, String name);
}