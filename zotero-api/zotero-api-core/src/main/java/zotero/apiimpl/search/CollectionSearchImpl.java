package zotero.apiimpl.search;

import zotero.api.Library;
import zotero.api.constants.Sort;
import zotero.api.iterators.CollectionIterator;
import zotero.api.search.CollectionSearch;
import zotero.api.search.Direction;
import zotero.api.search.ItemEndpointSearch;
import zotero.apiimpl.iterators.CollectionIteratorImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class CollectionSearchImpl extends ItemEndpointSearchImpl<CollectionSearch, CollectionIterator> implements CollectionSearch
{
	public CollectionSearchImpl(Library library)
	{
		super(library, ZoteroRestPaths.COLLECTIONS);
	}

	@Override
	public CollectionIterator search()
	{
		RestResponse<ZoteroRestItem[]> response = execute();

		return new CollectionIteratorImpl(response, library);
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
