package zotero.api.samples.categories;

import java.io.IOException;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.samples.Configuration;

public class CategoryDeletion
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("Usage: java CategoryDeletion config key");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);
		
		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		// Create a new conference paper
		Collection collection = library.fetchCollection(args[1]);

		// Print the new item's key
		System.out.println("Key   : " + collection.getKey());
		System.out.println("Name  : " + collection.getName());
		System.out.println("Items : " + collection.getNumberOfItems());
		
		// Set the properties
		collection.delete();
		
		// Print the new item's key
		System.out.println("Deleted");
	}

}
