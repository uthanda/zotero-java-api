package zotero.apiimpl.iterators;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.CollectionImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
{
	public CollectionIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
	{
		super(response, CollectionImpl::fromItem, library);
	}
}