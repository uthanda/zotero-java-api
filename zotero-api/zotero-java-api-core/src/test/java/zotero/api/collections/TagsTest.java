package zotero.api.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.Document;
import zotero.api.Library;
import zotero.api.Links;
import zotero.api.Tag;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.LinkType;
import zotero.api.constants.TagType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.TagIterator;
import zotero.api.util.MockRestService;
import zotero.apiimpl.TagImpl;
import zotero.apiimpl.collections.TagsImpl;

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
	public void testIterator()
	{
		Document doc = (Document) library.fetchItem("B4ERDVS4");
		
		Tag tag = doc.getTags().iterator().next();
		
		assertEquals("followrefs", tag.getTag());
		
		Links links = tag.getLinks();
		
		assertNotNull(links);
		assertEquals("https://api.zotero.org/users/12345678/tags/followrefs", links.get(LinkType.SELF).getHref());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToRest()
	{
		Document doc = (Document) library.fetchItem("B4ERDVS4");
		
		TagsImpl tags = (TagsImpl) doc.getTags();
		
		// No changes should be NULL
		assertNull(TagsImpl.toRest(tags));
		
		tags.clear();

		// Cleared should be false
		assertEquals(Boolean.FALSE, TagsImpl.toRest(tags));
		
		TagImpl tag = new TagImpl("Foo", TagType.USER, 0, null, null);
		tags.add(tag);
		tag = new TagImpl("Bar", TagType.AUTOMATIC, 0, null, null);
		tags.add(tag);
		
		// Dirty should result in REST data
		List<Map<String,Object>> rest = (List<Map<String, Object>>) TagsImpl.toRest(tags);
		
		assertEquals(2, rest.size());

		assertEquals(TagType.USER.getZoteroType(), rest.get(0).get(ZoteroKeys.Tag.TYPE));
		assertEquals(TagType.AUTOMATIC.getZoteroType(), rest.get(1).get(ZoteroKeys.Tag.TYPE));
		
		assertEquals("Foo", rest.get(0).get(ZoteroKeys.Tag.TAG));
		assertEquals("Bar", rest.get(1).get(ZoteroKeys.Tag.TAG));
	}
}
