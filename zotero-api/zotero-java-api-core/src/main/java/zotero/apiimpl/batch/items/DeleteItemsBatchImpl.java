package zotero.apiimpl.batch.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import zotero.api.Item;
import zotero.api.batch.items.BatchItemResponse;
import zotero.api.batch.items.DeleteItemsBatch;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.request.builders.DeleteBuilder;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

public class DeleteItemsBatchImpl extends ItemBatchImpl implements DeleteItemsBatch
{
	private LibraryImpl library;

	public DeleteItemsBatchImpl(LibraryImpl library)
	{
		super(ZoteroRest.Batching.MAX_BATCH_COMMIT_COUNT);
		this.library = library;
	}

	@Override
	public BatchItemResponse commit() throws ZoteroRuntimeException
	{
		List<String> ids = new ArrayList<>();
		
		iterator().forEachRemaining(e -> ids.add(e.getItem().getKey()));
		
		DeleteBuilder<?,?> builder = DeleteBuilder.createBuilder(new SuccessResponseBuilder());
		
		builder.url(ZoteroRest.Items.ALL).queryParam("itemKey", ids.stream().collect(Collectors.joining(",")));
		
		RestResponse<?> response = library.performRequest(builder);
		
		iterator().forEachRemaining(handle -> ((ItemImpl)handle.getItem()).refresh(null));
		
		return null;
	}
}
