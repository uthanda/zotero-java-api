package zotero.apiimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import zotero.api.Attachment;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroExceptionCodes;
import zotero.api.constants.ZoteroExceptionType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.Properties;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.ZoteroRest.URLParameter;
import zotero.apiimpl.rest.model.ZoteroRestItem;
import zotero.apiimpl.rest.request.builders.ContentPostBuilder;
import zotero.apiimpl.rest.request.builders.GetBuilder;
import zotero.apiimpl.rest.request.builders.PostBuilder;
import zotero.apiimpl.rest.response.JSONRestResponseBuilder;
import zotero.apiimpl.rest.response.RestResponse;
import zotero.apiimpl.rest.response.StreamResponseBuilder;
import zotero.apiimpl.rest.response.SuccessResponseBuilder;

public class AttachmentImpl extends ItemImpl implements Attachment
{
	private boolean pending = false;
	private InputStream is;
	private Long fileSize;

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
		((PropertiesImpl) getProperties()).addProperty(new PropertyStringImpl(ZoteroKeys.AttachmentKeys.PARENT_ITEM, parentKey, true));
	}

	@Override
	public LinkMode getLinkMode()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return (LinkMode) getProperties().getProperty(ZoteroKeys.AttachmentKeys.LINK_MODE).getValue();
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

		return getProperties().getString(ZoteroKeys.AttachmentKeys.CHARSET);
	}

	@Override
	public void setCharset(String charset)
	{
		super.checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.AttachmentKeys.CHARSET, charset);
	}

	@Override
	public String getContentType()
	{
		super.checkDeletionStatus();
		checkPendingStatus();

		return getProperties().getString(ZoteroKeys.AttachmentKeys.CONTENT_TYPE);
	}

	@Override
	public void setContentType(String type)
	{
		super.checkDeletionStatus();

		getProperties().putValue(ZoteroKeys.AttachmentKeys.CONTENT_TYPE, type);
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
		builder.url(ZoteroRest.Items.FILE).urlParam(URLParameter.ITEM_KEY, getKey());

		return ((LibraryImpl) getLibrary()).performRequest(builder).getResponse();
	}

	@Override
	public void validate()
	{
		Properties props = getProperties();

		switch ((LinkMode) props.getProperty(ZoteroKeys.AttachmentKeys.LINK_MODE).getValue())
		{
			case IMPORTED_FILE:
			{
				String md5 = props.getString(ZoteroKeys.AttachmentKeys.MD5);
				String mtime = props.getString(ZoteroKeys.AttachmentKeys.MTIME);
				String filename = props.getString(ZoteroKeys.AttachmentKeys.FILENAME);
				String contentType = props.getString(ZoteroKeys.AttachmentKeys.CONTENT_TYPE);

				if (contentType == null)
				{
					throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.ATTACHMENT_MISSING_PARAM, "No contentType provided for attachment");
				}

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
				if (props.getString(ZoteroKeys.EntityKeys.URL) == null)
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
	public void provideContent(InputStream is, Long fileSize, String md5)
	{
		this.is = is;
		this.fileSize = fileSize;
		getProperties().putValue(ZoteroKeys.AttachmentKeys.MD5, md5);
	}

	@Override
	public void save()
	{
		boolean isNew = this.getKey() == null;

		// First we create/update the attachment
		super.save();

		this.pending = false;

		if (this.getLinkMode() != LinkMode.IMPORTED_FILE)
		{
			return;
		}

		// If we're pending, then we manage the content
		processContent(isNew);
	}

	private void processContent(boolean isNew)
	{
		Properties props = getProperties();

		String md5 = props.getString(ZoteroKeys.AttachmentKeys.MD5);
		String mtime = props.getString(ZoteroKeys.AttachmentKeys.MTIME);
		String filename = props.getString(ZoteroKeys.AttachmentKeys.FILENAME);
		String mimeType = props.getString(ZoteroKeys.AttachmentKeys.CONTENT_TYPE);

		validateImportedFileProperties(md5, mtime, filename);

		Map<String, Object> body = getAuthorization(md5, mtime, filename, isNew);

		postContent(filename, mimeType, body);
	}

	private void registerUpload(String uploadKey)
	{
		PostBuilder<Void, ?> builder = PostBuilder.createBuilder(new SuccessResponseBuilder());

		//@formatter:off
		builder
			.url(ZoteroRest.Items.FILE)
			.formParam(zotero.api.constants.ZoteroKeys.AttachmentKeys.UPLOAD, uploadKey)
			.urlParam(URLParameter.ITEM_KEY, getKey())
			.header(ZoteroRest.Headers.IF_MATCH, getProperties().getString(ZoteroKeys.AttachmentKeys.MD5));
		//@formatter:on

		LibraryImpl library = (LibraryImpl) getLibrary();

		library.performRequest(builder);
	}

	@SuppressWarnings("unchecked")
	protected void postContent(String filename, String mimeType, Map<String, Object> body)
	{
		String uploadKey = (String) body.get("uploadKey");
		String url = (String) body.get("url");

		Map<String, String> params = (Map<String, String>) body.get("params");

		MultipartEntityBuilder mpb = MultipartEntityBuilder.create();

		if (body.containsKey("exists") && ((Double) body.get("exists")).intValue() == 1)
		{
			// Nothing to do if the file already exists so BAIL!
			return;
		}

		// Add the provided params
		params.forEach((key, value) -> {
			StringBody sb = new StringBody(value, ContentType.MULTIPART_FORM_DATA);
			mpb.addPart(key, sb);
		});

		InputStreamBody contentBody = new InputStreamBody(is, ContentType.create(mimeType), filename);

		mpb.addPart("file", contentBody);

		// This will throw an exception if the content POST fails
		ContentPostBuilder builder = ContentPostBuilder.createBuilder().url(url).entity(mpb.build());

		LibraryImpl library = (LibraryImpl) getLibrary();

		library.performRequest(builder);

		registerUpload(uploadKey);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getAuthorization(String md5, String mtime, String filename, boolean isNew)
	{
		PostBuilder<?, ?> builder = PostBuilder.createBuilder(new JSONRestResponseBuilder<>(Map.class));

		//@formatter:off
		builder.url(ZoteroRest.Items.FILE)
			.urlParam(URLParameter.ITEM_KEY, this.getKey())
			.formParam(ZoteroKeys.AttachmentKeys.MD5, md5)
			.formParam(ZoteroKeys.AttachmentKeys.FILENAME, filename)
			.formParam(ZoteroKeys.AttachmentKeys.FILE_SIZE, fileSize.toString())
			.formParam(ZoteroKeys.AttachmentKeys.MTIME, mtime)
			.formParam("params", "1");
		//@formatter:on

		if (isNew)
		{
			// builder.header(ZoteroRest.Headers.IF_NONE_MATCH, "*");
			builder.header(ZoteroRest.Headers.IF_MATCH, md5);
		}
		else
		{
			builder.header(ZoteroRest.Headers.IF_MATCH, md5);
		}

		LibraryImpl library = (LibraryImpl) getLibrary();

		RestResponse<?> response = library.performRequest(builder);

		return (Map<String, Object>) response.getResponse();
	}

	protected void validateImportedFileProperties(String md5, String mtime, String filename) throws ZoteroRuntimeException
	{
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
	}

	@Override
	public void changeLinkMode(LinkMode mode)
	{
		throw new UnsupportedOperationException("Changing attachment types is not currently supported");
	}

	@Override
	public void provideContent(File file)
	{
		String mime = URLConnection.guessContentTypeFromName(file.getName());

		if (mime == null)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.MIME_NOT_RESOLVED, "Unable to resolve mime type for file");
		}

		String md5 = createMD5(file);

		try (FileInputStream fis = new FileInputStream(file))
		{
			this.provideContent(fis, file.length(), md5);

			BasicFileAttributes attr = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);

			getProperties().putValue(ZoteroKeys.AttachmentKeys.FILENAME, file.getName());
			getProperties().putValue(ZoteroKeys.AttachmentKeys.CONTENT_TYPE, mime);
			getProperties().putValue(ZoteroKeys.AttachmentKeys.MTIME, Long.toString(attr.lastModifiedTime().toMillis()));
		}
		catch (IOException e)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.IO, ZoteroExceptionCodes.IO.IO_ERROR, e.getLocalizedMessage(), e);
		}

	}

	public String createMD5(File file) throws ZoteroRuntimeException
	{
		try
		{
			// Generate the MD5
			try (FileInputStream fis = new FileInputStream(file))
			{
				MessageDigest md = MessageDigest.getInstance("MD5");

				byte[] buffer = new byte[10240];
				int length;

				while ((length = fis.read(buffer)) > -1)
				{
					md.update(buffer, 0, length);
				}

				return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
			}
		}
		catch (IOException e)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.DATA, ZoteroExceptionCodes.Data.MD5_ERROR, "Failed to generate MD5 for file");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new ZoteroRuntimeException(ZoteroExceptionType.UNKNOWN, ZoteroExceptionCodes.Unknown.INTERNAL_ERROR, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public final String getTitle()
	{
		checkDeletionStatus();
	
		return super.getProperties().getString(ZoteroKeys.ItemKeys.TITLE);
	}

	@Override
	public final void setTitle(String title)
	{
		checkDeletionStatus();
	
		super.getProperties().putValue(ZoteroKeys.ItemKeys.TITLE, title);
	}
}
