package zotero.apiimpl.iterators;

import java.util.Arrays;
import java.util.stream.Collectors;

import zotero.api.Document;
import zotero.api.constants.ItemType;
import zotero.api.iterators.DocumentIterator;
import zotero.apiimpl.DocumentImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.request.builders.GetBuilder;

public final class DocumentIteratorImpl extends ZoteroIteratorImpl<Document> implements DocumentIterator
{
	public DocumentIteratorImpl(LibraryImpl library)
	{
		super(DocumentImpl::fromRest, library);
	}

	@Override
	public void addQueryParams(GetBuilder<?,?> builder)
	{
		String types = Arrays
				.asList(ItemType.values())
				.stream()
				.filter(item -> ItemType.ATTACHMENT != item && ItemType.NOTE != item)
				.map(ItemType::getZoteroName)
				.collect(Collectors.joining(" || "));
		
		builder.queryParam("itemType", types);
	}
}