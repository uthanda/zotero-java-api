package zotero.api.collections;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;
import zotero.apiimpl.ChangeTracker;

/**
 * Contains a list of creators for a given item.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Creators extends Iterable<Creator>, ChangeTracker
{
	/**
	 * Adds a new creator to the collection.
	 * 
	 * @param type Creator type
	 * @param firstName First name
	 * @param lastName Last name
	 */
	void add(CreatorType type, String firstName, String lastName);

	int size();

	Creator get(int index);

	void remove(Creator creator);

	void remove(int index);
}
