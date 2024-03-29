package zotero.apiimpl;

import zotero.api.Note;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
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
		((PropertiesImpl)getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.AttachmentKeys.PARENT_ITEM, parentKey));
		((PropertiesImpl)getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.NOTE, null));
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

	public static Note fromRest(LibraryImpl library, ZoteroRestItem item)
	{
		NoteImpl note = new NoteImpl(library);
		
		note.refresh(item);
		
		return note;
		
	}
}
