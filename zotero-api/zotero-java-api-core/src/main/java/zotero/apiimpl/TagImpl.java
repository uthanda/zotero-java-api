package zotero.apiimpl;

import java.util.HashMap;
import java.util.Map;

import zotero.api.Tag;
import zotero.api.collections.Links;
import zotero.api.constants.TagType;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.iterators.TagIterator;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class TagImpl implements Tag
{
	private final String tag;

	private TagType type;
	private Integer numItems;
	private Links links;
	private LibraryImpl library;

	public TagImpl(String tag, LibraryImpl library)
	{
		this(tag, TagType.USER, 0, null, library);
	}

	public TagImpl(String tag, TagType type, int numItems, Links links, LibraryImpl library)
	{
		this.tag = tag;
		this.type = type;
		this.numItems = numItems;
		this.links = links;
		this.library = library;
	}

	@Override
	public String getTag()
	{
		return tag;
	}

	@Override
	public TagType getType()
	{
		if (type == null)
		{
			refresh();
		}

		return type;
	}

	@Override
	public int getNumberItems()
	{
		if (numItems == null)
		{
			refresh();
		}

		return numItems;
	}

	@Override
	public Links getLinks()
	{
		if (links == null)
		{
			refresh();
		}

		return links;
	}

	@Override
	public void refresh()
	{
		TagIterator ti = library.fetchTag(tag);

		if (ti.getTotalCount() != 1)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.INVALID_TAG_RESPONSE, "Expected only 1 item and found " + ti.getTotalCount());
		}

		Tag info = ti.next();

		this.type = info.getType();
		this.links = info.getLinks();
		this.numItems = info.getNumberItems();
	}

	public static Map<String,Object> toRest(Tag tag)
	{
		Map<String,Object> map = new HashMap<>();
		
		map.put(ZoteroKeys.TagKeys.TAG, tag.getTag());
		map.put(ZoteroKeys.TagKeys.TYPE, tag.getType().getZoteroType());
		
		return map;
	}
	
	public static TagImpl fromRest(LibraryImpl library, ZoteroRestItem item)
	{
		String tag = item.getTag();
		int numItems = ((Double) item.getMeta().get(ZoteroKeys.MetaKeys.NUM_ITEMS)).intValue();
		int type = ((Double) item.getMeta().get(ZoteroKeys.MetaKeys.TYPE)).intValue();

		Links links = LinksImpl.fromRest(library, item.getLinks());

		return new TagImpl(tag, TagType.fromType(type), numItems, links, library);
	}
	
	@Override
	public int hashCode()
	{
		return type.hashCode() | tag.hashCode();
	}
	  
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof TagImpl))
		{
			return false;
		}
		
		TagImpl other = (TagImpl) obj;
		
		return other.type == type && other.tag.equals(this.tag);
	}
	
	@Override
	public String toString()
	{
		return String.format("[Tag type:%s, tag:%s]", type, tag);
	}
}
