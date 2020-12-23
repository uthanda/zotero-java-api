package zotero.api;

import java.util.Date;

import zotero.api.collections.Creators;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

public interface Item extends Entry 
{
	ItemIterator fetchChildren();
	
	String getTitle();

	void setTitle(String title);
	
	Creators getCreators();

	Date getDateAdded();

	Date getDateModified();

	Date getAccessDate();

	ItemType getItemType();
	
	void changeItemType(ItemType type);

	String getRights();
	
	void setRights(String rights);

	String getURL();
	
	void setURL(String url);

	String getShortTitle();
	
	void setShortTitle(String shortTitle);

	String getExtra();
	
	void setExtra(String extra);

	String getAbstractNote();
	
	void setAbstractNote(String abstractNote);

	CollectionIterator getCollections();

	Tags getTags();

	int getNumberOfChilden();

	Relationships getRelationships();

	void refresh();

	void save();

	void delete();
}