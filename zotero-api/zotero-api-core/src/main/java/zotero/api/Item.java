package zotero.api;

import java.util.Date;

import zotero.api.collections.Collections;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;

public interface Item extends Entry
{
	ItemType getItemType();

	// "accessDate": "",
	Date getAccessDate();

	// Quick access to common metadata items
	String getTitle();

	void setTitle(String title);

	Tags getTags();

	Collections getCollections();

	Relationships getRelationships();
}