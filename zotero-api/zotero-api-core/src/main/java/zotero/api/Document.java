package zotero.api;

import java.util.Date;

import zotero.api.collections.Creators;
import zotero.api.constants.ItemType;
import zotero.api.iterators.ItemIterator;

public interface Document extends Item
{
	// Read-only metadata
	String getCreatorSummary();
	
	Date getDateAdded();
	
	Date getDateModified();
	
	String getParsedDate();

	int getNumberOfChilden();
	
	ItemIterator fetchChildren();
	
	Creators getCreators();

	void changeItemType(ItemType type);
}