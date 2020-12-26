package zotero.apiimpl;

import zotero.api.Link;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.properties.PropertiesImpl;
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

	@Override
	public final void setHref(String href)
	{
		getProperties().putValue(ZoteroKeys.Link.HREF, href);
	}

	@Override
	public final String getType()
	{
		return getProperties().getString(ZoteroKeys.Link.TYPE);
	}

	@Override
	public final void setType(String type)
	{
		getProperties().putValue(ZoteroKeys.Link.TYPE, type);
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
			else
			{
				props.addProperty(new PropertyStringImpl(e.getKey(), (String) e.getValue()));
			}
		});

		return link;
	}
}