package zotero.apiimpl.iterators;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import zotero.api.Library;
import zotero.api.iterators.ZoteroIterator;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.RestResponse;
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.model.ZoteroRestItem;

class ZoteroIteratorImpl<T> implements ZoteroIterator<T>
{
	private int totalCount;
	private ZoteroRestItem[] page;
	private RestResponse<ZoteroRestItem[]> response;
	private int index = 0;
	private BiFunction<ZoteroRestItem, Library, T> builder;
	private Library library;

	protected ZoteroIteratorImpl(RestResponse<ZoteroRestItem[]> response, BiFunction<ZoteroRestItem, Library, T> builder, Library library)
	{
		this.totalCount = response.getTotalResults();
		this.page = response.getResponse();
		this.response = response;
		this.builder = builder;
		this.library = library;
	}

	@Override
	public boolean hasNext()
	{
		return index < page.length || response.getLink("next") != null;
	}

	@Override
	public T next()
	{
		if (index >= page.length)
		{
			String nextLink = response.getLink("next");
			
			if (nextLink == null)
			{
				throw new NoSuchElementException();
			}

			GetBuilder<ZoteroRestItem[]> b = ZoteroRestGetRequest.Builder.createBuilder(ZoteroRestItem[].class).specialUrl(nextLink);
			
			response = ((LibraryImpl)library).performGet(b);
			
			page = response.getResponse();
			index = 0;
		}

		return builder.apply(page[index++], library);
	}

	@Override
	public final int getTotalCount()
	{
		return totalCount;
	}
}