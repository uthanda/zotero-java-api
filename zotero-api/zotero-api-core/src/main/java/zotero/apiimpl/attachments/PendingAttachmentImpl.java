package zotero.apiimpl.attachments;

import zotero.api.Library;
import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.properties.PropertiesImpl;
import zotero.apiimpl.properties.PropertyStringImpl;

public class PendingAttachmentImpl extends AttachmentImpl
{
	public PendingAttachmentImpl(Library library, LinkMode mode)
	{
		this(library, mode, null);
	}

	public PendingAttachmentImpl(Library library, LinkMode mode, String parent)
	{
		super(mode,library);

		if (parent != null)
		{
			PropertiesImpl props = (PropertiesImpl) getProperties();
			props.addProperty(new PropertyStringImpl(ZoteroKeys.PARENT_COLLECTION, parent));
		}
	}
}
