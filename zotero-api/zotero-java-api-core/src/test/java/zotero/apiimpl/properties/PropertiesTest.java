package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import zotero.api.Collection;
import zotero.api.Library;
import zotero.api.Tag;
import zotero.api.auth.ZoteroAPIKey;
import zotero.api.collections.Collections;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.api.constants.RelationshipType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.util.MockRestService;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CollectionsImpl;
import zotero.apiimpl.collections.CreatorsImpl;
import zotero.apiimpl.collections.RelationshipsImpl;
import zotero.apiimpl.collections.TagsImpl;
import zotero.apiimpl.rest.model.SerializationMode;
import zotero.apiimpl.rest.model.ZoteroRestData;
import zotero.apiimpl.rest.model.ZoteroRestItem;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class PropertiesTest
{
	private static MockRestService service = new MockRestService();
	private static LibraryImpl library;
	private static ZoteroRestItem item;
	private static PropertiesImpl props;
	
	@BeforeClass
	public static void setup() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = (LibraryImpl) Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
		
		Map<String,Object> creator = new HashMap<>();
		creator.put(ZoteroKeys.CreatorKeys.CREATOR_TYPE, "author");
		creator.put(ZoteroKeys.CreatorKeys.FIRST_NAME, "John");
		creator.put(ZoteroKeys.CreatorKeys.LAST_NAME, "Doe");
		
		List<Map<String,Object>> creators = new ArrayList<>();
		creators.add(creator);
		
		ZoteroRestData data = new ZoteroRestData();
		data.put(ZoteroKeys.ItemKeys.ACCESS_DATE, "1970-01-01T00:00:00Z");
		data.put(ZoteroKeys.ItemKeys.TITLE, "The Title");
		data.put(ZoteroKeys.ItemKeys.VERSION, 12.0);
		data.put(ZoteroKeys.DocumentKeys.CREATORS, creators);
		data.put(ZoteroKeys.ItemKeys.COLLECTIONS, Arrays.asList("Y82V25U2"));
		
		item = new ZoteroRestItem();
		item.setVersion(12);
		item.setData(data);
		
		props = new PropertiesImpl(library);
		props.fromRest(library, item.getData());
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for each subsequent run
		service.initialize();
	}

	@Test
	public void testGetString()
	{
		assertEquals("The Title", props.getString(ZoteroKeys.ItemKeys.TITLE));
	}

	@Test
	public void testGetInteger()
	{
		assertEquals(12, props.getInteger(ZoteroKeys.ItemKeys.VERSION).intValue());
	}

	@Test
	public void testGetDate()
	{
		assertEquals(0L,props.getDate(ZoteroKeys.ItemKeys.ACCESS_DATE).getTime());
	}
	
	@Test
	public void testGetCollectionsProperty()
	{
		Collections collections = (Collections) props.getProperty(ZoteroKeys.ItemKeys.COLLECTIONS).getValue();
		Collection collection = collections.iterator().next();
		
		assertEquals("Y82V25U2", collection.getKey());
	}

	@Test
	public void testGetCreatorsProperty()
	{
		Creators creators = (Creators) props.getProperty(ZoteroKeys.DocumentKeys.CREATORS).getValue();
		assertEquals(1, creators.size());
	}

	@Test
	public void testFromRest()
	{
		assertNotNull(props);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testToRestFull()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		CreatorsImpl creators = new CreatorsImpl(library);
		creators.add(CreatorType.ARTIST, "Vincent", "Van Gogh");
		
		Tag tag = library.createTag("Painter");
		
		TagsImpl tags = new TagsImpl();
		tags.add(tag);
		
		RelationshipsImpl relationships = new RelationshipsImpl(library);
		relationships.getRelatedItems(RelationshipType.DC_REPLACES).addRelation("B4ERDVS4");
		
		Collection collection = library.fetchCollection("A6C8YX9M");
		((PropertyStringImpl)collection.getProperties().getProperty(ZoteroKeys.CollectionKeys.PARENT_COLLECTION)).setValue("COL12345");
		
		CollectionsImpl collections = new CollectionsImpl();
		collections.addToCollection(collection);
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.TITLE, "A title"));
		properties.addProperty(new PropertyTagsImpl(tags));
		properties.addProperty(new PropertyCreatorsImpl(creators));
		properties.addProperty(new PropertyRelationshipsImpl(relationships));
		properties.addProperty(new PropertyCollectionsImpl(collections));
		
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, properties, SerializationMode.FULL);
		
		assertEquals(5, data.size());
		
		assertEquals("A title", data.get(ZoteroKeys.ItemKeys.TITLE));

		assertEquals("Vincent", ((List<Map<String,Object>>)data.get(ZoteroKeys.DocumentKeys.CREATORS)).get(0).get(ZoteroKeys.CreatorKeys.FIRST_NAME));
		assertEquals("Van Gogh", ((List<Map<String,Object>>)data.get(ZoteroKeys.DocumentKeys.CREATORS)).get(0).get(ZoteroKeys.CreatorKeys.LAST_NAME));
		assertEquals("artist", ((List<Map<String,Object>>)data.get(ZoteroKeys.DocumentKeys.CREATORS)).get(0).get(ZoteroKeys.CreatorKeys.CREATOR_TYPE));
		
		assertEquals("Painter", ((List<Map<String,Object>>)data.get(ZoteroKeys.ItemKeys.TAGS)).get(0).get(ZoteroKeys.TagKeys.TAG));
		
		assertEquals("A6C8YX9M", ((List<String>)data.get(ZoteroKeys.ItemKeys.COLLECTIONS)).get(0));
		
		assertEquals("B4ERDVS4", ((Map<String,List<String>>)data.get(ZoteroKeys.ItemKeys.RELATIONS)).get(RelationshipType.DC_REPLACES.getZoteroName()).get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToRestDeltaNoTitle()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		CreatorsImpl creators = CreatorsImpl.fromRest(library, new Gson().fromJson("[{'creatorType':'author','firstName':'Vincent','lastName':'Van Gogh'}]", List.class));
		creators.add(CreatorType.ARTIST, "Steve", "Taylor");
		
		TagsImpl tags = new TagsImpl(Arrays.asList(library.createTag("Painter")));
		tags.add(library.createTag("Singer"));
		
		RelationshipsImpl relationships = RelationshipsImpl.fromRest(library, new Gson().fromJson("{'dc:replaces':['KEY123']}", Map.class));
		relationships.getRelatedItems(RelationshipType.OWL_SAMEAS).addRelation("B4ERDVS4");

		Collection collection = library.fetchCollection("A6C8YX9M");
		
		CollectionsImpl collections = CollectionsImpl.fromRest(library, Arrays.asList("COLL1234"));
		collections.addToCollection(collection);
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.TITLE, "A title"));
		properties.addProperty(new PropertyTagsImpl(tags));
		properties.addProperty(new PropertyCreatorsImpl(creators));
		properties.addProperty(new PropertyRelationshipsImpl(relationships));
		properties.addProperty(new PropertyCollectionsImpl(collections));
		
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, properties, SerializationMode.PARTIAL);
		
		assertEquals(4, data.size());
		assertTrue(data.containsKey(ZoteroKeys.ItemKeys.TAGS));
		assertTrue(data.containsKey(ZoteroKeys.DocumentKeys.CREATORS));
		assertTrue(data.containsKey(ZoteroKeys.ItemKeys.RELATIONS));
		assertTrue(data.containsKey(ZoteroKeys.ItemKeys.COLLECTIONS));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToRestDeltaTitleOnly()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		CreatorsImpl creators = CreatorsImpl.fromRest(library, new Gson().fromJson("[{'creatorType':'author','firstName':'Vincent','lastName':'Van Gogh'}]", List.class));
		
		TagsImpl tags = new TagsImpl(Arrays.asList(library.createTag("Painter")));
		
		RelationshipsImpl relationships = RelationshipsImpl.fromRest(library, new Gson().fromJson("{'dc:replaces':['KEY123']}", Map.class));
		
		CollectionsImpl collections = CollectionsImpl.fromRest(library, Arrays.asList("COLL1234"));
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.TITLE, "A title"));
		properties.addProperty(new PropertyTagsImpl(tags));
		properties.addProperty(new PropertyCreatorsImpl(creators));
		properties.addProperty(new PropertyRelationshipsImpl(relationships));
		properties.addProperty(new PropertyCollectionsImpl(collections));
		
		properties.putValue(ZoteroKeys.ItemKeys.TITLE, "A new title");
		
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, properties, SerializationMode.PARTIAL);
		
		assertEquals(1, data.size());
		assertTrue(data.containsKey(ZoteroKeys.ItemKeys.TITLE));
	}

	@Test
	public void testGetPropertyNames()
	{
		Set<String> names = props.getPropertyNames();
		assertEquals(5, names.size());
		assertTrue(names.contains(ZoteroKeys.ItemKeys.VERSION));
		assertTrue(names.contains(ZoteroKeys.ItemKeys.TITLE));
		assertTrue(names.contains(ZoteroKeys.ItemKeys.ACCESS_DATE));
		assertTrue(names.contains(ZoteroKeys.DocumentKeys.CREATORS));
		assertTrue(names.contains(ZoteroKeys.ItemKeys.COLLECTIONS));
	}

	@Test
	public void testAddProperty()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.TITLE, "Foo"));
	
		assertEquals(1, properties.getPropertyNames().size());
		assertTrue(properties.getPropertyNames().contains(ZoteroKeys.ItemKeys.TITLE));
		assertEquals("Foo", properties.getString(ZoteroKeys.ItemKeys.TITLE));
	}
	
	@Test
	public void testPutValue()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.ItemKeys.TITLE, "Foo"));
		
		properties.putValue(ZoteroKeys.ItemKeys.TITLE, "New value");
	
		assertEquals("New value", properties.getString(ZoteroKeys.ItemKeys.TITLE));
		assertTrue(((PropertyStringImpl)properties.getProperty(ZoteroKeys.ItemKeys.TITLE)).isDirty());
	}
}
