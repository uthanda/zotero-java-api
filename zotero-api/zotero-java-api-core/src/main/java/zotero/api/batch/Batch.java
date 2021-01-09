package zotero.api.batch;

/**
 * A batch is a collection of items that will be executed in a single network
 * call to the library. Using a batch can help boost performance through reduced
 * network calls and greater efficiency on the server side. This interface will
 * typically be extended to better define the item type(s) that it can handle as
 * well as to define the handle that is returned.
 * 
 * @author Michael Oland
 * @since 1.0
 *
 * @param <T> Object type this batch executes
 * @param <H> The handle type the batch returns.
 */
public interface Batch<T, H extends BatchHandle<T>>
{
	/**
	 * Executes the operations added to the batch.
	 * 
	 * @return A batch response that contains the items and the results of each.
	 */
	BatchResponse<T> commit();

	/**
	 * Gets the number of items in the batch
	 * 
	 * @return Number of items in the batch
	 */
	int getCount();

	/**
	 * Adds a new object to the batch
	 * 
	 * @param object Object to be committed
	 * @return Handle to that object
	 */
	H add(T object);
}
