package zotero.api;

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

	void provideContent(InputStream is, Integer fileSize);
}