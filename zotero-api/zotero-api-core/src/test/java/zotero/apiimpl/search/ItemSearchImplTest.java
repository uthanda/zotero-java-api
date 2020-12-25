package zotero.apiimpl.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.ItemType;
import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.builders.PostBuilder;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.impl.ZoteroRestPostRequest;
import zotero.api.iterators.ItemIterator;
import zotero.api.search.QuickSearchMode;
import zotero.api.util.MockRestService;
import zotero.api.util.Params;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames="zotero.api.internal.rest.impl.*")
public class ItemSearchImplTest
{
	private static MockRestService service = new MockRestService();
	private static Library library;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID, new ZoteroAPIKey(MockRestService.API_KEY));
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException
	{
		// Initialize the service for the thread as well ....
		service.initialize();

		GetBuilder<?> gb = ZoteroRestGetRequest.Builder.createBuilder(Object.class);

		assertTrue(gb instanceof zotero.api.util.MockGetRequest.MockRequestBuilder);

		PostBuilder pb = ZoteroRestPostRequest.Builder.createBuilder();

		assertTrue(pb instanceof zotero.api.util.MockPostRequest.MockRequestBuilder);
	}
	
	@Test
	public void testIncludeTrashed()
	{
		Params params = new Params(); 
		
		ItemSearchImpl search = new ItemSearchImpl(library);
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
		
		ItemSearchImpl search = new ItemSearchImpl(library);
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
		ItemSearchImpl search = new ItemSearchImpl(library);
		
		ItemIterator iterator = search.includeTrashed(false).itemType(ItemType.ARTWORK).itemType(ItemType.BOOK).search();
		
		assertEquals(1, iterator.getTotalCount());
		
		Item item = iterator.next();
		
		assertEquals("KZT65H5M", item.getKey());
	}
}
