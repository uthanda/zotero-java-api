package zotero.apiimpl;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.collections.Links;
import zotero.api.constants.LinkType;
import zotero.apiimpl.rest.model.ZoteroRestLinks;

final class LinksImpl implements Links
{
	private Map<LinkType, LinkImpl> links = new EnumMap<>(LinkType.class);

	@Override
	public boolean has(LinkType key)
	{
		return links.containsKey(key);
	}

	@Override
	public LinkImpl get(LinkType key)
	{
		return links.get(key);
	}

	public LinkImpl create(LibraryImpl library, LinkType key)
	{
		if (links.containsKey(key))
		{
			throw new IllegalStateException("Key " + key + " already exists");
		}

		LinkImpl link = new LinkImpl(library);

		links.put(key, link);

		return link;
	}

	public static Links fromRest(LibraryImpl library, ZoteroRestLinks links)
	{
		LinksImpl zl = new LinksImpl();

		zl.links.putAll(links.entrySet().stream().collect(Collectors.toMap(e -> LinkType.fromZoteroType(e.getKey()), e -> LinkImpl.fromRest(library, e.getValue()))));

		return zl;
	}

}