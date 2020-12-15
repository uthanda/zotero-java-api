package zotero.apiimpl;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;

final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
{
	CollectionIteratorImpl(RestResponse<ZoteroRestItem[]> response, Library library)
	{
		super(response, CollectionImpl::fromItem, library);
	}
}