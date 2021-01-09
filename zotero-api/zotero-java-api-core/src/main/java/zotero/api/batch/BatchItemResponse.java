package zotero.api.batch;

import zotero.api.Item;

/**
 * Represents the response from a batch item commit. It will contain each of the
 * items committed and their responses.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface BatchItemResponse extends BatchResponse<Item>
{
}
