package zotero.api.collections;

import java.util.List;

import zotero.api.Creator;
import zotero.api.constants.CreatorType;

public interface CreatorsList extends List<Creator>
{
	void add(CreatorType type, String firstName, String lastName);
}
