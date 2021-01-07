package zotero.apiimpl.iterators;

import static zotero.apiimpl.rest.ZoteroRest.Link.NEXT;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import zotero.api.iterators.ZoteroIterator;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public abstract class ZoteroIteratorImpl<T> implements ZoteroIterator<T>
{
	private int totalCount;
	private ZoteroRestItem[] page;
	private RestResponse<ZoteroRestItem[]> response;
	private int index = 0;
	private BiFunction<LibraryImpl, ZoteroRestItem, T> builder;
	private LibraryImpl library;

	protected ZoteroIteratorImpl(BiFunction<LibraryImpl, ZoteroRestItem, T> builder, LibraryImpl library)
	{
		this.builder = builder;
		this.library = library;
	}

	public void setResponse(RestResponse<ZoteroRestItem[]> response)
	{
		this.response = response;
		this.totalCount = response.getTotalResults();
		this.page = response.getResponse();
	}

	@Override
	public boolean hasNext()
	{
		return index < page.length || response.getLink(NEXT) != null;
	}

	@Override
	public T next()
	{
		// TODO fix the bug here where we're returning null when we shouldnt
		if (index >= page.length)
		{
			String nextLink = response.getLink(NEXT);

			if (nextLink == null)
			{
				throw new NoSuchElementException();
			}

			GetBuilder<ZoteroRestItem[], ?> b = GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem[].class)).specialUrl(nextLink);

			response = library.performRequest(b);

			page = response.getResponse();

			// Apparently it'll return a value of []
			if (page.length == 0)
			{
				return null;
			}

			index = 0;
		}

		return builder.apply(library, page[index++]);
	}

	@Override
	public final int getTotalCount()
	{
		return totalCount;
	}
	
	public abstract void addQueryParams(GetBuilder<?,?> builder);
}