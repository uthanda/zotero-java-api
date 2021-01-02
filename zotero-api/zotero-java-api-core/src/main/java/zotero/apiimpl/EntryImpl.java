package zotero.apiimpl;

import java.util.Arrays;

import zotero.api.Entry;
import zotero.api.Library;
import zotero.api.Links;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.rest.ZoteroRest.Collections;
import zotero.apiimpl.rest.ZoteroRest.Items;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroPostResponse;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.model.ZoteroRestLinks;
import zotero.apiimpl.rest.request.builders.DeleteBuilder;
import zotero.apiimpl.rest.request.builders.PatchBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

abstract class EntryImpl extends PropertiesItemImpl implements Entry
{
	private LibraryImpl library;

	private Links links;

	private String key;

	private Integer version;

	private boolean deleted = false;

	protected EntryImpl(LibraryImpl library)
	{
		super();
		this.library = library;
		this.version = null;
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
	public final Integer getVersion()
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

		DeleteBuilder<?, ?> builder = DeleteBuilder.createBuilder(new SuccessResponseBuilder());

		if (this instanceof ItemImpl)
		{
			builder.itemKey(this.getKey()).url(Items.SPECIFIC);
		}
		else if (this instanceof CollectionImpl)
		{
			builder.collectionKey(this.getKey()).url(Collections.SPECIFIC);
		}
		else
		{
			throw new IllegalStateException("Delete unimplmented for item type " + this.getClass().getSimpleName());
		}

		builder.lastVersion(version);
		
		library.performRequest(builder);

		this.deleted = true;
	}

	public static void loadLinks(EntryImpl entry, ZoteroRestLinks links)
	{
		entry.links = LinksImpl.from(links);
	}

	protected void refresh(ZoteroRestItem item)
	{
		// Refresh the links
		this.links = LinksImpl.from(item.getLinks());
		this.key = item.getKey();
		this.version = item.getVersion();
		super.refresh((LibraryImpl) library, item);
	}

	protected ZoteroRestItem executeCreate(String url, ZoteroRestItem item) throws ZoteroRuntimeException
	{
		PostBuilder<ZoteroPostResponse, ?> builder = PostBuilder.createBuilder(new JSONRestResponseBuilder<>(ZoteroPostResponse.class));

		// New items need to be posted in a JSON array
		builder.jsonObject(Arrays.asList(item)).url(url);

		RestResponse<ZoteroPostResponse> resp = library.performRequest((builder));

		ZoteroPostResponse response = resp.getResponse();

		if (response.getSuccess().isEmpty())
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, response.getFailed().get("0").getMessage());
		}

		item = response.getSuccessful().get("0");

		return item;
	}

	protected void executeUpdate(String url, URLParameter param, String key, ZoteroRestItem item)
	{
		PatchBuilder<Void, ?> builder = PatchBuilder.createBuilder(new SuccessResponseBuilder());

		if (item.getVersion() == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, "Version must be provided");
		}
		
		if (key == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.API_ERROR, "Key must be provided");
		}

		builder.jsonObject(item).url(url).urlParam(param, key);
		
		// The request should return a true response which we can ignore.  IF there are errors
		// the performRequest call will throw it as an exception
		this.version = library.performRequest(builder).getLastModifyVersion();
	}
}