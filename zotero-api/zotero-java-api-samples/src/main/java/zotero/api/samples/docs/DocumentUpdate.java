package zotero.api.samples.docs;

import java.io.IOException;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.samples.Configuration;

public class DocumentUpdate
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("Usage: java DocumentDelete config key title");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);

		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		// Fetch the item
		Document doc = (Document) library.fetchItem(args[1]);

		// Print the new item's key
		System.out.println("Key      : " + doc.getKey());
		System.out.println("Title    : " + doc.getTitle());
		System.out.println("Children : " + doc.getNumberOfChilden());
		
		doc.setTitle(args[2]);
		doc.save();

		// Print the new item's key
		System.out.println("Key      : " + doc.getKey());
		System.out.println("Title    : " + doc.getTitle());
		System.out.println("Children : " + doc.getNumberOfChilden());
	}
}
