package zotero.apiimpl;

import zotero.api.PropertiesItem;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class PropertiesItemImpl implements PropertiesItem
{
	private PropertiesImpl properties = new PropertiesImpl();

	PropertiesItemImpl()
	{
	}

	PropertiesItemImpl(LibraryImpl library, ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.fromRest(library, item);
	}

	PropertiesItemImpl(ItemType type, LinkMode mode)
	{
		this.properties = new PropertiesImpl();
		PropertiesImpl.initializeAttachmentProperties(type, mode, this.properties);
	}
	
	PropertiesItemImpl(ItemType type)
	{
		this.properties = new PropertiesImpl();
		PropertiesImpl.initializeDocumentProperties(type, this.properties, null);
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

		PropertiesImpl.initializeDocumentProperties(type, properties, current);
	}

	protected void refresh(LibraryImpl library, ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.fromRest(library, item);
	}
}