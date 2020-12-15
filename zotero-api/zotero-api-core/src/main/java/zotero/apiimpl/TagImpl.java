package zotero.apiimpl;

import java.util.Map;

import zotero.api.Tag;

public final class TagImpl extends PropertiesItemImpl implements Tag
{
	private TagImpl(Map<String, Object> values)
	{
		super(values);
	}

	public static Tag fromMap(Map<String, Object> values)
	{
		return new TagImpl(values);
	}
}