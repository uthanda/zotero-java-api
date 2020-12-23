package zotero.apiimpl;

import java.util.Map;

import zotero.api.PropertiesItem;
import zotero.api.constants.ItemType;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;

public class PropertiesItemImpl implements PropertiesItem
{
	private PropertiesImpl properties = new PropertiesImpl();

	PropertiesItemImpl()
	{
	}

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
		this.properties = new PropertiesImpl();
		PropertiesImpl.initialize(type, this.properties, null);
	}

	@Override
	public final Properties getProperties()
	{
		return properties;
	}

	final void reinitialize(ItemType type)
	{
		PropertiesImpl current = properties;

		this.properties = new PropertiesImpl();

		PropertiesImpl.initialize(type, properties, current);
	}
}