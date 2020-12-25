package zotero.apiimpl.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import zotero.api.constants.ItemType;
import zotero.api.constants.Sort;
import zotero.api.search.Direction;
import zotero.api.search.Search;

public class SearchImpl<S> implements Search<S>
{
	private Set<String> itemKeys = new LinkedHashSet<>();
	private String quickSearch;
	private Integer since;
	private Set<String> tags = new LinkedHashSet<>();
	private Sort sort;
	private Direction direction;
	private Integer limit;
	private Integer start;

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
		
		if(sort != null)
		{
			params.accept("sort", sort.getZoteroName());
		}
		
		if(direction != null)
		{
			params.accept("direction", direction.getZoteroName());
		}
		
		if(limit != null)
		{
			params.accept("limit", limit.toString());
		}
		
		if(start != null)
		{
			params.accept("start", start.toString());
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

	@SuppressWarnings("unchecked")
	@Override
	public S sort(Sort sort, Direction order)
	{
		this.sort = sort;
		this.direction = order;
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S limit(Integer limit)
	{
		this.limit = limit;
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S start(Integer start)
	{
		this.start = start;
		return (S) this;
	}
}