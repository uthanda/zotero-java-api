package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.DatatypeConverter;

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

import zotero.api.collections.Collections;
import zotero.api.collections.Creators;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.CreatorType;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkType;
import zotero.api.constants.RelationshipType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockRestService;
import zotero.api.util.PassThruInputStream;
import zotero.apiimpl.rest.ZoteroRest;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class ItemsTest
{
	private static final String TEST_ITEM_B4ERDVS4 = "B4ERDVS4";
	// Static setups
	private static MockRestService service = new MockRestService();
	private static Library library;
	private static Document item;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		service.setGet(MockRestService.fakeGet);

		library = Library.createLibrary(MockRestService.API_ID, new ZoteroAPIKey(MockRestService.API_KEY));
		item = (Document) library.fetchItem(TEST_ITEM_B4ERDVS4);
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		service.initialize();
	}

	@Test
	public void testZoteroItemFixedProperties() throws IOException
	{
		assertEquals(DatatypeConverter.parseDateTime("2020-07-04T19:06:47Z").getTimeInMillis(), item.getAccessDate().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-07-03T10:31:52Z").getTimeInMillis(), item.getDateAdded().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-12-10T06:28:27Z").getTimeInMillis(), item.getDateModified().getTime());
		assertEquals(ItemType.JOURNAL_ARTICLE, item.getItemType());
		assertEquals(TEST_ITEM_B4ERDVS4, item.getKey());
		assertEquals(2, item.getNumberOfChilden());
		assertEquals("Toward a methodology for case modeling", item.getTitle());
		assertEquals(2906, item.getVersion().intValue());
	}

	@Test
	public void testItemSpecificProperties()
	{
		// Check the arbitrary properties
		assertEquals("Software and Systems Modeling", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.PUBLICATION_TITLE));
		assertEquals("vol1", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.VOLUME));
		assertEquals("issue1", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.ISSUE));
		assertEquals("1-2", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.PAGES));
		assertEquals("2019-11-7", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.DATE));
		assertEquals("s1", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.SERIES));
		assertEquals("SeriesTitle", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.SERIES_TITLE));
		assertEquals("SeriesText", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.SERIES_TEXT));
		assertEquals("Softw Syst Model", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.JOURNAL_ABBREVIATION));
		assertEquals("en", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.LANGUAGE));
		assertEquals("10.1007/s10270-019-00766-5", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.DOI));
		assertEquals("1619-1366, 1619-1374", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.ISSN));
		assertEquals("anArchive", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.ARCHIVE));
		assertEquals("theArchive", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.ARCHIVE_LOCATION));
		assertEquals("DOI.org (Crossref)", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.LIBRARY_CATALOG));
		assertEquals("42", item.getProperties().getString(zotero.api.constants.ZoteroKeys.Document.CALL_NUMBER));
	}

	@Test
	public void testLinks()
	{
		assertTrue(item.getLinks().has(LinkType.SELF));
		assertTrue(item.getLinks().has(LinkType.ALTERNATE));
	}

	@Test
	public void testCreators()
	{
		Creators creators = item.getCreators();

		assertEquals(5, creators.size());

		Creator creator = creators.get(0);

		assertEquals(CreatorType.AUTHOR, creator.getType());
		assertEquals("Marcin", creator.getFirstName());
		assertEquals("Hewelt", creator.getLastName());

		creator = creators.get(1);

		assertEquals(CreatorType.AUTHOR, creator.getType());
		assertEquals("Luise", creator.getFirstName());
		assertEquals("Pufahl", creator.getLastName());

		creator = creators.get(2);

		assertEquals(CreatorType.AUTHOR, creator.getType());
		assertEquals("Sankalita", creator.getFirstName());
		assertEquals("Mandal", creator.getLastName());

		creator = creators.get(3);

		assertEquals(CreatorType.AUTHOR, creator.getType());
		assertEquals("Felix", creator.getFirstName());
		assertEquals("Wolff", creator.getLastName());

		creator = creators.get(4);

		assertEquals(CreatorType.AUTHOR, creator.getType());
		assertEquals("Mathias", creator.getFirstName());
		assertEquals("Weske", creator.getLastName());
	}

	@Test
	public void testTags()
	{
		Tags tags = item.getTags();
		assertEquals(1, tags.size());
		assertEquals("followrefs", tags.get(0));
	}

	@Test
	public void testCollections()
	{
		Collections collections = item.getCollections();
		CollectionIterator iterator = collections.iterator();
		assertEquals(2, iterator.getTotalCount());

		assertTrue(iterator.hasNext());
		Collection collection = iterator.next();
		assertEquals("A6C8YX9M", collection.getKey());

		assertTrue(iterator.hasNext());
		collection = iterator.next();
		assertEquals("FJ3SUIFZ", collection.getKey());

		assertFalse(iterator.hasNext());
	}

	@Test
	public void testRelationships()
	{
		Relationships relationships = item.getRelationships();
		List<String> relationUris = relationships.getRelatedKeys(RelationshipType.DC_REPLACES);
		assertEquals("http://zotero.org/users/5787467/items/GX8ZD6D9", relationUris.get(0));
		assertEquals("http://zotero.org/users/5787467/items/NYA3Z5B9", relationUris.get(1));
	}

	@Test
	public void testGetChildren()
	{
		ItemIterator i = item.fetchChildren();

		assertEquals(2, i.getTotalCount());
		assertTrue(i.hasNext());

		Item child = i.next();
		assertNotNull(child);
		assertEquals(ItemType.ATTACHMENT, child.getItemType());
		assertTrue(child instanceof Attachment);

		child = i.next();
		assertNotNull(child);
		assertEquals(ItemType.ATTACHMENT, child.getItemType());
		assertTrue(child instanceof Attachment);

		assertFalse(i.hasNext());
	}

	@Test
	public void testCreate()
	{
		AtomicInteger ai = new AtomicInteger();

		service.setPost(post -> {

			post.setEntity(new InputStreamEntity(new PassThruInputStream(post, this::testCreate), ContentType.APPLICATION_JSON));

			ai.incrementAndGet();

			return MockRestService.postSuccess.apply(post);
		});

		Document item = (Document) library.createDocument(ItemType.CASE);

		item.getCreators().add(CreatorType.AUTHOR, "John", "Dewey");
		item.save();

		assertEquals(1, ai.intValue());

		assertEquals("R2E8GCGX", item.getKey());
	}

	private void testCreate(byte[] content)
	{
		String json = new String(content);

		ZoteroRestItem[] items = new Gson().fromJson(json, ZoteroRestItem[].class);

		assertEquals(1, items.length);

		ZoteroRestItem item = items[0];

		assertNull(item.getKey());

		ZoteroRestData data = item.getData();
		assertTrue(data.get(zotero.api.constants.ZoteroKeys.Document.CREATORS) instanceof List);

		@SuppressWarnings("unchecked")
		List<Map<String, String>> creators = (List<Map<String, String>>) data.get(zotero.api.constants.ZoteroKeys.Document.CREATORS);
		assertEquals(1, creators.size());
		Map<String, String> creator = creators.get(0);
		assertEquals(CreatorType.AUTHOR.getZoteroName(), creator.get(zotero.api.constants.ZoteroKeys.Creator.CREATOR_TYPE));
		assertEquals("John", creator.get(zotero.api.constants.ZoteroKeys.Creator.FIRST_NAME));
		assertEquals("Dewey", creator.get(zotero.api.constants.ZoteroKeys.Creator.LAST_NAME));
	}

	@Test
	public void testUpdate()
	{
		AtomicInteger ai = new AtomicInteger();

		service.setPatch(patch -> {

			patch.setEntity(new InputStreamEntity(new PassThruInputStream(patch, this::testUpdate), ContentType.APPLICATION_JSON));

			ai.incrementAndGet();

			return MockRestService.patchSuccess.apply(patch);
		});

		Document update = (Document) library.fetchItem(TEST_ITEM_B4ERDVS4);

		update.getCreators().add(CreatorType.CARTOGRAPHER, "John", "Dewey");
		update.setTitle("Changed title");

		update.save();

		assertEquals(1, ai.intValue());
		assertEquals(2907, update.getVersion().intValue());
	}

	private void testUpdate(byte[] content)
	{
		ZoteroRestItem item = new Gson().fromJson(new String(content), ZoteroRestItem.class);
		assertEquals(TEST_ITEM_B4ERDVS4, item.getKey());

		ZoteroRestData data = item.getData();

		assertEquals(2, data.keySet().size());

		assertTrue(data.containsKey(zotero.api.constants.ZoteroKeys.Document.CREATORS));
		assertTrue(data.containsKey(zotero.api.constants.ZoteroKeys.Item.TITLE));

		assertEquals("Changed title", data.get(zotero.api.constants.ZoteroKeys.Item.TITLE));

		assertTrue(data.get(zotero.api.constants.ZoteroKeys.Document.CREATORS) instanceof List);

		@SuppressWarnings("unchecked")
		List<Map<String, String>> creators = (List<Map<String, String>>) data.get(zotero.api.constants.ZoteroKeys.Document.CREATORS);
		assertEquals(6, creators.size());
		Map<String, String> creator = creators.get(5);
		assertEquals(CreatorType.CARTOGRAPHER.getZoteroName(), creator.get(zotero.api.constants.ZoteroKeys.Creator.CREATOR_TYPE));
		assertEquals("John", creator.get(zotero.api.constants.ZoteroKeys.Creator.FIRST_NAME));
		assertEquals("Dewey", creator.get(zotero.api.constants.ZoteroKeys.Creator.LAST_NAME));
	}

	@Test
	public void testDelete()
	{
		AtomicInteger ai = new AtomicInteger();
		
		service.setDelete(delete -> {

			// Check the request info.  Should be the right URL and have the If-Unmodified-Since-Version header set
			assertEquals("/users/apiId/items/B4ERDVS4", delete.getURI().getPath());
			assertNotNull(delete.getFirstHeader(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION));
			assertEquals("2906", delete.getFirstHeader(ZoteroRest.Headers.IF_UNMODIFIED_SINCE_VERSION).getValue());
			
			ai.incrementAndGet();
			
			return MockRestService.deleteSuccess.apply(delete);
		});

		Item delete = library.fetchItem(TEST_ITEM_B4ERDVS4);
		delete.delete();
		
		assertEquals(1, ai.intValue());
	}

	@Test(expected = ZoteroRuntimeException.class)
	public void testPostDeleteException()
	{
		Item delete = library.fetchItem(TEST_ITEM_B4ERDVS4);
		delete.delete();

		delete.getKey();
	}
}
