package zotero.api.search;

import zotero.api.iterators.CollectionIterator;

public interface CollectionSearch extends ItemEndpointSearch<CollectionSearch, CollectionIterator>
{
	CollectionIterator search();
}