package zotero.apiimpl.search;

import static zotero.api.constants.ZoteroKeys.Search.INCLUDE_TRASHED;
import static zotero.api.constants.ZoteroKeys.Search.INCLUDE_TRASHED_FALSE;
import static zotero.api.constants.ZoteroKeys.Search.INCLUDE_TRASHED_TRUE;
import static zotero.apiimpl.rest.ZoteroRest.Items.ALL;

import java.util.function.BiConsumer;

import zotero.api.constants.Sort;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.Direction;
import zotero.api.search.ItemEndpointSearch;
import zotero.api.search.ItemSearch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.iterators.ItemIteratorImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.response.RestResponse;

public class ItemSearchImpl extends ItemEndpointSearchImpl<ItemSearch, ItemIterator> implements ItemSearch
{
	private Boolean includeTrashed;

	public ItemSearchImpl(LibraryImpl library)
	{
		super(library, ALL);
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

		return new ItemIteratorImpl(response, library);
	}

	@Override
	public void apply(BiConsumer<String, String> params)
	{
		super.apply(params);

		if (includeTrashed != null)
		{
			params.accept(INCLUDE_TRASHED, includeTrashed.booleanValue() ? INCLUDE_TRASHED_TRUE : INCLUDE_TRASHED_FALSE);
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
