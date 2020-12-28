package zotero.api.search;

import zotero.api.iterators.ItemIterator;

public interface ItemSearch extends ItemEndpointSearch<ItemSearch,ItemIterator>
{
	ItemIterator search();

	ItemSearch includeTrashed(boolean include);
}