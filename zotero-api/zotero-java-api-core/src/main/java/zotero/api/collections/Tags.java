package zotero.api.collections;

import zotero.api.Tag;
import zotero.apiimpl.ChangeTracker;

/**
 * A collection of tags for the given item.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Tags extends ChangeTracker, Iterable<Tag>
{
	int size();

	void add(Tag tag);
	
	void remove(Tag tag);

	void clear();
}