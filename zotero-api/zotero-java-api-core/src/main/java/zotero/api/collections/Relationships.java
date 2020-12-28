package zotero.api.collections;

import java.util.List;
import java.util.Set;

import zotero.api.constants.RelationshipType;

/**
 * Provides a collection of relationships between two items.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Relationships
{
	/**
	 * Gets the relationships of the specified type.
	 * 
	 * @param type Relationship type
	 * @return List of keys for related items.
	 */
	List<String> getRelatedKeys(RelationshipType type);
	
	/**
	 * Gets the types of relationships currently present.
	 * 
	 * @return Set of relationship types present for the item.
	 */
	Set<RelationshipType> getTypes();
	
	// TODO this should be refactored to provide not the keys but the items
}