package zotero.apiimpl.attachments;

import java.io.InputStream;

import zotero.api.Library;
import zotero.api.attachments.Attachment;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.Entity;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class AttachmentImpl extends ItemImpl implements Attachment
{
	public AttachmentImpl(ZoteroRestItem jsonItem, Library library)
	{
		super(jsonItem, library);
	}

	public AttachmentImpl(LinkMode mode, Library library)
	{
		super(mode, library);
	}
	
	@Override
	public LinkMode getType()
	{
		return (LinkMode) getProperties().getProperty(ZoteroKeys.LINK_MODE).getValue();
	}

	@Override
	public String getCharset()
	{
		return getProperties().getString(zotero.api.constants.ZoteroKeys.Attachment.CHARSET);
	}

	@Override
	public void setCharset(String charset)
	{
		getProperties().putValue(zotero.api.constants.ZoteroKeys.Attachment.CHARSET, charset);
	}

	@Override
	public String getContentType()
	{
		return getProperties().getString(zotero.api.constants.ZoteroKeys.Attachment.CONTENT_TYPE);
	}

	@Override
	public void setContentType(String type)
	{
		getProperties().putValue(zotero.api.constants.ZoteroKeys.Attachment.CONTENT_TYPE, type);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, Library library)
	{
		return new AttachmentImpl(jsonItem, library);
	}

	@Override
	public InputStream retrieveContent()
	{
		if (getType() != LinkMode.IMPORTED_FILE)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.INVALID_ATTACHMENT_TYPE,
					"Attachment must be of type IMPORTED_FILE to support retrieveContent()");
		}

		GetBuilder<InputStream> builder = ZoteroRestGetRequest.Builder.createBuilder(InputStream.class);
		builder.url(ZoteroRestPaths.ITEM_FILE).urlParam(Entity.KEY, getKey());
		
		return ((LibraryImpl)getLibrary()).performGet(builder).getResponse();
	}
}
