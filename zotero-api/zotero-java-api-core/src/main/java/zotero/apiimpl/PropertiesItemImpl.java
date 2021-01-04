package zotero.apiimpl;

import zotero.api.PropertiesItem;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class PropertiesItemImpl implements PropertiesItem
{
	private final LibraryImpl library;
	private PropertiesImpl properties;

	PropertiesItemImpl(LibraryImpl library)
	{
		this.library = library;
		this.properties =  new PropertiesImpl(library);
	}

	PropertiesItemImpl(LibraryImpl library, ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.fromRest(library, item);
		this.library = library;
	}

	PropertiesItemImpl(LibraryImpl library, LinkMode mode)
	{
		this.properties = new PropertiesImpl(library);
		this.library = library;
		PropertiesImpl.initializeAttachmentProperties(mode, this.properties);
	}

	PropertiesItemImpl(LibraryImpl library, ItemType type)
	{
		this.properties = new PropertiesImpl(library);
		this.library = library;

		if (type == ItemType.NOTE)
		{
			PropertiesImpl.initializeNoteProperties(this.properties, null);
		}
		else
		{
			PropertiesImpl.initializeDocumentProperties(type, this.properties, null);
		}
	}

	@Override
	public final Properties getProperties()
	{
		return properties;
	}

	final void reinitialize(ItemType type)
	{
		PropertiesImpl current = properties;

		this.properties = new PropertiesImpl(library);

		PropertiesImpl.initializeDocumentProperties(type, properties, current);
	}

	protected void refresh(LibraryImpl library, ZoteroRestItem item)
	{
		this.properties = PropertiesImpl.fromRest(library, item);
	}
}