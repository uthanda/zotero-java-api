package zotero.api;

import zotero.api.constants.LinkType;

public interface Links
{
	boolean has(LinkType type);

	Link get(LinkType key);

	Link create(LinkType key);
}