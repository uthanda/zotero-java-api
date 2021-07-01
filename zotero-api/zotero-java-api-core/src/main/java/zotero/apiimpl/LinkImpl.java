package zotero.apiimpl;

import zotero.api.constants.LinkType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.meta.Link;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyEnumImpl;
import zotero.apiimpl.properties.PropertyIntegerImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.model.ZoteroRestLink;

final class LinkImpl extends PropertiesItemImpl implements Link
{
	public LinkImpl(LibraryImpl library)
	{
		super(library);
		
		PropertiesImpl properties = (PropertiesImpl) getProperties();
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.LinkKeys.HREF, null));
		properties.addProperty(new PropertyIntegerImpl(ZoteroKeys.LinkKeys.LENGTH, null));
		properties.addProperty(new PropertyIntegerImpl(ZoteroKeys.LinkKeys.ATTACHMENT_SIZE, null));
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.LinkKeys.TITLE, null));
		properties.addProperty(new PropertyEnumImpl<>(ZoteroKeys.LinkKeys.TYPE, LinkType.class, null));
	}

	@Override
	public final String getURL()
	{
		return getProperties().getString(ZoteroKeys.LinkKeys.HREF);
	}

	@Override
	public final String getType()
	{
		return (String) getProperties().getProperty(ZoteroKeys.LinkKeys.TYPE).getValue();
	}

	@SuppressWarnings("unchecked")
	public final void setType(LinkType type)
	{
		((PropertyEnumImpl<LinkType>)getProperties().getProperty(ZoteroKeys.LinkKeys.TYPE)).setValue(type);
	}

	public static LinkImpl fromRest(LibraryImpl library, ZoteroRestLink item)
	{
		LinkImpl link = new LinkImpl(library);
		((PropertiesImpl)link.getProperties()).fromRest(library, item);
		
		return link;
	}
}