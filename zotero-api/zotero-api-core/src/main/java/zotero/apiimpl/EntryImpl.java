package zotero.apiimpl;

import zotero.api.Entry;
import zotero.api.Library;
import zotero.api.Links;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.model.ZoteroRestLinks;
import zotero.apiimpl.rest.request.builders.DeleteBuilder;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

abstract class EntryImpl extends PropertiesItemImpl implements Entry
{
	private Library library;

	private Links links;

	private String key;

	private int version;

	private boolean deleted = false;

	protected EntryImpl(LibraryImpl library)
	{
		super();
		this.library = library;
	}

	protected EntryImpl(ItemType type, LibraryImpl library)
	{
		super(type);
		this.library = library;
	}
	
	protected EntryImpl(LinkMode mode, LibraryImpl library)
	{
		super(ItemType.ATTACHMENT, mode);
		this.library = library;
	}

	EntryImpl(ZoteroRestItem item, LibraryImpl library)
	{
		super(library, item);
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
		checkDeletionStatus();
		
		return key;
	}

	@Override
	public final int getVersion()
	{
		checkDeletionStatus();
		
		return version;
	}

	@Override
	public final Library getLibrary()
	{
		checkDeletionStatus();
		
		return library;
	}

	@Override
	public final Links getLinks()
	{
		checkDeletionStatus();
		
		return links;
	}

	protected final void checkDeletionStatus()
	{
		if (deleted)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.OBJECT_DELETED, "Object was deleted");
		}
	}

	@Override
	public void delete()
	{
		checkDeletionStatus();
	
		DeleteBuilder<?,?> builder = DeleteBuilder.createBuilder(new SuccessResponseBuilder());
		builder.itemKey(this.getKey()).url(getDeletePath());
		
		((LibraryImpl)getLibrary()).performRequest(builder);
		this.deleted = true;
	}

	abstract String getDeletePath();

	public static void loadLinks(EntryImpl entry, ZoteroRestLinks links)
	{
		entry.links = LinksImpl.from(links);
	}
}