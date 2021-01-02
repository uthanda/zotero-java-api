package zotero.apiimpl.collections;

import static zotero.api.constants.ZoteroKeys.Item.TAGS;
import static zotero.api.constants.ZoteroKeys.Tag.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zotero.api.collections.Tags;
import zotero.apiimpl.properties.PropertyListImpl;

public final class TagsImpl extends PropertyListImpl.ObservableList<String> implements Tags
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 855435163437169254L;

	public TagsImpl()
	{
		super(TAGS, null, false);
	}

	public TagsImpl(List<String> values)
	{
		super(TAGS, values, false);
	}

	public static List<Map<String, String>> toRest(Tags tags)
	{
		TagsImpl t = (TagsImpl) tags;

		List<Map<String, String>> zrt = new ArrayList<>();

		t.forEach(e -> {
			Map<String, String> map = new HashMap<>();
			map.put(TAG, e);
			zrt.add(map);
		});

		return zrt;
	}

	@SuppressWarnings("unchecked")
	public static Tags fromRest(Object value)
	{
		if (value instanceof Boolean && Boolean.FALSE.equals(value))
		{
			return new TagsImpl();
		}

		List<String> values = new ArrayList<>();

		((List<Map<String, Object>>) value).forEach(e -> values.add((String) e.get(TAG)));

		return new TagsImpl(values);
	}

	@Override
	public String toString()
	{
		return String.format("[Tags: tags:%s, dirty:%b]", super.toString(), this.isDirty());
	}
}