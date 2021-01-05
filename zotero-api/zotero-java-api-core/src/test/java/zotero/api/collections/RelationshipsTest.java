package zotero.api.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.RelationshipType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.util.MockRestService;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.RelationshipsImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class RelationshipsTest
{
	private static RelationshipsImpl relationships;

	// Static setups
	private static MockRestService service = new MockRestService();
	private static LibraryImpl library;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		service.setGet(MockRestService.fakeGet);

		library = (LibraryImpl) Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
		
		JsonObject entity = MockRestService.getEntityNode("/users/12345678/items/B4ERDVS4", "<empty>", "GET");
		
		//@formatter:off
		JsonObject object = entity
				.get("item").getAsJsonObject()
				.get("data").getAsJsonObject()
				.get(ZoteroKeys.ItemKeys.RELATIONS).getAsJsonObject();
		//@formatter:on
		
		@SuppressWarnings("unchecked")
		Map<String, Object> creators = new Gson().fromJson(object, Map.class);
		RelationshipsTest.relationships = RelationshipsImpl.fromRest(library, creators);
	}
	
	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		service.initialize();
	}
	
	@Test
	public void testGetTypes()
	{
		assertEquals(1, relationships.getTypes().size());
		assertTrue(relationships.getTypes().contains(RelationshipType.DC_REPLACES));
	}
	
	@Test
	public void testGetRelatedItems()
	{
		RelationSet relatedItems = relationships.getRelatedItems(RelationshipType.DC_REPLACES);
		
		assertEquals(2, relatedItems.size());
		
		Iterator<String> i = relatedItems.iterator();
		
		i.next();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFromRest()
	{
		Map<String,Object> rest = new Gson().fromJson("{\"dc:relation\": [\"http://zotero.org/users/5787467/items/Y22WKAQL\", \"two\"]}", Map.class);
		
		Relationships relationships = RelationshipsImpl.fromRest(library, rest);
		assertNotNull(relationships.getRelatedItems(RelationshipType.DC_RELATION));
		assertEquals(2, relationships.getRelatedItems(RelationshipType.DC_RELATION).size());
		assertTrue(relationships.getRelatedItems(RelationshipType.DC_RELATION).contains("http://zotero.org/users/5787467/items/Y22WKAQL"));
		assertTrue(relationships.getRelatedItems(RelationshipType.DC_RELATION).contains("two"));
		
		rest = new Gson().fromJson("{\"dc:relation\": \"http://zotero.org/users/5787467/items/Y22WKAQL\"}", Map.class);
		
		relationships = RelationshipsImpl.fromRest(library, rest);
		assertNotNull(relationships.getRelatedItems(RelationshipType.DC_RELATION));
		assertEquals(1, relationships.getRelatedItems(RelationshipType.DC_RELATION).size());
		assertTrue(relationships.getRelatedItems(RelationshipType.DC_RELATION).contains("http://zotero.org/users/5787467/items/Y22WKAQL"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToRest()
	{
		RelationshipsImpl relationships = new RelationshipsImpl(library);

		// Empty should serialize an empty list
		Map<String, List<String>> zrs = (Map<String, List<String>>)RelationshipsImpl.toRest(relationships);
		assertNotNull(zrs);
		assertTrue(zrs.isEmpty());
		
		relationships.getRelatedItems(RelationshipType.DC_RELATION).addRelation("http://www.zotero.org/");

		zrs = (Map<String, List<String>>)RelationshipsImpl.toRest(relationships);

		assertNotNull(zrs);
		assertFalse(zrs.isEmpty());
		assertEquals("http://www.zotero.org/", zrs.get(RelationshipType.DC_RELATION.getZoteroName()).get(0));
		
		relationships.clear();
		
		assertEquals(Boolean.FALSE, RelationshipsImpl.toRest(relationships));
	}
}
