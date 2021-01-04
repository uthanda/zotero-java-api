package zotero.api.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import zotero.api.Library;
import zotero.api.ZoteroAPIKey;
import zotero.api.constants.CreatorType;
import zotero.api.util.MockRestService;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CreatorsImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class CreatorsTest
{
	private static Creators creators;

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
		JsonArray array = entity
				.get("item").getAsJsonObject()
				.get("data").getAsJsonObject()
				.get("creators").getAsJsonArray();
		//@formatter:on
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> creators = new Gson().fromJson(array, List.class);
		CreatorsTest.creators = CreatorsImpl.fromRest(library, creators);
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		service.initialize();
	}
	
	@Test
	public void testRead()
	{
		assertEquals(5,creators.size());
	}
	
	@Test
	public void testIsDirty()
	{
		CreatorsImpl creators = new CreatorsImpl(library);
		assertFalse(creators.isDirty());
		creators.add(CreatorType.ARTIST, "John", "Smith");
		assertTrue(creators.isDirty());
	}
	
	@Test
	public void testToRest()
	{
		
	}
}
