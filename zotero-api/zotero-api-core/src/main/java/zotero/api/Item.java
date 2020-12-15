package zotero.api;

import java.util.Date;
import java.util.List;

import zotero.api.collections.CreatorsList;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;

public interface Item extends Entry 
{
	ItemIterator fetchChildren();
	
	String getTitle();

	CreatorsList getCreators();

	Date getDateAdded();

	Date getDateModified();

	String getItemType();

	String getRights();

	String getURL();

	String getShortTitle();

	Date getAccessDate();

	String getExtra();

	String getAbstractNote();

	CollectionIterator getCollections();

	List<Tag> getTags();

	void refresh() throws Exception;

	int getNumberOfChilden();

	void save();

	void delete();

	Relationships getRelationships();
}