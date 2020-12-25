package zotero.apiimpl.search;

import java.util.function.BiConsumer;

import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.QuickSearchMode;

import zotero.apiimpl.*;
import zotero.apiimpl.iterators.ZoteroItemIteratorImpl;

public abstract class ItemEndpointSearchImpl extends SearchBuilderImpl<ItemEndpointSearchImpl>
{
	private final Library library;
	private final String url;
	
	private Boolean includeTrashed;
	private QuickSearchMode mode;
	
	public ItemEndpointSearchImpl(Library library, String url)
	{
		this.library = library;
		this.url = url;
	}

	public ItemEndpointSearchImpl includeTrashed(boolean include)
	{
		this.includeTrashed = include;
		return this;
	}

	public ItemEndpointSearchImpl quickSearchMode(QuickSearchMode mode)
	{
		this.mode = mode;
		return this;
	}

	@Override
	public void apply(BiConsumer<String, String> params)
	{
		super.apply(params);

		if (includeTrashed != null)
		{
			params.accept("includeTrashed", includeTrashed.booleanValue() ? "1" : "0");
		}

		if (mode != null)
		{
			params.accept("qmode", mode.getZoteroName());
		}
	}
	
	public ItemIterator search()
	{
		GetBuilder<ZoteroRestItem[]> builder = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem[].class);
		this.apply(builder::queryParam);
		
		builder.url(url);
		
		RestResponse<ZoteroRestItem[]> response = ((LibraryImpl)library).performGet(builder);
		
		return new ZoteroItemIteratorImpl(response, library);
	}
}
