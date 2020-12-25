package zotero.apiimpl;

import zotero.api.PropertiesItem;
import zotero.api.constants.ItemType;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class PropertiesItemImpl implements PropertiesItem
{
	private PropertiesImpl properties = new PropertiesImpl();

	PropertiesItemImpl()
	{
	}

	PropertiesItemImpl(ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.fromRest(item);
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