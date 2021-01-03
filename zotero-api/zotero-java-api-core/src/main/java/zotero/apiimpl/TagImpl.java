package zotero.apiimpl;

import zotero.api.Links;
import zotero.api.Tag;
import zotero.api.constants.TagType;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class TagImpl implements Tag
{
	private final String tag;
	private final TagType type;
	private final int numItems;
	private final Links links;

	public TagImpl(String tag)
	{
		this(tag, TagType.TAG, 0, new LinksImpl());
	}
	
	public TagImpl(String tag, TagType type, int numItems, Links links)
	{
		this.tag = tag;
		this.type = type;
		this.numItems = numItems;
		this.links = links;
	}
	
	@Override
	public String getTag()
	{
		return tag;
	}

	@Override
	public TagType getType()
	{
		return type;
	}

	@Override
	public int getNumberItems()
	{
		return numItems;
	}

	@Override
	public Links getLinks()
	{
		return links;
	}
	
	public static TagImpl fromRest(ZoteroRestItem item, LibraryImpl library)
	{
		String tag = item.getTag();
		int numItems = ((Double)item.getMeta().get(ZoteroKeys.Meta.NUM_ITEMS)).intValue();
	
		Links links = LinksImpl.fromRest(item.getLinks());
		
		return new TagImpl(tag, TagType.TAG, numItems, links);
	}
}
