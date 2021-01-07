package zotero.apiimpl.iterators;

import zotero.api.Note;
import zotero.api.constants.ItemType;
import zotero.api.iterators.NoteIterator;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.NoteImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public class NoteIteratorImpl extends ZoteroIteratorImpl<Note> implements NoteIterator
{
	public NoteIteratorImpl(LibraryImpl library)
	{
		super(NoteImpl::fromRest, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?, ?> builder)
	{
		builder.queryParam("itemType", ItemType.NOTE.getZoteroName());
	}
}
