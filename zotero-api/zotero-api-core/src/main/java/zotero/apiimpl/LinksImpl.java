package zotero.apiimpl;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.Links;
import zotero.api.constants.LinkType;
import zotero.api.internal.rest.model.ZoteroRestLinks;

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

	@Override
	public LinkImpl create(LinkType key)
	{
		// TODO key checking should go here
		if (links.containsKey(key))
		{
			throw new IllegalStateException("Key " + key + " already exists");
		}

		LinkImpl link = new LinkImpl();

		links.put(key, link);

		return link;
	}

	public static Links from(ZoteroRestLinks links)
	{
		LinksImpl zl = new LinksImpl();

		zl.links.putAll(links.entrySet().stream().collect(Collectors.toMap(e -> LinkType.fromZoteroType(e.getKey()), e -> LinkImpl.from(e.getValue()))));

		return zl;
	}

}