package zotero.api.samples.docs;

import java.io.IOException;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.samples.Configuration;

public class DocumentRetrieval
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("Usage: java DocumentRetrieval config key");
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
	}

}
