package zotero.apiimpl;

import zotero.api.constants.ZoteroKeys;
import static zotero.apiimpl.rest.ZoteroRest.Items.FILE;

import java.io.InputStream;
import java.util.Map;

import zotero.api.Attachment;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.rest.response.StreamResponseBuilder;

public class AttachmentImpl extends ItemImpl implements Attachment
{
	private boolean pending = false;
	private InputStream is;
	private Integer fileSize;

	public AttachmentImpl(ZoteroRestItem jsonItem, LibraryImpl library)
	{
		super(jsonItem, library);
	}

	public AttachmentImpl(LinkMode mode, LibraryImpl library)
	{
		this(mode, library, null);
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

		return (LinkMode) getProperties().getProperty(ZoteroKeys.Attachment.LINK_MODE).getValue();
	}

	private void checkPendingStatus()
	{
		if (pending)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_PENDING, "Property access is disallowed because the attachment is pending upload");
		}
	}

	@Override
	public String getCharset()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return getProperties().getString(ZoteroKeys.Attachment.CHARSET);
	}

	@Override
	public void setCharset(String charset)
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		getProperties().putValue(ZoteroKeys.Attachment.CHARSET, charset);
	}

	@Override
	public String getContentType()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return getProperties().getString(ZoteroKeys.Attachment.CONTENT_TYPE);
	}

	@Override
	public void setContentType(String type)
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		getProperties().putValue(ZoteroKeys.Attachment.CONTENT_TYPE, type);
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
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.INVALID_ATTACHMENT_TYPE, "Attachment must be of type IMPORTED_FILE to support retrieveContent()");
		}

		GetBuilder<InputStream, ?> builder = GetBuilder.createBuilder(new StreamResponseBuilder());
		builder.url(FILE).urlParam(URLParameter.ITEM_KEY, getKey());

		return ((LibraryImpl) getLibrary()).performRequest(builder).getResponse();
	}

	@Override
	public void validate()
	{
		Properties props = getProperties();

		switch ((LinkMode)props.getProperty(ZoteroKeys.Attachment.LINK_MODE).getValue())
		{
			case IMPORTED_FILE:
			{
				String md5 = props.getString(ZoteroKeys.Attachment.MD5);
				String mtime = props.getString(ZoteroKeys.Attachment.MTIME);
				String filename = props.getString(ZoteroKeys.Attachment.FILENAME);

				if (md5 == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No md5 provided for attachment");
				}

				if (fileSize == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No fileSize provided for attachment");
				}

				if (mtime == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No mtime provided for attachment");
				}

				if (filename == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No filename provided for attachment");
				}

				if (is == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_NO_CONTENT, "Attempted to save an attachment with no content");
				}

				break;
			}
			case IMPORTED_URL:
			{
				if (props.getString(ZoteroKeys.Entity.URL) == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No url provided for attachment");
				}

				break;
			}
			case LINKED_FILE:
				break;
			case LINKED_URL:
				break;
			default:
				break;

		}
	}

	@Override
	public void provideContent(InputStream is, Integer fileSize)
	{
		this.is = is;
		this.fileSize = fileSize;
	}

	@Override
	public void save()
	{
		// First we create/update the attachment
		super.save();

		this.pending = false;

		if (this.getLinkMode() != LinkMode.IMPORTED_FILE)
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
		String mtime = props.getString(ZoteroKeys.Attachment.MTIME);
		String filename = props.getString(ZoteroKeys.Attachment.FILENAME);

		if (md5 == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No MD5 sum provided for attachment");
		}

		if (fileSize == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No fileSize sum provided for attachment");
		}

		if (mtime == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No mtime sum provided for attachment");
		}

		if (filename == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No filename sum provided for attachment");
		}

		//@formatter:off
		builder.url(FILE)
			.urlParam(URLParameter.ITEM_KEY, this.getKey())
			.formParam(ZoteroKeys.Attachment.MD5, md5)
			.formParam(ZoteroKeys.Attachment.FILENAME, filename)
			.formParam(ZoteroKeys.Attachment.FILE_SIZE, Integer.toString(fileSize))
			.formParam(ZoteroKeys.Attachment.MTIME, mtime);
		//@formatter:on
		
		LibraryImpl library = (LibraryImpl)getLibrary();
		RestResponse<?> response = library.performRequest(builder);
	}
	
	@Override
	public void changeLinkMode(LinkMode mode)
	{
		throw new UnsupportedOperationException("Changing attachment types is not currently supported");
	}
}
