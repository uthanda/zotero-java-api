package zotero.api;

import java.util.Date;

import zotero.api.collections.Creators;
import zotero.api.constants.ItemType;
import zotero.api.iterators.AttachmentIterator;
import zotero.api.iterators.NoteIterator;

/**
 * Represents a document (non-attachment item) in the library. It provides the
 * ability to access the item's metadata, children and creators.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Document extends Item
{
	// Read-only metadata
	/**
	 * Gets the creator summary from the item.
	 * 
	 * @return Creator summary
	 */
	String getCreatorSummary();

	/**
	 * Gets the date the item was added to the library
	 * 
	 * @return Date added to the library
	 */
	Date getDateAdded();

	/**
	 * Gets the date the item was last modified
	 * 
	 * @return Date last modified
	 */
	Date getDateModified();

	/**
	 * Gets the parsed date
	 * 
	 * @return Parsed date
	 */
	String getParsedDate();

	/**
	 * Gets the number of children.
	 * 
	 * @return Number of children
	 */
	int getNumberOfChilden();

	/**
	 * Fetches the notes for this item.
	 * 
	 * @return Note iterator for access the item's notes.
	 */
	NoteIterator fetchNotes();
	
	/**
	 * Fetches the attachments for this item.
	 * 
	 * @return Attachment iterator for access the item's attachments.
	 */
	AttachmentIterator fetchAttachments();

	/**
	 * Gets the list of creators for this item.
	 * 
	 * @return Creators list
	 */
	Creators getCreators();

	/**
	 * Changes the item type of the document. This will have the effect of
	 * removing non-relevant metadata items and adding new ones.
	 * 
	 * @param type
	 *            New item type
	 */
	void changeItemType(ItemType type);

	Note createNote();

	/**
	 * Gets the title of the item. This is a convenience method that wraps
	 * around <code>getProperties().getString(ZoteroKeys.TITLE)</code>.
	 * 
	 * @return Item title
	 */
	String getTitle();

	/**
	 * Sets the title of the item. This is a convenience method that wraps
	 * around <code>getProperties().putString(ZoteroKeys.TITLE, title)</code>.
	 * 
	 * @param title
	 *            Item title
	 */
	void setTitle(String title);
}
