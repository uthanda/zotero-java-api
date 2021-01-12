package zotero.apiimpl.batch.items;

import java.util.List;

import zotero.api.batch.items.UpdateItemsBatch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.request.builders.PatchBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;

public class UpdateItemsBatchImpl extends SerializingItemBatchImpl implements UpdateItemsBatch
{
	public UpdateItemsBatchImpl(LibraryImpl library)
	{
		super(library, ZoteroRest.Batching.MAX_BATCH_COMMIT_COUNT);
	}

	public BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list)
	{
		return PatchBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroPostResponse.class));
	}

	protected SerializationMode getSerializationMode()
	{
		return SerializationMode.PARTIAL;
	}
}
