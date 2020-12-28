package zotero.api;

import java.util.Date;

import zotero.api.collections.Collections;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;

/**
 * An Item is a document or attachment that is stored in the library.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Item extends Entry
{
	/**
	 * Gets the item type.
	 * 
	 * @return Item type
	 */
	ItemType getItemType();

	// "accessDate": "",
	/**
	 * Gets the access date.
	 * 
	 * @return Access date
	 */
	Date getAccessDate();

	// Quick access to common metadata items
	/**
	 * Gets the title of the item. This is a convenience method
	 * that wraps around <code>getProperties().getString(ZoteroKeys.TITLE)</code>.
	 * 
	 * @return Item title
	 */
	String getTitle();

	/**
	 * Sets the title of the item. This is a convenience method
	 * that wraps around <code>getProperties().putString(ZoteroKeys.TITLE, title)</code>.
	 * 
	 * @param title Item title
	 */
	void setTitle(String title);

	/**
	 * Gets the tags for the item.
	 * 
	 * @return Item tags
	 */
	Tags getTags();

	/**
	 * Gets the collections this item is linked to.
	 * 
	 * @return Collections list
	 */
	Collections getCollections();

	/**
	 * Gets the relationships collection for this item.
	 * 
	 * @return Relationships collection.
	 */
	Relationships getRelationships();
}