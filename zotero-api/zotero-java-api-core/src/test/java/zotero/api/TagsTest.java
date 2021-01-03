package zotero.api;

import static org.junit.Assert.*;

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

import zotero.api.constants.LinkType;
import zotero.api.constants.TagType;
import zotero.api.iterators.TagIterator;
import zotero.api.util.MockRestService;
import zotero.apiimpl.TagImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class TagsTest
{
	// Static setups
	private static MockRestService service = new MockRestService();
	private static Library library;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		service.setGet(MockRestService.fakeGet);

		library = Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		service.initialize();
	}

	@Test
	public void testFetchAll()
	{
		TagIterator i = library.fetchTagsAll();
		
		assertTrue(i.hasNext());
		
		Tag tag = i.next();
		
		assertEquals("Connectors", tag.getTag());
		assertEquals(1, tag.getNumberItems());
		
		assertTrue(i.hasNext());
		
		tag = i.next();
		
		assertEquals("Research and development", tag.getTag());
		assertEquals(1, tag.getNumberItems());
		
		assertFalse(i.hasNext());
	}

	@Test
	public void testFetchSpecific()
	{
		TagIterator i = library.fetchTag("Connectors");
		
		assertTrue(i.hasNext());
		
		Tag tag = i.next();
		
		assertEquals("Connectors", tag.getTag());
		assertEquals(1, tag.getNumberItems());
		
		assertFalse(i.hasNext());
	}
	
	@Test
	public void testRefresh()
	{
		Document doc = (Document) library.fetchItem("B4ERDVS4");
		
		Tag tag = doc.getTags().get(0);
		
		assertEquals("followrefs", tag.getTag());
		
		Links links = tag.getLinks();
		
		assertNotNull(links);
		assertEquals("https://api.zotero.org/users/12345678/tags/followrefs", links.get(LinkType.SELF).getHref());
	}
	
	@Test
	public void testCompare()
	{
		TagImpl one = new TagImpl("Foo", TagType.AUTOMATIC, 0, null, null);
		TagImpl two = new TagImpl("Foo", TagType.USER, 0, null, null);
		TagImpl three = new TagImpl("Foo", TagType.USER, 0, null, null);
		
		assertNotEquals(one, two);
		assertNotEquals(one, three);
		assertEquals(two, three);
	}

}
