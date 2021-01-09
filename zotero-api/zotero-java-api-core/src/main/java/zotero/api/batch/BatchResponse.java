package zotero.api.batch;

/**
 * Represents the outcome of a batch commit and provides both access to the
 * individual batch handles as well as convenience methods to confirm the number
 * of items and whether the whole batch was successful or if there were errors.
 * 
 * @author Michael Oland
 * @since 1.0
 *
 * @param <T>
 */
public interface BatchResponse<T>
{
	/**
	 * Indicates whether there were any errors in the items being added.
	 * 
	 * @return True if errors are present, false if not
	 */
	boolean hasErrors();

	/**
	 * Gets the handle for the item for the given index.
	 * 
	 * @param index Index
	 * @return Item
	 */
	BatchHandle<T> getHandle(int index);

	/**
	 * Gets the number of items in the batch.
	 * 
	 * @return Item count
	 */
	int getCount();
}
