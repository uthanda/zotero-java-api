package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import zotero.api.constants.ZoteroKeys;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockRestService;
import zotero.api.util.PassThruInputStream;
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
		assertEquals(2906, item.getVersion());
	}

	@Test
	public void testItemSpecificProperties()
	{
		// Check the arbitrary properties
		assertEquals("Software and Systems Modeling", item.getProperties().getString(ZoteroKeys.PUBLICATION_TITLE));
		assertEquals("vol1", item.getProperties().getString(ZoteroKeys.VOLUME));
		assertEquals("issue1", item.getProperties().getString(ZoteroKeys.ISSUE));
		assertEquals("1-2", item.getProperties().getString(ZoteroKeys.PAGES));
		assertEquals("2019-11-7", item.getProperties().getString(ZoteroKeys.DATE));
		assertEquals("s1", item.getProperties().getString(ZoteroKeys.SERIES));
		assertEquals("SeriesTitle", item.getProperties().getString(ZoteroKeys.SERIES_TITLE));
		assertEquals("SeriesText", item.getProperties().getString(ZoteroKeys.SERIES_TEXT));
		assertEquals("Softw Syst Model", item.getProperties().getString(ZoteroKeys.JOURNAL_ABBREVIATION));
		assertEquals("en", item.getProperties().getString(ZoteroKeys.LANGUAGE));
		assertEquals("10.1007/s10270-019-00766-5", item.getProperties().getString(ZoteroKeys.DOI));
		assertEquals("1619-1366, 1619-1374", item.getProperties().getString(ZoteroKeys.ISSN));
		assertEquals("anArchive", item.getProperties().getString(ZoteroKeys.ARCHIVE));
		assertEquals("theArchive", item.getProperties().getString(ZoteroKeys.ARCHIVE_LOCATION));
		assertEquals("DOI.org (Crossref)", item.getProperties().getString(ZoteroKeys.LIBRARY_CATALOG));
		assertEquals("42", item.getProperties().getString(ZoteroKeys.CALL_NUMBER));
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
		service.setPost(post -> {

			post.setEntity(new InputStreamEntity(new PassThruInputStream(post, this::testCreate), ContentType.APPLICATION_JSON));

			return MockRestService.postSuccess.apply(post);
		});

		Document update = (Document) library.createDocument(ItemType.CASE);

		update.getCreators().add(CreatorType.CARTOGRAPHER, "John", "Dewey");
		update.save();
	}

	private void testCreate(byte[] content)
	{
		ZoteroRestItem item = new Gson().fromJson(new String(content), ZoteroRestItem.class);

		assertNull(item.getKey());

		ZoteroRestData data = item.getData();
		assertTrue(data.get(ZoteroKeys.CREATORS) instanceof List);

		@SuppressWarnings("unchecked")
		List<Map<String, String>> creators = (List<Map<String, String>>) data.get(ZoteroKeys.CREATORS);
		assertEquals(1, creators.size());
		Map<String, String> creator = creators.get(0);
		assertEquals(CreatorType.CARTOGRAPHER.getZoteroName(), creator.get(ZoteroKeys.CREATOR_TYPE));
		assertEquals("John", creator.get(ZoteroKeys.FIRST_NAME));
		assertEquals("Dewey", creator.get(ZoteroKeys.LAST_NAME));
	}

	@Test
	public void testUpdate()
	{
		service.setPatch(patch -> {

			patch.setEntity(new InputStreamEntity(new PassThruInputStream(patch, this::testUpdate), ContentType.APPLICATION_JSON));

			return MockRestService.patchSuccess.apply(patch);
		});

		Document update = (Document) library.fetchItem(TEST_ITEM_B4ERDVS4);

		update.getCreators().add(CreatorType.CARTOGRAPHER, "John", "Dewey");
		update.setTitle("Changed title");

		update.save();
	}

	private void testUpdate(byte[] content)
	{
		ZoteroRestItem item = new Gson().fromJson(new String(content), ZoteroRestItem.class);
		assertEquals(TEST_ITEM_B4ERDVS4, item.getKey());

		ZoteroRestData data = item.getData();

		assertEquals(2, data.keySet().size());

		assertTrue(data.containsKey(ZoteroKeys.CREATORS));
		assertTrue(data.containsKey(ZoteroKeys.TITLE));

		assertEquals("Changed title", data.get(ZoteroKeys.TITLE));

		assertTrue(data.get(ZoteroKeys.CREATORS) instanceof List);

		@SuppressWarnings("unchecked")
		List<Map<String, String>> creators = (List<Map<String, String>>) data.get(ZoteroKeys.CREATORS);
		assertEquals(6, creators.size());
		Map<String, String> creator = creators.get(5);
		assertEquals(CreatorType.CARTOGRAPHER.getZoteroName(), creator.get(ZoteroKeys.CREATOR_TYPE));
		assertEquals("John", creator.get(ZoteroKeys.FIRST_NAME));
		assertEquals("Dewey", creator.get(ZoteroKeys.LAST_NAME));
	}

	@Test
	public void testDelete()
	{
		service.setDelete(delete ->{
			assertEquals("/users/apiId/items/B4ERDVS4", delete.getURI().getPath());
			return MockRestService.deleteSuccess.apply(delete);
		});
		
		Item delete = library.fetchItem(TEST_ITEM_B4ERDVS4);
		delete.delete();
	}

	@Test(expected = ZoteroRuntimeException.class)
	public void testPostDeleteException()
	{
		Item delete = library.fetchItem(TEST_ITEM_B4ERDVS4);
		delete.delete();

		delete.getKey();
	}
}
