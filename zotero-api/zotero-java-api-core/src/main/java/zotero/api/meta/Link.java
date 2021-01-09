package zotero.api.meta;

import zotero.api.constants.LinkType;
import zotero.api.properties.PropertiesItem;

/**
 * A link represents an HTTP or API link to a specific object in the library.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Link extends PropertiesItem
{
	String getURL();

	LinkType getType();
}