package zotero.api;

import java.io.File;
import java.io.InputStream;

import zotero.api.constants.LinkMode;

/**
 * An attachment is a file or URL either stored in the library or as an external
 * reference.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Attachment extends Item
{
	/**
	 * Gets the link mode type.
	 * 
	 * @return Link mode
	 */
	LinkMode getLinkMode();

	/**
	 * Changes the link mode and will have the effect of changing the required
	 * and available properties.
	 * 
	 * @param mode
	 *            New mode
	 */
	void changeLinkMode(LinkMode mode);

	/**
	 * Gets the content (MIME) type for the attachment.
	 * 
	 * @return Content type
	 */
	String getContentType();

	/**
	 * Sets the content type of the attachment
	 * 
	 * @param type
	 *            Conten type
	 */
	void setContentType(String type);

	/**
	 * Gets the character set for the attachment
	 * 
	 * @return Character set
	 */
	String getCharset();

	/**
	 * Sets the character set of the attachment
	 * 
	 * @param charset
	 *            Character set
	 */
	void setCharset(String charset);

	/**
	 * Gets the content of the attachment (applicable only for
	 * <code>LinkMode.IMPORTED_FILE</code> attachments. Calling this method on
	 * other link types will result in a <code>ZoteroRuntimeException</code>.
	 * 
	 * @return Input stream for accessing the content.
	 */
	InputStream retrieveContent();

	/**
	 * Provides the content for an attachment as an InputStream. This method
	 * requires the user to provide the content size and MD5 checksum. An
	 * alternate method is to call the
	 * {@link zotero.api.Attachment#provideContent(File)} method with a
	 * pre-created File object. This method will calculate the checksum and
	 * gather the rest of the required elements.
	 * 
	 * @param stream
	 *            Input stream for accessing the content
	 * @param size
	 *            Content size in bytes
	 * @param checksum
	 *            Pre-calculated MD5 checksum.
	 */
	void provideContent(InputStream stream, Long size, String checksum);

	/**
	 * Provides the content as a File object that can be loaded. This method
	 * will calculate the checksum and gather the filename, size and checksum
	 * automatically.
	 * 
	 * @param file
	 *            File to import as an attachment.
	 */
	void provideContent(File file);

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