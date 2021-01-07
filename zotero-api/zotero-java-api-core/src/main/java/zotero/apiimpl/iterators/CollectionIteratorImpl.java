package zotero.apiimpl.iterators;

import zotero.api.Collection;
import zotero.api.iterators.CollectionIterator;
import zotero.apiimpl.CollectionImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public final class CollectionIteratorImpl extends ZoteroIteratorImpl<Collection> implements CollectionIterator
{
	public CollectionIteratorImpl(LibraryImpl library)
	{
		super(CollectionImpl::fromItem, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?, ?> builder)
	{
		// NOOP
	}
}