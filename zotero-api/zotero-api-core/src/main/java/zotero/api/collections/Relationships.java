package zotero.api.collections;

import java.util.List;
import java.util.Set;

import zotero.api.constants.RelationshipType;

public interface Relationships
{
	List<String> getRelationships(RelationshipType type);
	
	Set<RelationshipType> getTypes();
}