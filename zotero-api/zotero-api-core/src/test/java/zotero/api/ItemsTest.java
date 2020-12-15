package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.collections.CreatorsList;
import zotero.api.constants.CreatorType;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkTypes;
import zotero.api.constants.RelationshipTypes;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.builders.PutBuilder;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.impl.ZoteroRestPutRequest;
import zotero.api.internal.rest.impl.ZoteroRestPutRequest.Builder;
import zotero.api.internal.rest.model.ZoteroRestCreator;
import zotero.api.internal.rest.model.ZoteroRestData;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockPutRequest;
import zotero.api.util.MockRestService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ZoteroRestGetRequest.class, ZoteroRestPutRequest.class })
public class ItemsTest
{
	// Static setups
	private static MockRestService service = new MockRestService();
	private static Library library;
	private static Item item;
	
	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID, new ZoteroAPIKey(MockRestService.API_KEY));
		item = library.fetchItem("B4ERDVS4");
	}

	// Instance level stuff
	
	private MockPutRequest putRequest;

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException
	{
		service.initialize();
		
		GetBuilder<?> gb = ZoteroRestGetRequest.Builder.createBuilder(Object.class);
		
		assertTrue(gb instanceof zotero.api.util.MockGetRequest.MockRequestBuilder);
		
		PutBuilder pb = ZoteroRestPutRequest.Builder.createBuilder();
		
		assertTrue(pb instanceof zotero.api.util.MockPutRequest.MockRequestBuilder);
	}

	@Test
	public void testZoteroItemFixedProperties() throws IOException
	{
		assertEquals("abstractNoteContent", item.getAbstractNote());
		assertEquals(DatatypeConverter.parseDateTime("2020-07-04T19:06:47Z").getTimeInMillis(), item.getAccessDate().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-07-03T10:31:52Z").getTimeInMillis(), item.getDateAdded().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-12-10T06:28:27Z").getTimeInMillis(), item.getDateModified().getTime());
		assertEquals("tex.ids: hewelt_toward_2019, hewelt_towards_nodate", item.getExtra());
		assertEquals(ItemType.JOURNAL_ARTICLE.getZoteroType(), item.getItemType());
		assertEquals("B4ERDVS4", item.getKey());
		assertEquals(2, item.getNumberOfChilden());
		assertEquals("rightsContent", item.getRights());
		assertEquals("TAMFCM", item.getShortTitle());
		assertEquals("Toward a methodology for case modeling", item.getTitle());
		assertEquals("http://link.springer.com/10.1007/s10270-019-00766-5", item.getURL());
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
		assertTrue(item.getLinks().has(LinkTypes.TYPE_SELF));
		assertTrue(item.getLinks().has(LinkTypes.TYPE_ALTERNATE));
	}

	@Test
	public void testCreators()
	{
		CreatorsList creators = item.getCreators();
		
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
		List<Tag> tags = item.getTags();
		assertEquals(1, tags.size());
		assertEquals("followrefs", tags.get(0).getProperties().getString("tag"));
	}

	@Test
	public void testCollections()
	{
		CollectionIterator iterator = item.getCollections();
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
		List<String> relationUris = relationships.getRelationships(RelationshipTypes.TYPE_DC_REPLACES);
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
		assertEquals(ItemType.ATTACHMENT.getZoteroType(), child.getItemType());
		
		child = i.next();
		assertNotNull(child);
		assertEquals(ItemType.ATTACHMENT.getZoteroType(), child.getItemType());
		
		assertFalse(i.hasNext());
	}
	
	@Test
	public void testCreate()
	{
		service.setPutCallbackFunction(req -> {
			testUpdate(req);
			return Boolean.TRUE;
		});
		
		Item update = library.createItem(ItemType.CASE);
		
		System.out.println(update.getProperties().getPropertyNames());
		
		update.getCreators().add(CreatorType.CARTOGRAPHER, "John", "Dewey");
		update.save();
	}

	private void testUpdate(MockPutRequest request)
	{
		Object content = request.getContent();
		assertTrue(content instanceof ZoteroRestItem);
		
		ZoteroRestItem item = (ZoteroRestItem) content;
		assertNull(item.getKey());
		
		ZoteroRestData data = item.getData();
		assertTrue(data.get(ZoteroKeys.CREATORS) instanceof List);
		List<ZoteroRestCreator> creators = (List<ZoteroRestCreator>) data.get(ZoteroKeys.CREATORS);
		assertEquals(5, creators.size());
		assertEquals(CreatorType.ATTORNEY_AGENT.name(), creators.get(0).getCreatorType());
	}
}
