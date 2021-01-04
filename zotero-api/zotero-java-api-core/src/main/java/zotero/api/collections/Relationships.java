package zotero.api.collections;

import java.util.Set;

import zotero.api.constants.RelationshipType;
import zotero.apiimpl.ChangeTracker;

/**
 * Provides a collection of relationships between two items.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Relationships extends ChangeTracker, Iterable<RelationSet>
{
	/**
	 * Gets the relationships of the specified type.
	 * 
	 * @param type Relationship type
	 * @return List of keys for related items.
	 */
	RelationSet getRelatedItems(RelationshipType type);
	
	/**
	 * Gets the types of relationships currently present.
	 * 
	 * @return Set of relationship types present for the item.
	 */
	Set<RelationshipType> getTypes();

	void clear();
}