package zotero.apiimpl.search;

import static zotero.apiimpl.rest.ZoteroRest.Collections.ALL;

import zotero.api.constants.Sort;
import zotero.api.iterators.CollectionIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.Direction;
import zotero.api.search.ItemEndpointSearch;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.iterators.CollectionIteratorImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.response.RestResponse;

public class CollectionSearchImpl extends ItemEndpointSearchImpl<CollectionSearch, CollectionIterator> implements CollectionSearch
{
	public CollectionSearchImpl(LibraryImpl library)
	{
		super(library, ALL);
	}

	@Override
	public CollectionIterator search()
	{
		RestResponse<ZoteroRestItem[]> response = execute();

		CollectionIteratorImpl it = new CollectionIteratorImpl(library);
		
		it.setResponse(response);
		
		return it;
	}

	@Override
	public ItemEndpointSearch<CollectionSearch, CollectionIterator> sort(Sort sort, Direction order)
	{
		if (sort == Sort.NUM_ITEMS)
		{
			throw new IllegalStateException("Sort.NUM_ITEMS not valid for Collection searches");
		}

		return super.sort(sort, order);
	}
}
