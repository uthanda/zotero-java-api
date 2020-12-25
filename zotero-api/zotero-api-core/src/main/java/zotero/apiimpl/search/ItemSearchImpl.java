package zotero.apiimpl.search;

import zotero.api.Library;
import zotero.api.internal.rest.ZoteroRestPaths;

public class ItemSearchImpl extends ItemEndpointSearchImpl
{
	public ItemSearchImpl(Library library)
	{
		super(library, ZoteroRestPaths.ITEMS);
	}
}
