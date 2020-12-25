package zotero.apiimpl;

import zotero.api.Entry;
import zotero.api.Library;
import zotero.api.Links;
import zotero.api.constants.ItemType;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.internal.rest.model.ZoteroRestLinks;

abstract class EntryImpl extends PropertiesItemImpl implements Entry
{
	private Library library;

	private Links links;

	private String key;

	private int version;

	protected EntryImpl(Library library)
	{
		super();
		this.library = library;
	}

	protected EntryImpl(ItemType type, Library library)
	{
		super(type);
		this.library = library;
	}

	EntryImpl(ZoteroRestItem item, Library library)
	{
		super(item);
		this.key = item.getKey();
		this.version = item.getVersion();
		this.library = library;
		this.links = LinksImpl.from(item.getLinks());
	}

	@Override
	public int hashCode()
	{
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof EntryImpl))
		{
			return false;
		}

		if (obj.getClass() != this.getClass())
		{
			return false;
		}

		return key.equals(((EntryImpl) obj).key);
	}

	@Override
	public final String getKey()
	{
		return key;
	}

	@Override
	public final int getVersion()
	{
		return version;
	}

	@Override
	public final Library getLibrary()
	{
		return library;
	}

	@Override
	public final Links getLinks()
	{
		return links;
	}

	public static void loadLinks(EntryImpl entry, ZoteroRestLinks links)
	{
		entry.links = LinksImpl.from(links);
	}
}