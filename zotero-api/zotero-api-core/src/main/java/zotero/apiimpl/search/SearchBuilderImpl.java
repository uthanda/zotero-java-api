package zotero.apiimpl.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import zotero.api.constants.ItemType;

public class SearchBuilderImpl<B>
{
	private Set<String> itemTypes = new LinkedHashSet<>();
	private Set<String> itemKeys = new LinkedHashSet<>();
	private String quickSearch;
	private Integer since;
	private Set<String> tags = new LinkedHashSet<>();

	@SuppressWarnings("unchecked")
	public B itemKey(String key)
	{
		itemKeys.add(escapeItem(key));
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B quickSearch(String search)
	{
		this.quickSearch = search;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B since(Integer since)
	{
		this.since = since;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B tag(String tag)
	{
		this.tags.add(escapeItem(tag));
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B notTag(String tag)
	{
		this.tags.add("-" + escapeItem(tag));
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B orTags(List<String> tags)
	{
		String combined = tags.stream().map(SearchBuilderImpl::escapeItem).collect(Collectors.joining("||"));

		this.tags.add(combined);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B itemType(ItemType itemType)
	{
		this.itemTypes.add(escapeItem(itemType));
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B notItemType(ItemType itemType)
	{
		this.itemTypes.add("-" + escapeItem(itemType));
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B orItemTypes(List<ItemType> itemTypes)
	{
		String combined = itemTypes.stream().map(SearchBuilderImpl::escapeItem).collect(Collectors.joining("||"));
	
		this.itemTypes.add(combined);
		return (B) this;
	}

	public void apply(BiConsumer<String, String> params)
	{
		if (!itemKeys.isEmpty())
		{
			params.accept("itemKey", itemKeys.stream().collect(Collectors.joining(",")));
		}

		itemTypes.forEach(type -> params.accept("itemType", type));
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