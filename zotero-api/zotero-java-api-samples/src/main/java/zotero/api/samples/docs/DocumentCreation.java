package zotero.api.samples.docs;

import java.io.IOException;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.batch.item.BatchItemHandle;
import zotero.api.batch.item.BatchItemResponse;
import zotero.api.batch.item.CreateItemsBatch;
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

		// Create the batch
		CreateItemsBatch batch = library.createCreateItemsBatch();
		
		// Create our documents and batch item handles
		Document[] docs = new Document[2];
		BatchItemHandle[] handles = new BatchItemHandle[2];
		
		// Create a new document
		docs[0] = library.createDocument(ItemType.fromZoteroType(args[1]));
		docs[0].getProperties().putValue(ZoteroKeys.ItemKeys.TITLE, args[2]);
		handles[0] = batch.add(docs[0]);
		
		// Create a new document
		docs[1] = library.createDocument(ItemType.fromZoteroType(args[3]));
		docs[1].getProperties().putValue(ZoteroKeys.ItemKeys.TITLE, args[4]);
		handles[1] = batch.add(docs[1]);

		// Commit the documents
		BatchItemResponse response = batch.commit();
		
		// Print an overall status message
		if(!response.hasErrors())
		{
			System.out.println("All documents committed successfully");
		}
		else
		{
			System.out.println("There were errors");
		}

		// Print the information about each document
		printResult(handles[0]);
		printResult(handles[1]);
	}

	private static void printResult(BatchItemHandle handle)
	{
		System.out.printf("Item %d result:%n", handle.getIndex());
		
		switch(handle.getResult())
		{
			case FAILED:{
				System.out.printf("\tFailed: %s%n", handle.getMessage());
				break;
			}
			case INVALID:{
				System.out.printf("\tInvalid: %s%n", handle.getMessage());
				break;
			}				
			case SUCCESS:{
				System.out.printf("\tSuccess (key): %s%n", handle.getItem().getKey());
				break;
			}
			case UNCHANGED:{
				System.out.printf("\tUnchanged (key): %s%n", handle.getItem().getKey());
				break;
			}
			case UNEXECUTED:{
				// Internal tracking state and should never happen
				break;
			}
			default:{
				// Should only happen if a new result type is added.
				break;
			}
			
		}
	}
}
