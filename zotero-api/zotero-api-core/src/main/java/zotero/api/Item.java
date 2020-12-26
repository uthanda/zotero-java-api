package zotero.api;

import java.util.Date;

import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.iterators.CollectionIterator;

public interface Item extends Entry
{
	ItemType getItemType();
	
	// "accessDate": "",
	Date getAccessDate();
	
	// Quick access to common metadata items
	String getTitle();
	void setTitle(String title);
	
	Tags getTags();

	CollectionIterator getCollections();
	
	Relationships getRelationships();
}