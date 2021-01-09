package zotero.api.meta;

import zotero.api.Library;
import zotero.api.properties.PropertiesItem;

/**
 * Represents a base entry for any item in a Zotero library. This provides the
 * main metadata (version, key and the source library), plus access to the
 * links. It also offers the actions for the item, such as <code>save</code>,
 * <code>delete</code> and <code>refresh</code>.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Entry extends PropertiesItem, Linked
{
	/**
	 * Gets the Zotero item key (unique identifier)
	 * 
	 * @return Item key
	 */
	String getKey();

	/**
	 * Gets the item version.
	 * 
	 * @return Item version (or null if the item has not been created yet)
	 */
	Integer getVersion();

	/**
	 * Gets the library for the item.
	 * 
	 * @return Library
	 */
	Library getLibrary();

	/**
	 * Refreshes the metadata for this object.
	 */
	void refresh();

	/**
	 * Saves changes to the object
	 */
	void save();

	/**
	 * Deletes the object
	 */
	void delete();
}