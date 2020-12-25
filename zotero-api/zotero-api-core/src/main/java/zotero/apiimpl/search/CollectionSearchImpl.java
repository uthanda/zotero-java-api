package zotero.apiimpl.search;

import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.api.search.CollectionSearch;
import zotero.apiimpl.iterators.CollectionIteratorImpl;

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
}
