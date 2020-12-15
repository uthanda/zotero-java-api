package zotero.apiimpl;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import zotero.api.Library;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.ZoteroIterator;

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
		return index < page.length || response.hasNext();
	}

	@Override
	public T next()
	{
		if (index >= page.length)
		{
			if (!response.hasNext())
			{
				throw new NoSuchElementException();
			}

			try
			{
				response = response.next();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

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