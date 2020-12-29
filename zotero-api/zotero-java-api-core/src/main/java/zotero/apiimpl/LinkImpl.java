package zotero.apiimpl;

import static zotero.api.constants.ZoteroKeys.Link.HREF;
import static zotero.api.constants.ZoteroKeys.Link.LENGTH;
import static zotero.api.constants.ZoteroKeys.Link.TYPE;

import zotero.api.Link;
import zotero.api.constants.LinkType;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyEnumImpl;
import zotero.apiimpl.properties.PropertyIntegerImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.model.ZoteroRestLink;

final class LinkImpl extends PropertiesItemImpl implements Link
{
	@Override
	public final String getHref()
	{
		return getProperties().getString(HREF);
	}

	public final void setHref(String href)
	{
		getProperties().putValue(HREF, href);
	}

	@Override
	public final String getType()
	{
		return getProperties().getString(TYPE);
	}

	@SuppressWarnings("unchecked")
	public final void setType(LinkType type)
	{
		((PropertyEnumImpl<LinkType>)getProperties().getProperty(TYPE)).setValue(type);
	}

	public static LinkImpl from(ZoteroRestLink jsonLink)
	{
		LinkImpl link = new LinkImpl();

		PropertiesImpl props = (PropertiesImpl) link.getProperties();

		jsonLink.entrySet().forEach(e -> {
			if (e.getKey().equals(LENGTH))
			{
				props.addProperty(new PropertyIntegerImpl(LENGTH, ((Double) jsonLink.get(LENGTH)).intValue()));
			}
			else
			{
				props.addProperty(new PropertyStringImpl(e.getKey(), (String) e.getValue()));
			}
		});

		return link;
	}
}