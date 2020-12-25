package zotero.apiimpl.search;

import java.util.function.BiConsumer;

import zotero.api.Library;
import zotero.api.constants.Sort;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.Direction;
import zotero.api.search.ItemEndpointSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.iterators.ZoteroItemIteratorImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.model.ZoteroRestItem;

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

	@Override
	public ItemEndpointSearch<ItemSearch, ItemIterator> sort(Sort sort, Direction order)
	{
		if (sort == Sort.NUM_ITEMS)
		{
			throw new IllegalStateException("Sort.NUM_ITEMS not valid for Item searches");
		}
		
		return super.sort(sort, order);
	}
}
