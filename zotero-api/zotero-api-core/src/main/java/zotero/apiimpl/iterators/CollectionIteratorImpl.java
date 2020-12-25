package zotero.apiimpl.iterators;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.CollectionImpl;

public final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
{
	public CollectionIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
	{
		super(response, CollectionImpl::fromItem, library);
	}
}