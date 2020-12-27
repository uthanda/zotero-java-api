package zotero.apiimpl.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import zotero.api.Library;
import zotero.api.constants.ItemType;
import zotero.api.search.ItemEndpointSearch;
import zotero.api.search.QuickSearchMode;

import zotero.apiimpl.*;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public abstract class ItemEndpointSearchImpl<S,T> extends SearchImpl<ItemEndpointSearch<S,T>> implements ItemEndpointSearch<S,T>
{
	protected final Library library;
	private final String url;
	
	private QuickSearchMode mode;
	private Set<String> itemTypes = new LinkedHashSet<>();
	
	public ItemEndpointSearchImpl(Library library, String url)
	{
		this.library = library;
		this.url = url;
	}

	@SuppressWarnings("unchecked")
	@Override
	public S quickSearchMode(QuickSearchMode mode)
	{
		this.mode = mode;
		return (S) this;
	}

	@Override
	public void apply(BiConsumer<String, String> params)
	{
		super.apply(params);

		itemTypes.forEach(type -> params.accept("itemType", type));
		
		if (mode != null)
		{
			params.accept("qmode", mode.getZoteroName());
		}
	}
	
	protected RestResponse<ZoteroRestItem[]> execute()
	{
		GetBuilder<ZoteroRestItem[],?> builder = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class));
		this.apply(builder::queryParam);
		
		builder.url(url);
		
		RestResponse<ZoteroRestItem[]> response = ((LibraryImpl)library).performRequest(builder);
		return response;
	}

	@SuppressWarnings("unchecked")
	public S itemType(ItemType itemType)
	{
		this.itemTypes.add(escapeItem(itemType));
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S notItemType(ItemType itemType)
	{
		this.itemTypes.add("-" + escapeItem(itemType));
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S orItemTypes(List<ItemType> itemTypes)
	{
		String combined = itemTypes.stream().map(SearchImpl::escapeItem).collect(Collectors.joining("||"));
	
		this.itemTypes.add(combined);
		return (S) this;
	}
}
