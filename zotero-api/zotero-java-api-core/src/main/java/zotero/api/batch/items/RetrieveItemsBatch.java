package zotero.api.batch.items;

/**
 * This batch will create one or more items in a single call to the repository.
 * See {@link ItemsBatch} for more details on what types of item(s) it can
 * accept.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface RetrieveItemsBatch extends ItemsBatch
{
	public BatchItemHandle add(String key);
}
