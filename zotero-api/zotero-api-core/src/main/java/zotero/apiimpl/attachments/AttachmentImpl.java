package zotero.apiimpl.attachments;

import zotero.api.Library;
import zotero.api.attachments.Attachment;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.ItemImpl;
import zotero.apiimpl.rest.model.ZoteroRestItem;

public class AttachmentImpl extends ItemImpl implements Attachment
{
	public AttachmentImpl(ZoteroRestItem jsonItem, Library library)
	{
		super(jsonItem, library);
	}

	@Override
	public LinkMode getType()
	{
		return (LinkMode) getProperties().getProperty(ZoteroKeys.LINK_MODE).getValue();
	}

	@Override
	public String getCharset()
	{
		return getProperties().getString(ZoteroKeys.CHARSET);
	}

	@Override
	public void setCharset(String charset)
	{
		getProperties().putValue(ZoteroKeys.CHARSET, charset);
	}

	@Override
	public String getContentType()
	{
		return getProperties().getString(ZoteroKeys.CONTENT_TYPE);
	}

	@Override
	public void setContentType(String type)
	{
		getProperties().putValue(ZoteroKeys.CONTENT_TYPE, type);
	}

	public static ItemImpl fromRest(ZoteroRestItem jsonItem, Library library)
	{
		return new AttachmentImpl(jsonItem, library);
	}
}
