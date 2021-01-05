package zotero.apiimpl;

import zotero.api.PropertiesItem;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;

public class PropertiesItemImpl implements PropertiesItem
{
	private PropertiesImpl properties;

	PropertiesItemImpl(LibraryImpl library)
	{
		this.properties =  new PropertiesImpl(library);
	}

	@Override
	public final Properties getProperties()
	{
		return properties;
	}
	
	void replace(PropertiesImpl properties)
	{
		this.properties = properties;
	}
}