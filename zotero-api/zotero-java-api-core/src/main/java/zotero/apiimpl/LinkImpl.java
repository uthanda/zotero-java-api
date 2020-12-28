package zotero.apiimpl;

import zotero.api.Link;
import zotero.api.constants.LinkType;
import zotero.api.constants.ZoteroKeys;
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
		return getProperties().getString(ZoteroKeys.Link.HREF);
	}

	public final void setHref(String href)
	{
		getProperties().putValue(ZoteroKeys.Link.HREF, href);
	}

	@Override
	public final LinkType getType()
	{
		return (LinkType) getProperties().getProperty(ZoteroKeys.Link.TYPE).getValue();
	}

	@SuppressWarnings("unchecked")
	public final void setType(LinkType type)
	{
		((PropertyEnumImpl<LinkType>)getProperties().getProperty(ZoteroKeys.Link.TYPE)).setValue(type);
	}

	public static LinkImpl from(ZoteroRestLink jsonLink)
	{
		LinkImpl link = new LinkImpl();

		PropertiesImpl props = (PropertiesImpl) link.getProperties();

		jsonLink.entrySet().forEach(e -> {
			if (e.getKey().equals(zotero.api.constants.ZoteroKeys.Link.LENGTH))
			{
				props.addProperty(new PropertyIntegerImpl(zotero.api.constants.ZoteroKeys.Link.LENGTH, ((Double) jsonLink.get(zotero.api.constants.ZoteroKeys.Link.LENGTH)).intValue()));
			}
			else if (e.getKey().equals(zotero.api.constants.ZoteroKeys.Link.TYPE))
			{
				props.addProperty(new PropertyEnumImpl<>(ZoteroKeys.Link.LENGTH, LinkType.class, LinkType.fromZoteroType(jsonLink.get(ZoteroKeys.Link.TYPE).toString())));
			}
			else
			{
				props.addProperty(new PropertyStringImpl(e.getKey(), (String) e.getValue()));
			}
		});

		return link;
	}
}