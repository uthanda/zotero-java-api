package zotero.apiimpl.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.builders.PostBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestDeleteRequest;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPatchRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPostRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	ZoteroRestGetRequest.class, ZoteroRestGetRequest.Builder.class,
	ZoteroRestDeleteRequest.class, ZoteroRestDeleteRequest.Builder.class,
	ZoteroRestPatchRequest.class, ZoteroRestPatchRequest.Builder.class,
	ZoteroRestPostRequest.class, ZoteroRestPostRequest.Builder.class
})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
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

	@Test
	public void testItemType()
	{
		Params params = new Params();
	
		ItemSearchImpl search = (ItemSearchImpl) library.createItemSearch();
		search.apply(params::addParam);
	
		assertEquals(0, params.size());
	
		search.itemType(ItemType.ARTWORK);
		search.apply(params::addParam);
	
		assertEquals(1, params.size());
		assertEquals(1, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));
	
		params.clear();
	
		search.notItemType(ItemType.ATTACHMENT);
		search.apply(params::addParam);
	
		assertEquals(1, params.size());
		assertEquals(2, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));
		assertEquals("-" + ItemType.ATTACHMENT.getZoteroName(), params.get("itemType").get(1));
	
		params.clear();
	
		search.orItemTypes(Arrays.asList(new ItemType[] { ItemType.BILL, ItemType.BOOK }));
		search.apply(params::addParam);
	
		assertEquals(1, params.size());
		assertEquals(3, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));
		assertEquals("-" + ItemType.ATTACHMENT.getZoteroName(), params.get("itemType").get(1));
		assertEquals(ItemType.BILL.getZoteroName() + "||" + ItemType.BOOK.getZoteroName(), params.get("itemType").get(2));
	}
}