package zotero.apiimpl.iterators;

import zotero.api.Attachment;
import zotero.api.constants.ItemType;
import zotero.api.iterators.AttachmentIterator;
import zotero.apiimpl.AttachmentImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public class AttachmentIteratorImpl extends ZoteroIteratorImpl<Attachment> implements AttachmentIterator
{
	public AttachmentIteratorImpl(LibraryImpl library)
	{
		super(AttachmentImpl::fromRest, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?, ?> builder)
	{
		builder.queryParam("itemType", ItemType.ATTACHMENT.getZoteroName());
	}
}
