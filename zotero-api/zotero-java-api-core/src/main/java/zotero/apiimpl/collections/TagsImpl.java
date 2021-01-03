package zotero.apiimpl.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zotero.api.Tag;
import zotero.api.collections.Tags;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.TagImpl;
import zotero.apiimpl.properties.PropertyListImpl;

public final class TagsImpl extends PropertyListImpl.ObservableList<Tag> implements Tags
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 855435163437169254L;

	public TagsImpl()
	{
		super(ZoteroKeys.Item.TAGS, null, false);
	}

	public TagsImpl(List<Tag> values)
	{
		super(ZoteroKeys.Item.TAGS, values, false);
	}

	public static List<Map<String, String>> toRest(Tags tags)
	{
		TagsImpl t = (TagsImpl) tags;

		List<Map<String, String>> zrt = new ArrayList<>();

		t.forEach(e -> {
			Map<String, String> map = new HashMap<>();
			map.put(ZoteroKeys.Tag.TAG, e.getTag());
			zrt.add(map);
		});

		return zrt;
	}

	@SuppressWarnings("unchecked")
	public static Tags fromRest(LibraryImpl library, Object value)
	{
		if (value instanceof Boolean && Boolean.FALSE.equals(value))
		{
			return new TagsImpl();
		}

		List<Tag> values = new ArrayList<>();

		((List<Map<String, Object>>) value).forEach(e -> values.add((new TagImpl((String) e.get(ZoteroKeys.Tag.TAG), library))));

		return new TagsImpl(values);
	}

	@Override
	public String toString()
	{
		return String.format("[Tags: tags:%s, dirty:%b]", super.toString(), this.isDirty());
	}
}