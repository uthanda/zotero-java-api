package zotero.api.collections;

import java.util.List;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;

/**
 * Contains a list of creators for a given item.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface Creators extends List<Creator>
{
	/**
	 * Adds a new creator to the collection.
	 * 
	 * @param type Creator type
	 * @param firstName First name
	 * @param lastName Last name
	 */
	void add(CreatorType type, String firstName, String lastName);
}
