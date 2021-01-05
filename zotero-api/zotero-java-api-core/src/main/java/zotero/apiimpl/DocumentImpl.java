package zotero.apiimpl;

import static zotero.api.constants.ZoteroKeys.DocumentKeys.CREATORS;
import static zotero.api.constants.ZoteroKeys.DocumentKeys.DATE_ADDED;
import static zotero.api.constants.ZoteroKeys.DocumentKeys.DATE_MODIFIED;
import static zotero.api.constants.ZoteroKeys.MetaKeys.CREATOR_SUMMARY;
import static zotero.api.constants.ZoteroKeys.MetaKeys.NUM_CHILDREN;
import static zotero.api.constants.ZoteroKeys.MetaKeys.PARSED_DATE;
import static zotero.apiimpl.rest.ZoteroRest.Items.CHILDREN;

import java.util.Date;

import zotero.api.Document;
import zotero.api.Note;
import zotero.api.collections.Creators;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class DocumentImpl extends ItemImpl implements Document
{
	private int numChildren;
	private String parsedDate;
	private String creatorSummary;

	DocumentImpl(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		super(jsonItem, library);
	}

	public DocumentImpl(ItemType type, LibraryImpl library)
	{
		super(type, library);
	}

	@Override
	public Date getDateAdded()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(DATE_ADDED);
	}

	@Override
	public Date getDateModified()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(DATE_MODIFIED);
	}

	@Override
	public String getCreatorSummary()
	{
		return creatorSummary;
	}

	@Override
	public String getParsedDate()
	{
		checkDeletionStatus();

		return parsedDate;
	}

	@Override
	public final int getNumberOfChilden()
	{
		return numChildren;
	}

	@Override
	public ItemIterator fetchChildren()
	{
		checkDeletionStatus();

		return ((LibraryImpl) getLibrary()).fetchItems(CHILDREN, this.getKey());
	}

	@Override
	public Creators getCreators()
	{
		checkDeletionStatus();

		return (Creators) super.getProperties().getProperty(CREATORS).getValue();
	}

	@Override
	public void changeItemType(ItemType type)
	{
		checkDeletionStatus();

		super.reinitialize(type);
	}

	@Override
	public final String getTitle()
	{
		checkDeletionStatus();
	
		return super.getProperties().getString(ZoteroKeys.ItemKeys.TITLE);
	}

	@Override
	public final void setTitle(String title)
	{
		checkDeletionStatus();
	
		super.getProperties().putValue(ZoteroKeys.ItemKeys.TITLE, title);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		DocumentImpl document = new DocumentImpl(jsonItem,library);
		
		document.numChildren = ((Double)jsonItem.getMeta().get(NUM_CHILDREN)).intValue();
		document.creatorSummary = (String) jsonItem.getMeta().get(CREATOR_SUMMARY);
		document.parsedDate = (String) jsonItem.getMeta().get(PARSED_DATE);
		
		return document;
	}

	@Override
	public Note createNote()
	{
		return new NoteImpl((LibraryImpl)getLibrary(), getKey());
	}

}
