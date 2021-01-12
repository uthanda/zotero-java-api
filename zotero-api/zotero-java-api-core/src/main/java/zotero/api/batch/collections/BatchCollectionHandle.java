package zotero.api.batch.collections;

import zotero.api.Collection;
import zotero.api.batch.BatchHandle;

/**
 * Represents a handle to a specific item in the batch. This item can be a
 * {@link zotero.api.Document}, {@link zotero.api.Attachment} or
 * {@link zotero.api.Note}.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public interface BatchCollectionHandle extends BatchHandle<Collection>
{

}
