package zotero.apiimpl;

import java.io.InputStream;
import java.util.Map;

import zotero.api.Attachment;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.constants.ZoteroKeys.Entity;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.StreamResponseBuilder;

public class AttachmentImpl extends ItemImpl implements Attachment
{
	private boolean pending = false;
	private InputStream is;

	public AttachmentImpl(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		super(jsonItem, library);
	}

	public AttachmentImpl(LinkMode mode, LibraryImpl library)
	{
		super(mode, library);
	}

	/**
	 * Creates a new attachment in pending state. It sets the parent key as a
	 * read-only property in the properties collection.
	 * 
	 * @param mode
	 * @param library
	 * @param parentKey
	 */
	public AttachmentImpl(LinkMode mode, LibraryImpl library, String parentKey)
	{
		super(mode, library);
		this.pending = true;
		// Create the parent key property and set it to read-only.
		((PropertiesImpl) getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.Attachment.PARENT_ITEM, parentKey, true));
	}

	@Override
	public LinkMode getLinkMode()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return (LinkMode) getProperties().getProperty(ZoteroKeys.LINK_MODE).getValue();
	}

	private void checkPendingStatus()
	{
		if (pending)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_PENDING,
					"Property access is disallowed because the attachment is pending upload");
		}
	}

	@Override
	public String getCharset()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return getProperties().getString(zotero.api.constants.ZoteroKeys.Attachment.CHARSET);
	}

	@Override
	public void setCharset(String charset)
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		getProperties().putValue(zotero.api.constants.ZoteroKeys.Attachment.CHARSET, charset);
	}

	@Override
	public String getContentType()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return getProperties().getString(zotero.api.constants.ZoteroKeys.Attachment.CONTENT_TYPE);
	}

	@Override
	public void setContentType(String type)
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		getProperties().putValue(zotero.api.constants.ZoteroKeys.Attachment.CONTENT_TYPE, type);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		return new AttachmentImpl(jsonItem, library);
	}

	@Override
	public InputStream retrieveContent()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		if (getLinkMode() != LinkMode.IMPORTED_FILE)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.INVALID_ATTACHMENT_TYPE,
					"Attachment must be of type IMPORTED_FILE to support retrieveContent()");
		}

		GetBuilder<InputStream, ?> builder = GetBuilder.createBuilder(new StreamResponseBuilder());
		builder.url(ZoteroRestPaths.ITEM_FILE).urlParam(Entity.KEY, getKey());

		return ((LibraryImpl) getLibrary()).performRequest(builder).getResponse();
	}

	@Override
	public void validate()
	{
		// If we're pending and have not set content then we throw and exception
		if (pending && is == null && getProperties().getProperty(ZoteroKeys.LINK_MODE).getValue() == LinkMode.IMPORTED_FILE)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_NO_CONTENT,
					"Attempted to save an attachment with no content");
		}
	}

	public void provideContent(InputStream is)
	{

		this.is = is;
	}

	@Override
	public void save()
	{
		// First we create/update the attachment
		super.save();

		// Then we bail if there is no need to manage the attachment
		if (!pending)
		{
			return;
		}

		// If we're pending, then we manage the content
		processContent();
	}

	private void processContent()
	{
		PostBuilder<?, ?> builder = PostBuilder.createBuilder(new JSONRestResponseBuilder<>(Map.class));

		Properties props = getProperties();

		String md5 = props.getString(ZoteroKeys.Attachment.MD5);
		Integer fileSize = props.getInteger(ZoteroKeys.Attachment.FILE_SIZE);
		String mtime = props.getString(ZoteroKeys.Attachment.MTIME);

		builder.url(ZoteroRestPaths.ITEM_FILE).url(this.getKey()).formParam("md5", md5)
				.formParam(ZoteroKeys.Attachment.FILENAME, props.getString(ZoteroKeys.Attachment.FILENAME)).formParam("filesize", Integer.toString(fileSize))
				.formParam("mtime", mtime).build().execute();
	}

	@Override
	public void changeLinkMode(LinkMode mode)
	{
		throw new UnsupportedOperationException("Changing attachment types is not currently supported");
	}
}
