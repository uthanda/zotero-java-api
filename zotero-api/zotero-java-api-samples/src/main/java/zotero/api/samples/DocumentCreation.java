package zotero.api.samples;

import java.io.IOException;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.constants.ItemType;
import zotero.api.constants.ZoteroKeys;

public class DocumentCreation
{
	public static void main(String[] args) throws IOException
	{
		Configuration config = Configuration.load(args[0]);
		
		Library library = Library.createLibrary(config.getUserId(), config.getApiKey());
		
		Document doc = library.createDocument(ItemType.CONFERENCE_PAPER);
		
		doc.setTitle("A test document");
		doc.getProperties().putValue(ZoteroKeys.Document.ABSTRACT, "Abstract goes here");
		doc.getProperties().putValue(ZoteroKeys.Document.DATE, "2020/01/01");
		doc.getProperties().putValue(ZoteroKeys.Document.PROCEEDINGS_TITLE, "Testing Custom APIs 2021");
		doc.save();
		
		System.out.println(doc.getKey());
	}
}
