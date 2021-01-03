package zotero.apiimpl.iterators;

import zotero.api.Tag;
import zotero.api.iterators.TagIterator;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.TagImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.response.RestResponse;

public final class TagIteratorImpl extends ZoteroIteratorImpl<Tag> implements TagIterator
{
	public TagIteratorImpl(RestResponse<ZoteroRestItem[]> response, LibraryImpl library)
	{
		super(response, TagImpl::fromRest, library);
	}
}