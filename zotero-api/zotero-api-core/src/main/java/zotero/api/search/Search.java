package zotero.api.search;

import java.util.List;

import zotero.api.constants.Sort;

public interface Search<S>
{
	S itemKey(String key);

	S quickSearch(String search);

	S since(Integer since);

	S tag(String tag);

	S notTag(String tag);

	S orTags(List<String> tags);
	
	S sort(Sort sort, Direction direction);
	
	S limit(Integer limit);
	
	S start(Integer start);
}