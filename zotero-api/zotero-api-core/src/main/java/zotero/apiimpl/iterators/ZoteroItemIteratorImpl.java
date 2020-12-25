package zotero.apiimpl.iterators;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public final class ZoteroItemIteratorImpl extends ZoteroIteratorImpl<Item> implements ItemIterator
{
	public ZoteroItemIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
	{
		super(response, ItemImpl::fromItem, library);
	}
}