package zotero.api.meta;

import zotero.api.constants.CreatorType;
import zotero.api.properties.PropertiesItem;

/**
 * A creator represents the creator, artist, or other person/entity
 * that is a recognized creator of a work.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Creator extends PropertiesItem
{
	/**
	 * Gets the type of creator
	 * 
	 * @return Creator type
	 */
	CreatorType getType();
	
	/**
	 * Sets the type of creator
	 * 
	 * @param type Creator type
	 */
	void setType(CreatorType type);
	
	/**
	 * Gets the first name of the creator.
	 * 
	 * @return First name
	 */
	String getFirstName();
	
	/**
	 * Sets the first name of the creator.
	 * 
	 * @param name First name
	 */
	void setFirstName(String name);

	/**
	 * Gets the last name of the creator.
	 * 
	 * @return Last name
	 */
	String getLastName();
	
	/**
	 * Sets the last name of the creator.
	 * 
	 * @param name Last name
	 */
	void setLastName(String name);
}