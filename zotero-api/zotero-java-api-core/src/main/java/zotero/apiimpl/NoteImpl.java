package zotero.apiimpl;

import zotero.api.Note;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class NoteImpl extends ItemImpl implements Note
{
	public NoteImpl(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		super(jsonItem, library);
	}

	public NoteImpl(LibraryImpl library, String key)
	{
		super(ItemType.NOTE, library);
		getProperties().putValue(ZoteroKeys.Attachment.PARENT_ITEM, key);
	}

	@Override
	public String getNoteContent()
	{
		return getProperties().getString(ZoteroKeys.Item.NOTE);
	}

	@Override
	public void setNoteContent(String content)
	{
		getProperties().putValue(ZoteroKeys.Item.NOTE, content);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		return new NoteImpl(jsonItem, library);
	}
}
