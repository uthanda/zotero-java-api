package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import zotero.api.constants.LinkType;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockRestService;
import zotero.api.util.PassThruInputStream;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	HttpClients.class
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
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
		collectionNoSubCollections = library.fetchCollection(KEY_NO_SUBS);
		collectionSubCollections = library.fetchCollection(KEY_SUBS);
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for each subsequent run
		service.initialize();
	}

	@Test
	public void testDeserialize()
	{
		assertEquals(KEY_NO_SUBS, collectionNoSubCollections.getKey());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.SELF));
		assertEquals("https://api.zotero.org/users/12345678/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkType.SELF).getHref());
		assertEquals("application/json", collectionNoSubCollections.getLinks().get(LinkType.SELF).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.ALTERNATE));
		assertEquals("https://www.zotero.org/uthanda/collections/FJ3SUIFZ", collectionNoSubCollections.getLinks().get(LinkType.ALTERNATE).getHref());
		assertEquals("text/html", collectionNoSubCollections.getLinks().get(LinkType.ALTERNATE).getType());
		assertTrue(collectionNoSubCollections.getLinks().has(LinkType.UP));
		assertEquals("https://api.zotero.org/users/12345678/collections/9DXQSYMF", collectionNoSubCollections.getLinks().get(LinkType.UP).getHref());
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
		// A hackish way of checking that the call was made
		AtomicInteger ai = new AtomicInteger();
		
		service.setPost(post -> {

			post.setEntity(new InputStreamEntity(new PassThruInputStream(post, this::testCreate), ContentType.APPLICATION_JSON));

			ai.incrementAndGet();
			
			return MockRestService.postSuccess.apply(post);
		});
		
		// Create the collection and save
		Collection child = library.createCollection(collectionNoSubCollections);
		child.setName("New collection");
		child.save();
		
		// Check the POST call was simulated (there's a better way somehow to do this)
		assertEquals(1, ai.intValue());

		// Test the Meta data and key
		assertEquals("QP52ERNW", child.getKey());
		assertEquals(0, child.getNumberOfCollections());
		assertEquals(0, child.getNumberOfItems());
		assertEquals(3643, child.getVersion().intValue());
		
		// Test the link data
		assertTrue(child.getLinks().has(LinkType.SELF));
		assertEquals("https://api.zotero.org/users/12345678/collections/QP52ERNW", child.getLinks().get(LinkType.SELF).getHref());
		assertEquals("application/json", child.getLinks().get(LinkType.SELF).getType());

		assertTrue(child.getLinks().has(LinkType.ALTERNATE));
		assertEquals("https://www.zotero.org/uthanda/collections/QP52ERNW", child.getLinks().get(LinkType.ALTERNATE).getHref());
		assertEquals("text/html", child.getLinks().get(LinkType.ALTERNATE).getType());

		assertTrue(child.getLinks().has(LinkType.UP));
		assertEquals("https://api.zotero.org/users/12345678/collections/FJ3SUIFZ", child.getLinks().get(LinkType.UP).getHref());
		assertEquals("application/json", child.getLinks().get(LinkType.UP).getType());
	}

	private void testCreate(byte[] buffer)
	{
		String json = new String(buffer);
		ZoteroRestItem[] items = new Gson().fromJson(json, ZoteroRestItem[].class);
		
		assertEquals(1, items.length);
		
		ZoteroRestItem item = items[0];
		
		// Each of these items should NOT be set
		assertNull(item.getKey());
		assertNull(item.getMeta());
		assertNull(item.getLibrary());
		assertNull(item.getLinks());
		
		// We should have data
		assertNotNull(item.getData());
		
		ZoteroRestData data = item.getData();
		
		// We should have 2 properties: name and parentCollection
		assertEquals(2, data.size());
		assertEquals("New collection", data.get(zotero.api.constants.ZoteroKeys.CollectionKeys.NAME));		
		assertEquals(KEY_NO_SUBS, data.get(zotero.api.constants.ZoteroKeys.CollectionKeys.PARENT_COLLECTION));
	}
	
	@Test
	public void testUpdateCollection()
	{
		AtomicInteger ai = new AtomicInteger();
		
		service.setPatch(patch -> {

			patch.setEntity(new InputStreamEntity(new PassThruInputStream(patch, this::testUpdate), ContentType.APPLICATION_JSON));

			ai.incrementAndGet();
			
			return MockRestService.patchSuccess.apply(patch);
		});

		Collection parent = library.fetchCollection(KEY_SUBS);
		parent.setName("Changed name");
		parent.save();
		
		assertEquals(1, ai.intValue());
		assertEquals(3649, parent.getVersion().intValue());
	}
	
	private void testUpdate(byte[] content)
	{
		ZoteroRestItem item = new Gson().fromJson(new String(content), ZoteroRestItem.class);
		assertEquals(null, item.getKey());
		assertNotNull(item.getData());
		
		ZoteroRestData data = item.getData();
		assertEquals(1, data.size());
		assertEquals("Changed name", data.get(zotero.api.constants.ZoteroKeys.CollectionKeys.NAME));		
	}
	
	@Test
	public void testDeleteCollection()
	{
		AtomicInteger ai = new AtomicInteger();
		
		service.setDelete(delete -> {

			ai.incrementAndGet();
			
			// Check the request info.  Should be the right URL and have the If-Unmodified-Since-Version header set
			assertEquals("/users/12345678/collections/" + KEY_SUBS, delete.getURI().getPath());
			assertNotNull(delete.getFirstHeader(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION));
			assertEquals("44", delete.getFirstHeader(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION).getValue());
			
			return MockRestService.deleteSuccess.apply(delete);
		});

		Collection parent = library.fetchCollection(KEY_SUBS);
		parent.delete();
		
		assertEquals(1, ai.intValue());
	}
}
