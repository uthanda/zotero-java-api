package zotero.api.collections;

import zotero.api.constants.RelationshipType;
import zotero.apiimpl.ChangeTracker;

public interface RelationSet extends ChangeTracker, Iterable<String>
{
	boolean contains(String url);
	
	int size();
	
	void addRelation(String url);
	
	void removeRelation(String url);
	
	RelationshipType getType();
}
