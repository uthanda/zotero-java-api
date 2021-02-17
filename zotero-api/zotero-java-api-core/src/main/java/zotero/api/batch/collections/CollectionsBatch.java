package zotero.api.batch.collections;

import zotero.api.Collection;
import zotero.api.Item;
import zotero.api.batch.Batch;
import zotero.api.exceptions.ZoteroRuntimeException;

/**
 * This interface defines a batch that can commit one or more items to the
 * library. These items will include {@link zotero.api.Document},
 * {@link zotero.api.Note} and {@link zotero.api.Attachment} items. Upon execution, all items
 * will be transmitted to the remote library for processing.  The batch item handles
 * will provide access to the final result for each item.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface CollectionsBatch extends Batch<Collection, BatchCollectionHandle>
{
	/**
	 * Adds a new item to the batch. <strong>Note:</strong> linked file
	 * attachments are not supported.
	 * 
	 * @throws ZoteroRuntimeException
	 *             Thrown if API constraints are violated, such as trying to
	 *             commit a linked attachment or exceeding the number of
	 *             allowable items in a batch.
	 * 
	 * @param item The item to add to the batch
	 * 
	 * @return A handle to the batch item that can be used later to determine
	 *         the result for that item
	 */
	BatchCollectionHandle add(Item item) throws ZoteroRuntimeException;

	/**
	 * Executes the batch and commits the items.
	 * 
	 * @throws ZoteroRuntimeException
	 *             Thrown if there is a remote or I/O error.
	 */
	BatchCollectionResponse commit() throws ZoteroRuntimeException;
}
