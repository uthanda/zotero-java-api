package zotero.api.batch.collections;

import zotero.api.Collection;
import zotero.api.batch.BatchResponse;

/**
 * Represents the response from a batch item commit. It will contain each of the
 * items committed and their responses.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface BatchCollectionResponse extends BatchResponse<Collection>
{
}
