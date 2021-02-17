package zotero.api.batch;

/**
 * Represents an item that has been added to a batch for processing. It will
 * contain a link to the original item {@link #getIndex()} as well as the
 * outcome of the call via {@link #getResult()} or {@link #getMessage()}.
 * 
 * @author Michael Oland
 * @since 1.0
 *
 * @param <T> Result type
 */
public interface BatchHandle<T>
{
	/**
	 * Gets the result of the item operation.
	 * 
	 * @return Item operation result.
	 */
	BatchResult getResult();

	/**
	 * Gets the message for the item. This is expected to be null if the item
	 * was successfully committed, otherwise it's the message from the local or
	 * remote API.
	 * 
	 * @return Error message describing the failure
	 */
	String getMessage();

	/**
	 * Get the original item. This item will be updated with any additional
	 * properties that result from the remote API call, such as the new item
	 * key.
	 * 
	 * @return Original item.
	 */
	T getItem();

	/**
	 * Gets the index of the item in the batch.
	 * 
	 * @return Item idex
	 */
	int getIndex();
}
