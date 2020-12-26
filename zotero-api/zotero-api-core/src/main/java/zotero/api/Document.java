package zotero.api;

import zotero.api.collections.Creators;
import zotero.api.constants.ItemType;
import zotero.api.iterators.ItemIterator;

public interface Document extends Item
{
	ItemIterator fetchChildren();
	
	Creators getCreators();

	void changeItemType(ItemType type);
}
