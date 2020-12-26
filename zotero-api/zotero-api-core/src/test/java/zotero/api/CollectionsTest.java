package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.constants.LinkType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockPatchRequest;
import zotero.api.util.MockPostRequest;
import zotero.api.util.MockRestService;
import zotero.apiimpl.rest.ZoteroRestPaths;
import zotero.apiimpl.rest.impl.ZoteroRestDeleteRequest;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPatchRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPostRequest;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	ZoteroRestGetRequest.class, ZoteroRestGetRequest.Builder.class,
	ZoteroRestDeleteRequest.class, ZoteroRestDeleteRequest.Builder.class,
	ZoteroRestPatchRequest.class, ZoteroRestPatchRequest.Builder.class,
	ZoteroRestPostRequest.class, ZoteroRestPostRequest.Builder.class
})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*"})
public class CollectionsTest
{
	private static final String KEY_SUBS = "Y82V25U2";
	private static final String KEY_NO_SUBS = "FJ3SUIFZ";
	private static MockRestService service = new MockRestService();
	private static Library library;
	private static Collection collectionNoSubCollections;
	private static Collection collectionSubCollections;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID, new ZoteroAPIKey(MockRestService.API_KEY));
		collectionNoSubCollections = library.fetchCollection(KEY_NO_SUBS);
		collectionSubCollections = library.fetchCollection(KEY_SUBS);
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException
	{
		// Initialize the mock service for each subsequent run
		service.initialize();
	}

	@Test
	public void testDeserialize()
	{
		assertEquals(KEY_NO_SUBS, collectionNoSubCollections.getKey());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.SELF));
		assertEquals("https://api.zotero.org/users/5787467/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkType.SELF).getHref());
		assertEquals("application/json", collectionNoSubCollections.getLinks().get(LinkType.SELF).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.ALTERNATE));
		assertEquals("https://www.zotero.org/uthanda/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkType.ALTERNATE).getHref());
		assertEquals("text/html", collectionNoSubCollections.getLinks().get(LinkType.ALTERNATE).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.UP));
		assertEquals("https://api.zotero.org/users/5787467/collections/9DXQSYMF", collectionNoSubCollections.getLinks().get(LinkType.UP).getHref());
		assertEquals("application/json", collectionNoSubCollections.getLinks().get(LinkType.UP).getType());
		assertEquals(0, collectionNoSubCollections.getNumberOfCollections());
		assertEquals(3, collectionNoSubCollections.getNumberOfItems());
		assertEquals("Useful", collectionNoSubCollections.getName());
	}

	@Test
	public void testFetchParent()
	{
		Collection parent = collectionNoSubCollections.fetchParentCollection();

		assertEquals("9DXQSYMF", parent.getKey());
	}

	@Test
	public void testFetchSubCollections()
	{
		CollectionIterator iterator = collectionNoSubCollections.fetchSubCollections();

		assertEquals(0, iterator.getTotalCount());

		iterator = collectionSubCollections.fetchSubCollections();

		assertEquals(4, iterator.getTotalCount());

		assertTrue(iterator.hasNext());
		Collection collection = iterator.next();
		assertEquals("NYJCQ4NZ", collection.getKey());

		assertTrue(iterator.hasNext());
		collection = iterator.next();
		assertEquals("6LB4NBH4", collection.getKey());

		assertTrue(iterator.hasNext());
		collection = iterator.next();
		assertEquals("M2F2X5CZ", collection.getKey());

		assertTrue(iterator.hasNext());
		collection = iterator.next();
		assertEquals("NTN3S2WV", collection.getKey());
	}

	@Test
	public void testFetchItems()
	{
		ItemIterator iterator = collectionNoSubCollections.fetchItems();

		assertEquals(7, iterator.getTotalCount());

		assertTrue(iterator.hasNext());
		Item item = iterator.next();
		assertEquals("B4ERDVS4", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("KZT65H5M", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("BH8JHCNI", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("RSWMDDEA", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("SY4RPB76", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("526R6IWN", item.getKey());

		assertTrue(iterator.hasNext());
		item = iterator.next();
		assertEquals("QFV7K969", item.getKey());

		assertFalse(iterator.hasNext());

		try
		{
			iterator.next();
			fail("Reached EOR and did not get an exception");
		}
		catch (NoSuchElementException ex)
		{

		}
	}
	
	@Test
	public void testCreateCollection()
	{
		service.setPostCallbackFunction(req ->{
			testPost(req);
			return Boolean.TRUE;
		});
		
		Collection child = library.createCollection(collectionNoSubCollections);
		child.setName("New collection");
		child.save();
	}

	private void testPost(MockPostRequest req)
	{
		assertEquals(ZoteroRestPaths.COLLECTIONS, req.getUrl());
		
		assertNotNull(req.getContent());
		
		ZoteroRestItem item = (ZoteroRestItem) req.getContent();
		assertEquals(null, item.getKey());
		assertNotNull(item.getData());
		
		ZoteroRestData data = item.getData();
		// We should have 2 properties: name and parentCollection
		assertEquals(2, data.size());
		assertEquals("New collection", data.get(ZoteroKeys.NAME));		
		assertEquals(KEY_NO_SUBS, data.get(ZoteroKeys.PARENT_COLLECTION));		
	}
	
	@Test
	public void testUpdateCollection()
	{
		service.setPatchCallbackFunction(req ->{
			testPatch(req);
			return Boolean.TRUE;
		});
		
		Collection parent = library.fetchCollection(KEY_SUBS);
		parent.setName("Changed name");
		parent.save();
	}
	
	private void testPatch(MockPatchRequest req)
	{
		assertEquals(ZoteroRestPaths.COLLECTION, req.getUrl());
		
		assertNotNull(req.getContent());
		
		ZoteroRestItem item = (ZoteroRestItem) req.getContent();
		assertEquals(null, item.getKey());
		assertNotNull(item.getData());
		
		ZoteroRestData data = item.getData();
		assertEquals(1, data.size());
		assertEquals("Changed name", data.get(ZoteroKeys.NAME));		
	}
}
