package zotero.api;

import zotero.api.constants.LinkType;

/**
 * Represents a collection links to the content or item within the Zotero web application.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Links
{
	/**
	 * Check if there is a link of the requested type.
	 * 
	 * @param type Link type
	 * @return True if one exists in the collection.
	 */
	boolean has(LinkType type);

	/**
	 * Get the link for the specific type
	 * 
	 * @param type Link type
	 * @return Link if present for the request type or <code>null</code> if not present.
	 */
	Link get(LinkType type);
}