package zotero.apiimpl.search;

import java.util.function.BiConsumer;

import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.iterators.ZoteroItemIteratorImpl;

public class ItemSearchImpl extends ItemEndpointSearchImpl<ItemSearch, ItemIterator> implements ItemSearch
{
	private Boolean includeTrashed;

	public ItemSearchImpl(Library library)
	{
		super(library, ZoteroRestPaths.ITEMS);
	}

	@Override
	public ItemSearch includeTrashed(boolean include)
	{
		this.includeTrashed = include;
		return this;
	}

	@Override
	public ItemIterator search()
	{
		RestResponse<ZoteroRestItem[]> response = execute();

		return new ZoteroItemIteratorImpl(response, library);
	}

	@Override
	public void apply(BiConsumer<String, String> params)
	{
		super.apply(params);

		if (includeTrashed != null)
		{
			params.accept("includeTrashed", includeTrashed.booleanValue() ? "1" : "0");
		}
	}
}
