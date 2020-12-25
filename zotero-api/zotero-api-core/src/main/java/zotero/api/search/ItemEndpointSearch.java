package zotero.api.search;

import java.util.List;

import zotero.api.constants.ItemType;

public interface ItemEndpointSearch<S,T> extends Search<ItemEndpointSearch<S,T>>
{
	S quickSearchMode(QuickSearchMode mode);
	
	S itemType(ItemType itemType);
	
	S notItemType(ItemType itemType);
	
	S orItemTypes(List<ItemType> itemTypes);

	T search();
}