package zotero.apiimpl;

import java.util.Map;

import zotero.api.PropertiesItem;
import zotero.api.constants.ItemType;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;

public class PropertiesItemImpl implements PropertiesItem
{
	private Properties properties = new PropertiesImpl();

	PropertiesItemImpl(Map<String, Object> values)
	{
		this.properties = PropertiesImpl.from(values);
	}

	PropertiesItemImpl(ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.from(item);
	}

	PropertiesItemImpl(ItemType type)
	{
		this.properties = PropertiesImpl.initialize(type);
	}

	@Override
	public final Properties getProperties()
	{
		return properties;
	}
}