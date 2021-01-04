package zotero.apiimpl.properties;

import zotero.api.collections.Tags;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.TagsImpl;

public class PropertyTagsImpl extends PropertyObjectImpl<Tags>
{
	public PropertyTagsImpl(Tags values)
	{
		super(ZoteroKeys.Item.TAGS, Tags.class, values);
	}

	@Override
	public Object toRestValue()
	{
		return TagsImpl.toRest((TagsImpl) getValue());
	}
	
	public static PropertyTagsImpl fromRest(LibraryImpl library, Object value)
	{
		Tags tags = TagsImpl.fromRest(library, value);
		
		return new PropertyTagsImpl(tags);
	}
}
