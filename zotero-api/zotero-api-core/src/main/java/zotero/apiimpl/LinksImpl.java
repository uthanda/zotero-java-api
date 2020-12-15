package zotero.apiimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import zotero.api.Links;
import zotero.api.internal.rest.model.ZoteroRestLinks;

final class LinksImpl implements Links
{
	private Map<String, LinkImpl> links = new HashMap<>();

	@Override
	public boolean has(String key)
	{
		return links.containsKey(key);
	}

	@Override
	public LinkImpl get(String key)
	{
		return links.get(key);
	}

	@Override
	public LinkImpl create(String key)
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

		zl.links.putAll(links.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			return LinkImpl.from(e.getValue());
		})));

		return zl;
	}

}