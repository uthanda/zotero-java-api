package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.constants.LinkTypes;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.impl.ZoteroRestPutRequest;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockRestService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ZoteroRestGetRequest.class, ZoteroRestPutRequest.Builder.class })
public class CollectionsTest
{
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
		collectionNoSubCollections = library.fetchCollection("FJ3SUIFZ");
		collectionSubCollections = library.fetchCollection("Y82V25U2");
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
		assertEquals("FJ3SUIFZ", collectionNoSubCollections.getKey());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkTypes.TYPE_SELF));
		assertEquals("https://api.zotero.org/users/5787467/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_SELF).getUri());
		assertEquals("application/json", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_SELF).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkTypes.TYPE_ALTERNATE));
		assertEquals("https://www.zotero.org/uthanda/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_ALTERNATE).getUri());
		assertEquals("text/html", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_ALTERNATE).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkTypes.TYPE_UP));
		assertEquals("https://api.zotero.org/users/5787467/collections/9DXQSYMF", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_UP).getUri());
		assertEquals("application/json", collectionNoSubCollections.getLinks().get(LinkTypes.TYPE_UP).getType());
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
}
