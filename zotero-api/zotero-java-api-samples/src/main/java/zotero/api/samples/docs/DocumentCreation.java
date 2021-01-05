package zotero.api.samples.docs;

import java.io.IOException;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.samples.Configuration;

public class DocumentCreation
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("Usage: java DocumentDelete config type title");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);
		
		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		// Create a new conference paper
		Document doc = library.createDocument(ItemType.fromZoteroType(args[1]));
		
		// Set the properties
		doc.setTitle("A test document");
		doc.getProperties().putValue(ZoteroKeys.ItemKeys.TITLE, args[2]);
		doc.save();
		
		// Print the new item's key
		System.out.println(doc.getKey());
	}
}
