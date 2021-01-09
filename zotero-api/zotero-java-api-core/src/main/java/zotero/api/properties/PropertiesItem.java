package zotero.api.properties;

/**
 * A properties item is an item that contains a collection of properties.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface PropertiesItem
{
	/**
	 * Get the properties collection for the item.
	 * 
	 * @return Properties collection.
	 */
	Properties getProperties();
}