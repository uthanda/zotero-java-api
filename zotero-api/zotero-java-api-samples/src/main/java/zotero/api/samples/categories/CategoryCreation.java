package zotero.api.samples.categories;

import java.io.IOException;

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.samples.Configuration;

public class CategoryCreation
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println("Usage: java CategoryCreation config name");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);
		
		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		// Create a new conference paper
		Collection collection = library.createCollection(null);
		
		// Set the properties
		collection.setName(args[1]);
		collection.save();
		
		// Print the new item's key
		System.out.println(collection.getKey());
	}

}
