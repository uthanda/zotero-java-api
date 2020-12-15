package zotero.apiimpl;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.ItemIterator;

final class ZoteroItemIteratorImpl extends ZoteroIteratorImpl<Item> implements ItemIterator
{
	ZoteroItemIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
	{
		super(response, ItemImpl::fromItem, library);
	}
}