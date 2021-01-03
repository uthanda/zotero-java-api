package zotero.api.samples.docs;

import java.io.IOException;
import java.util.stream.Collectors;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.Tag;
import zotero.api.samples.Configuration;

public class DocumentAddTag
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println("Usage: java DocumentDelete config key tag");
			System.exit(-1);
		}

		// Load our configuration
		Configuration config = Configuration.load(args[0]);

		// Get our library
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());

		// Fetch the item
		Document doc = (Document) library.fetchItem(args[1]);

		// Print the new item's key
		System.out.println("Current");
		System.out.println("Key      : " + doc.getKey());
		System.out.println("Title    : " + doc.getTitle());
		System.out.println("Tags     : " + doc.getTags().stream().map(Tag::getTag).collect(Collectors.joining(",")));

		doc.getTags().add(library.createTag(args[2]));
		
		doc.save();

		// Print the new item's key
		System.out.println();
		System.out.println("Updated:");
		System.out.println("Key      : " + doc.getKey());
		System.out.println("Title    : " + doc.getTitle());
		System.out.println("Tags     : " + doc.getTags().stream().map(Tag::getTag).collect(Collectors.joining(",")));
	}
}
