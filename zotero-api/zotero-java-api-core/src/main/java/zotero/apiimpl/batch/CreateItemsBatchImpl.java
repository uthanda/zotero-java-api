package zotero.apiimpl.batch;

import java.util.List;

import zotero.api.batch.CreateItemsBatch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;

public class CreateItemsBatchImpl extends ItemBatchImpl implements CreateItemsBatch
{
	public CreateItemsBatchImpl(LibraryImpl library)
	{
		super(library);
	}

	@Override
	public BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list)
	{
		return PostBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroPostResponse.class)).jsonObject(list).createWriteToken();
	}

	@Override
	protected SerializationMode getSerializationMode()
	{
		return SerializationMode.FULL;
	}
}
