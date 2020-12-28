package zotero.apiimpl;

import java.util.Date;

import zotero.api.Document;
import zotero.api.collections.Creators;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.ItemIterator;
import zotero.apiimpl.rest.ZoteroRestPaths;
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

		return super.getProperties().getDate(ZoteroKeys.DATE_ADDED);
	}

	@Override
	public Date getDateModified()
	{
		checkDeletionStatus();

		return super.getProperties().getDate(ZoteroKeys.DATE_MODIFIED);
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

		return ((LibraryImpl) getLibrary()).fetchItems(ZoteroRestPaths.ITEM_CHILDREN, this.getKey());
	}

	@Override
	public Creators getCreators()
	{
		checkDeletionStatus();

		return (Creators) super.getProperties().getProperty(ZoteroKeys.CREATORS).getValue();
	}

	@Override
	public void changeItemType(ItemType type)
	{
		checkDeletionStatus();

		super.reinitialize(type);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		DocumentImpl document = new DocumentImpl(jsonItem,library);
		
		document.numChildren = ((Double)jsonItem.getMeta().get(ZoteroKeys.NUM_CHILDREN)).intValue();
		document.creatorSummary = (String) jsonItem.getMeta().get(ZoteroKeys.CREATOR_SUMMARY);
		document.parsedDate = (String) jsonItem.getMeta().get(ZoteroKeys.PARSED_DATE);
		
		return document;
	}

}
