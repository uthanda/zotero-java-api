package zotero.api;

/**
 * A note is a way of storing text and other information either stand-alone or
 * as a child of another item, such as a {@link Document] or {@link Attachment}.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Note extends Item
{
	/**
	 * Gets the note content.
	 * 
	 * @return Note content
	 */
	String getNoteContent();

	/**
	 * Sets the note content
	 * 
	 * @param content Note content
	 */
	void setNoteContent(String content);
}
