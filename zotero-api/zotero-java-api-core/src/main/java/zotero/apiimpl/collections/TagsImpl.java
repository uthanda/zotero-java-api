package zotero.apiimpl.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import zotero.api.Tag;
import zotero.api.collections.Tags;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.TagImpl;

public final class TagsImpl implements Tags
{
	private boolean isDirty = false;
	private boolean cleared = false;
	private Set<Tag> tags;

	public TagsImpl()
	{
		tags = new LinkedHashSet<>();
	}

	public TagsImpl(List<Tag> values)
	{
		tags = new LinkedHashSet<>(values);
	}

	public static Object toRest(TagsImpl tags)
	{
		if (tags.cleared)
		{
			return Boolean.FALSE;
		}

		//@formatter:off
		return tags.tags
				.stream()
				.map(TagImpl::toRest)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@SuppressWarnings("unchecked")
	public static Tags fromRest(LibraryImpl library, Object value)
	{
		if (value instanceof Boolean && Boolean.FALSE.equals(value))
		{
			return new TagsImpl();
		}

		List<Tag> values = new ArrayList<>();

		((List<Map<String, Object>>) value).forEach(e -> values.add((new TagImpl((String) e.get(ZoteroKeys.TagKeys.TAG), library))));

		return new TagsImpl(values);
	}

	@Override
	public String toString()
	{
		return String.format("[Tags: tags:%s, dirty:%b]", super.toString(), this.isDirty());
	}

	@Override
	public boolean isDirty()
	{
		return isDirty;
	}

	@Override
	public int size()
	{
		return tags.size();
	}

	@Override
	public void add(Tag tag)
	{
		this.tags.add(tag);
		this.cleared = false;
		this.isDirty = true;
	}

	@Override
	public Iterator<Tag> iterator()
	{
		return tags.iterator();
	}

	@Override
	public void remove(Tag tag)
	{
		tags.remove(tag);
		this.cleared = false;
		this.isDirty = true;
	}

	@Override
	public void clear()
	{
		this.cleared = true;
		this.isDirty = true;
		this.tags.clear();
	}
}