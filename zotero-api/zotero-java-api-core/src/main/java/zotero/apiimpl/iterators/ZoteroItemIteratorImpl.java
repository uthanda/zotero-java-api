package zotero.apiimpl.iterators;

import zotero.api.Item;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.response.RestResponse;

public final class ZoteroItemIteratorImpl extends ZoteroIteratorImpl<Item> implements ItemIterator
{
	public ZoteroItemIteratorImpl(RestResponse<ZoteroRestItem[]> response, LibraryImpl library)
	{
		super(response, ItemImpl::fromItem, library);
	}
}