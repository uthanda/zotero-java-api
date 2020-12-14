package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

import zotero.api.constants.CreatorTypes;
import zotero.api.constants.ItemTypes;
import zotero.api.constants.LinkTypes;
import zotero.api.constants.RelationshipTypes;
import zotero.api.constants.ZoteroKeys;
import zotero.api.internal.rest.ZoteroGetUserAPIRequest;
import zotero.api.iterators.CollectionIterator;
import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockGetRestService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ZoteroGetUserAPIRequest.class })
public class ItemsTest
{
	private static MockGetRestService service = new MockGetRestService();
	private static Library library;
	private static Item item;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary("5787467", new ZoteroAPIKey("89ikoRRPvnGHVNBXHbiSRSXo"));
		item = library.fetchItem("B4ERDVS4");
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException
	{
		service.initialize();
	}

	@Test
	public void testZoteroItemFixedProperties() throws IOException
	{
		assertEquals("abstractNoteContent", item.getAbstractNote());
		assertEquals(DatatypeConverter.parseDateTime("2020-07-04T19:06:47Z").getTimeInMillis(), item.getAccessDate().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-07-03T10:31:52Z").getTimeInMillis(), item.getDateAdded().getTime());
		assertEquals(DatatypeConverter.parseDateTime("2020-12-10T06:28:27Z").getTimeInMillis(), item.getDateModified().getTime());
		assertEquals("tex.ids: hewelt_toward_2019, hewelt_towards_nodate", item.getExtra());
		assertEquals(ItemTypes.TYPE_JOURNAL_ARTICLE, item.getItemType());
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
		List<Creator> creators = item.getCreators();
		assertEquals(5, creators.size());
		assertEquals(CreatorTypes.TYPE_AUTHOR, creators.get(0).getProperties().getString(CreatorTypes.CREATOR_TYPE));
		assertEquals("Marcin", creators.get(0).getProperties().getString(CreatorTypes.FIRST_NAME));
		assertEquals("Hewelt", creators.get(0).getProperties().getString(CreatorTypes.LAST_NAME));

		assertEquals(CreatorTypes.TYPE_AUTHOR, creators.get(1).getProperties().getString(CreatorTypes.CREATOR_TYPE));
		assertEquals("Luise", creators.get(1).getProperties().getString(CreatorTypes.FIRST_NAME));
		assertEquals("Pufahl", creators.get(1).getProperties().getString(CreatorTypes.LAST_NAME));

		assertEquals(CreatorTypes.TYPE_AUTHOR, creators.get(2).getProperties().getString(CreatorTypes.CREATOR_TYPE));
		assertEquals("Sankalita", creators.get(2).getProperties().getString(CreatorTypes.FIRST_NAME));
		assertEquals("Mandal", creators.get(2).getProperties().getString(CreatorTypes.LAST_NAME));

		assertEquals(CreatorTypes.TYPE_AUTHOR, creators.get(3).getProperties().getString(CreatorTypes.CREATOR_TYPE));
		assertEquals("Felix", creators.get(3).getProperties().getString(CreatorTypes.FIRST_NAME));
		assertEquals("Wolff", creators.get(3).getProperties().getString(CreatorTypes.LAST_NAME));

		assertEquals(CreatorTypes.TYPE_AUTHOR, creators.get(4).getProperties().getString(CreatorTypes.CREATOR_TYPE));
		assertEquals("Mathias", creators.get(4).getProperties().getString(CreatorTypes.FIRST_NAME));
		assertEquals("Weske", creators.get(4).getProperties().getString(CreatorTypes.LAST_NAME));
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
		assertEquals("FJ3SUIFZ", collection.getKey());
		assertTrue(iterator.hasNext());
		collection = iterator.next();
		assertEquals("A6C8YX9M", collection.getKey());
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
		assertEquals(ItemTypes.TYPE_ATTACHMENT, child.getItemType());
		
		child = i.next();
		assertNotNull(child);
		assertEquals(ItemTypes.TYPE_ATTACHMENT, child.getItemType());
		
		assertFalse(i.hasNext());
	}
}
