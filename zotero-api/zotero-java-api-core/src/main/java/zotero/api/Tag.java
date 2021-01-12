package zotero.api;

import zotero.api.constants.TagType;
import zotero.api.meta.Linked;

/**
 * A tag is a simple text string that can be applied to various documents and
 * other items. There are two types of tags, automatically applied and user
 * applied tags.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Tag extends Linked
{
	/**
	 * Get the tag value.
	 * 
	 * @return Tag value
	 */
	String getTag();

	/**
	 * Gets the tag type
	 * 
	 * @return Tag type
	 */
	TagType getType();

	/**
	 * Gets the number of items that this tag is applied to, as reported by the
	 * meta data.
	 * 
	 * @return Number of items
	 */
	int getNumberItems();

	/**
	 * Refreshes the item with data from the server.
	 */
	void refresh();
}
