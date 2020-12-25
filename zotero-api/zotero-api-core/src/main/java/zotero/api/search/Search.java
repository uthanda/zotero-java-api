package zotero.api.search;

import java.util.List;

public interface Search<S>
{
	S itemKey(String key);

	S quickSearch(String search);

	S since(Integer since);

	S tag(String tag);

	S notTag(String tag);

	S orTags(List<String> tags);
}