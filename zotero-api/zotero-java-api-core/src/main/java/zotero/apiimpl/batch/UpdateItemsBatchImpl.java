package zotero.apiimpl.batch;

import java.util.List;

import zotero.api.batch.UpdateItemsBatch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.BaseBuilder;
import zotero.apiimpl.rest.request.builders.PatchBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;

public class UpdateItemsBatchImpl extends ItemBatchImpl implements UpdateItemsBatch
{
	public UpdateItemsBatchImpl(LibraryImpl library)
	{
		super(library);
	}

	@Override
	public BaseBuilder<ZoteroPostResponse, ?, JSONRestResponseBuilder<ZoteroPostResponse>> getBuilder(List<ZoteroRestItem> list)
	{
		return PatchBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroPostResponse.class));
	}

	@Override
	protected SerializationMode getSerializationMode()
	{
		return SerializationMode.PARTIAL;
	}

}
