package zotero.apiimpl.iterators;

import zotero.api.Collection;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.CollectionImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.response.RestResponse;

public final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
{
	public CollectionIteratorImpl(RestResponse<ZoteroRestItem[]> response, LibraryImpl library)
	{
		super(response, CollectionImpl::fromItem, library);
	}
}