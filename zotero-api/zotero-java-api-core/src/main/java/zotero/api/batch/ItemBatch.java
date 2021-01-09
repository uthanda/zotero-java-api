package zotero.api.batch;

import zotero.api.Item;

public interface ItemBatch extends Batch<Item, BatchItemHandle>
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
	 * @return A handle to the batch item that can be used later to determine
	 *         the result for that item
	 */
	BatchItemHandle add(Item item);

	/**
	 * Executes the batch and commits the items.
	 */
	BatchItemResponse commit();
}
