package zotero.apiimpl.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zotero.api.collections.Tags;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.properties.PropertyListImpl;

public final class TagsImpl extends PropertyListImpl.ObservableList<String> implements Tags
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 855435163437169254L;

	public TagsImpl()
	{
		super(ZoteroKeys.TAGS, null, false);
	}

	private TagsImpl(List<String> values)
	{
		super(ZoteroKeys.TAGS, values, false);
	}

	public static List<Map<String, String>> toRest(Tags tags)
	{
		TagsImpl t = (TagsImpl) tags;

		List<Map<String, String>> zrt = new ArrayList<>();

		t.forEach(e -> {
			Map<String, String> map = new HashMap<>();
			map.put("tag", e);
			zrt.add(map);
		});

		return zrt;
	}

	public static Tags from(List<Map<String, Object>> jsonTags)
	{
		List<String> values = new ArrayList<>();

		jsonTags.forEach(e -> values.add((String) e.get("tag")));

		return new TagsImpl(values);
	}
}