package zotero.api.batch;

import zotero.api.Item;

public interface ItemBatch extends Batch<Item, BatchItemHandle>
{
	BatchItemHandle add(Item item);
	
	BatchItemResponse commit();
}
