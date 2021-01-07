package zotero.apiimpl.iterators;

import zotero.api.Tag;
import zotero.api.iterators.TagIterator;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.TagImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public final class TagIteratorImpl extends ZoteroIteratorImpl<Tag> implements TagIterator
{
	public TagIteratorImpl(LibraryImpl library)
	{
		super(TagImpl::fromRest, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?, ?> builder)
	{
		// NOOP
	}
}