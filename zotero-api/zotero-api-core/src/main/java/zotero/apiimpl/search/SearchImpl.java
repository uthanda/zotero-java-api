package zotero.apiimpl.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import zotero.api.constants.ItemType;
import zotero.api.search.Search;

public class SearchImpl<S> implements Search<S>
{
	private Set<String> itemKeys = new LinkedHashSet<>();
	private String quickSearch;
	private Integer since;
	private Set<String> tags = new LinkedHashSet<>();

	@Override
	@SuppressWarnings("unchecked")
	public S itemKey(String key)
	{
		itemKeys.add(escapeItem(key));
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S quickSearch(String search)
	{
		this.quickSearch = search;
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S since(Integer since)
	{
		this.since = since;
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S tag(String tag)
	{
		this.tags.add(escapeItem(tag));
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S notTag(String tag)
	{
		this.tags.add("-" + escapeItem(tag));
		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S orTags(List<String> tags)
	{
		String combined = tags.stream().map(SearchImpl::escapeItem).collect(Collectors.joining("||"));

		this.tags.add(combined);
		return (S) this;
	}

	public void apply(BiConsumer<String, String> params)
	{
		if (!itemKeys.isEmpty())
		{
			params.accept("itemKey", itemKeys.stream().collect(Collectors.joining(",")));
		}

		tags.forEach(tag -> params.accept("tag", tag));

		if (quickSearch != null)
		{
			params.accept("q", quickSearch);
		}

		if (since != null)
		{
			params.accept("since", since.toString());
		}
	}

	public static String escapeItem(String item)
	{
		return item.replace("-", "\\-");
	}

	public static String escapeItem(ItemType item)
	{
		return escapeItem(item.getZoteroName());
	}
}