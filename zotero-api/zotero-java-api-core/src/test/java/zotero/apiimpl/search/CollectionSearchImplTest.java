package zotero.apiimpl.search;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.ItemType;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.ItemSearch;
import zotero.api.search.QuickSearchMode;
import zotero.api.util.MockRestService;
import zotero.api.util.Params;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class CollectionSearchImplTest
{
	private static MockRestService service = new MockRestService();
	private static Library library;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the service for the thread as well ....
		service.initialize();
	}
	
	@Test
	public void testIncludeTrashed()
	{
		Params params = new Params(); 
		
		ItemSearchImpl search = (ItemSearchImpl) library.createItemSearch();
		search.apply(params::addParam);
		
		assertEquals(0, params.size());
		
		search.includeTrashed(true);
		search.apply(params::addParam);
		
		assertEquals(1, params.size());
		assertEquals("1", params.get("includeTrashed").get(0));
		
		params.clear();
		
		search.includeTrashed(false);
		search.apply(params::addParam);
		
		assertEquals(1, params.size());
		assertEquals("0", params.get("includeTrashed").get(0));
	}
	
	@Test
	public void testQMode()
	{
		Params params = new Params(); 
		
		ItemSearchImpl search = (ItemSearchImpl) library.createItemSearch();
		search.apply(params::addParam);
		
		assertEquals(0, params.size());
		
		search.quickSearchMode(QuickSearchMode.EVERYTHING);
		search.apply(params::addParam);
		
		assertEquals(1, params.size());
		assertEquals(QuickSearchMode.EVERYTHING.getZoteroName(), params.get("qmode").get(0));
		
		params.clear();
		
		search.quickSearchMode(QuickSearchMode.TITLE_CREATOR_YEAR);
		search.apply(params::addParam);
		
		assertEquals(1, params.size());
		assertEquals(QuickSearchMode.TITLE_CREATOR_YEAR.getZoteroName(), params.get("qmode").get(0));
	}
	
	@Test
	public void testExecute()
	{
		ItemSearch search = library.createItemSearch();
		
		ItemIterator iterator = search.includeTrashed(false).itemType(ItemType.ARTWORK).itemType(ItemType.BOOK).search();
		
		assertEquals(1, iterator.getTotalCount());
		
		Item item = iterator.next();
		
		assertEquals("KZT65H5M", item.getKey());
	}
}
