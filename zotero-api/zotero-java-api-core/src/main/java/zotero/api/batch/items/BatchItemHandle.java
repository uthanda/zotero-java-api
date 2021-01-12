package zotero.api.batch.items;

import zotero.api.Item;
import zotero.api.batch.BatchHandle;

/**
 * Represents a handle to a specific item in the batch. This item can be a
 * {@link zotero.api.Document}, {@link zotero.api.Attachment} or
 * {@link zotero.api.Note}.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface BatchItemHandle extends BatchHandle<Item>
{

}
