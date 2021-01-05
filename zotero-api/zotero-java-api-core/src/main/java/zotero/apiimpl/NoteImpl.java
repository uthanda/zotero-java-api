package zotero.apiimpl;

import zotero.api.Note;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class NoteImpl extends ItemImpl implements Note
{
	private NoteImpl(LibraryImpl library)
	{
		super(library);
		initialize(library,(String)null);
	}

	public NoteImpl(LibraryImpl library, String parent)
	{
		super(library);
		initialize(library,parent);
	}

	public void initialize(LibraryImpl library, String parentKey) throws ZoteroRuntimeException
	{
		super.initialize(library, ItemType.NOTE);
		getProperties().putValue(ZoteroKeys.AttachmentKeys.PARENT_ITEM, parentKey);
	}
	
	@Override
	public String getNoteContent()
	{
		return getProperties().getString(ZoteroKeys.ItemKeys.NOTE);
	}

	@Override
	public void setNoteContent(String content)
	{
		getProperties().putValue(ZoteroKeys.ItemKeys.NOTE, content);
	}

	public static ItemImpl fromRest(LibraryImpl library, ZoteroRestItem item)
	{
		NoteImpl note = new NoteImpl(library);
		
		((PropertiesImpl)note.getProperties()).fromRest(library, item.getData());
		
		return note;
		
	}
}
