package zotero.apiimpl.iterators;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import zotero.api.Entry;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.CollectionImpl;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.EntryIterator;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;

public class RelatedItemIterator implements EntryIterator<Entry>
{
	private Set<String> urls;
	private Iterator<String> i;
	private LibraryImpl library;

	public RelatedItemIterator(LibraryImpl library, Set<String> urls)
	{
		this.urls = urls;
		this.i = urls.iterator();
		this.library = library;
	}

	@Override
	public boolean hasNext()
	{
		return i.hasNext();
	}

	@Override
	public Entry next()
	{
		try
		{
			URL url = new URL(i.next());
			String path = url.getPath();
			
			RestResponse<ZoteroRestItem> response = library.performRequest(GetBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroRestItem.class)).entryUrl(path));
			
			if(path.contains("items"))
			{
				return ItemImpl.fromItem(response.getResponse(), library);
			}
			else
			{
				return CollectionImpl.fromItem(response.getResponse(), library);
			}
		}
		catch (MalformedURLException e)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.IO_ERROR, e.getMessage(), e);
		}
		
	}

	@Override
	public int getTotalCount()
	{
		return urls.size();
	}

}
