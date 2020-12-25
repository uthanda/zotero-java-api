package zotero.apiimpl;

import zotero.api.Link;
import zotero.apiimpl.rest.model.ZoteroRestLink;

final class LinkImpl implements Link
{
	private String uri;
	@SuppressWarnings("unused")
	private String newUri = null;
	private String type;
	@SuppressWarnings("unused")
	private String newType = null;

	@Override
	public final String getUri()
	{
		return uri;
	}

	@Override
	public final void setUri(String uri)
	{
		this.newUri = uri;
	}

	@Override
	public final String getType()
	{
		return type;
	}

	@Override
	public final void setType(String type)
	{
		this.newType = type;
	}

	public static LinkImpl from(ZoteroRestLink jsonLink)
	{
		LinkImpl link = new LinkImpl();

		link.type = jsonLink.getType();
		link.uri = jsonLink.getHref();

		return link;
	}
}