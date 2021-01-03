package zotero.api.samples.attachments;

import java.io.FileInputStream;
import java.io.IOException;

import zotero.api.Attachment;
import zotero.api.Library;
import zotero.api.constants.LinkMode;
import zotero.api.samples.Configuration;

public class AttachmentCreate
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("Usage: java AttachmentCreate config title file");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);
		
		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		Attachment attachment = library.createAttachment(LinkMode.IMPORTED_FILE);
		
		attachment.setTitle(args[1]);
		attachment.setContentType("application/octet-stream");
		attachment.provideContent(new FileInputStream(args[2]));
		
		attachment.save();
	}
}
