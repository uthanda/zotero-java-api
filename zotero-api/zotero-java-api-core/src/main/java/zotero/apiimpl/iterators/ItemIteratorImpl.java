package zotero.apiimpl.iterators;

import zotero.api.Item;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public final class ItemIteratorImpl extends ZoteroIteratorImpl<Item> implements ItemIterator
{
	public ItemIteratorImpl(LibraryImpl library)
	{
		super(ItemImpl::fromRest, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?, ?> builder)
	{
		// NOOP as there are no params to add
	}
}