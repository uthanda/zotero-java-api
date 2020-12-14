package zotero;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.ZoteroAPIKey;

public class ZoteroResolver
{
	private static final String API_KEY = "89ikoRRPvnGHVNBXHbiSRSXo";
	private static final String USER_ID = "5787467";

	public static void main(String[] args)
	{
		Library library = Library.createLibrary(USER_ID, new ZoteroAPIKey(API_KEY));
		
		Item item = library.fetchItem("B4ERDVS4");
		
		System.out.println(item.getTitle());
	}
}
