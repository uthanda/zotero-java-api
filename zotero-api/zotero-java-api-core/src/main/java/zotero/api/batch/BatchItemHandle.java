package zotero.api.batch;

import zotero.api.Item;

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
