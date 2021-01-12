package zotero.apiimpl.batch.items;

import java.util.List;

import zotero.api.batch.items.CreateItemsBatch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;

public class CreateItemsBatchImpl extends SerializingItemBatchImpl implements CreateItemsBatch
{
	public CreateItemsBatchImpl(LibraryImpl library)
	{
		super(library, ZoteroRest.Batching.MAX_BATCH_COMMIT_COUNT);
	}

	public BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list)
	{
		return PostBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroPostResponse.class)).jsonObject(list).createWriteToken();
	}

	protected SerializationMode getSerializationMode()
	{
		return SerializationMode.FULL;
	}
}
