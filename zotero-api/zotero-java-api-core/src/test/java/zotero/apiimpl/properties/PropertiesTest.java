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
import zotero.api.ZoteroAPIKey;
import zotero.api.collections.Collections;
import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.api.constants.ItemType;
import zotero.api.constants.LinkMode;
import zotero.api.constants.RelationshipType;
import zotero.api.constants.ZoteroKeys;
import zotero.api.util.MockRestService;
import zotero.apiimpl.LibraryImpl;
import zotero.apiimpl.collections.CollectionsImpl;
import zotero.apiimpl.collections.CreatorsImpl;
import zotero.apiimpl.collections.RelationshipsImpl;
import zotero.apiimpl.collections.TagsImpl;
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
		creator.put(ZoteroKeys.Creator.CREATOR_TYPE, "author");
		creator.put(ZoteroKeys.Creator.FIRST_NAME, "John");
		creator.put(ZoteroKeys.Creator.LAST_NAME, "Doe");
		
		List<Map<String,Object>> creators = new ArrayList<>();
		creators.add(creator);
		
		ZoteroRestData data = new ZoteroRestData();
		data.put(ZoteroKeys.Item.ACCESS_DATE, "1970-01-01T00:00:00Z");
		data.put(ZoteroKeys.Item.TITLE, "The Title");
		data.put(ZoteroKeys.Item.VERSION, 12.0);
		data.put(ZoteroKeys.Document.CREATORS, creators);
		data.put(ZoteroKeys.Item.COLLECTIONS, Arrays.asList("Y82V25U2"));
		
		item = new ZoteroRestItem();
		item.setVersion(12);
		item.setData(data);
		
		props = PropertiesImpl.fromRest(library, item);
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
		assertEquals("The Title", props.getString(ZoteroKeys.Item.TITLE));
	}

	@Test
	public void testGetInteger()
	{
		assertEquals(12, props.getInteger(ZoteroKeys.Item.VERSION).intValue());
	}

	@Test
	public void testGetDate()
	{
		assertEquals(0L,props.getDate(ZoteroKeys.Item.ACCESS_DATE).getTime());
	}
	
	@Test
	public void testGetCollectionsProperty()
	{
		Collections collections = (Collections) props.getProperty(ZoteroKeys.Item.COLLECTIONS).getValue();
		Collection collection = collections.iterator().next();
		
		assertEquals("Y82V25U2", collection.getKey());
	}

	@Test
	public void testGetCreatorsProperty()
	{
		Creators creators = (Creators) props.getProperty(ZoteroKeys.Document.CREATORS).getValue();
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
		((PropertyStringImpl)collection.getProperties().getProperty(ZoteroKeys.Collection.PARENT_COLLECTION)).setValue("COL12345");
		
		CollectionsImpl collections = new CollectionsImpl();
		collections.addToCollection(collection);
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Item.TITLE, "A title"));
		properties.addProperty(new PropertyTagsImpl(tags));
		properties.addProperty(new PropertyCreatorsImpl(creators));
		properties.addProperty(new PropertyRelationshipsImpl(relationships));
		properties.addProperty(new PropertyCollectionsImpl(collections));
		
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, properties, false);
		
		assertEquals(5, data.size());
		
		assertEquals("A title", data.get(ZoteroKeys.Item.TITLE));

		assertEquals("Vincent", ((List<Map<String,Object>>)data.get(ZoteroKeys.Document.CREATORS)).get(0).get(ZoteroKeys.Creator.FIRST_NAME));
		assertEquals("Van Gogh", ((List<Map<String,Object>>)data.get(ZoteroKeys.Document.CREATORS)).get(0).get(ZoteroKeys.Creator.LAST_NAME));
		assertEquals("artist", ((List<Map<String,Object>>)data.get(ZoteroKeys.Document.CREATORS)).get(0).get(ZoteroKeys.Creator.CREATOR_TYPE));
		
		assertEquals("Painter", ((List<Map<String,Object>>)data.get(ZoteroKeys.Item.TAGS)).get(0).get(ZoteroKeys.Tag.TAG));
		
		assertEquals("A6C8YX9M", ((List<String>)data.get(ZoteroKeys.Item.COLLECTIONS)).get(0));
		
		assertEquals("B4ERDVS4", ((Map<String,List<String>>)data.get(ZoteroKeys.Item.RELATIONS)).get(RelationshipType.DC_REPLACES.getZoteroName()).get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToRestDelta()
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
		
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Item.TITLE, "A title"));
		properties.addProperty(new PropertyTagsImpl(tags));
		properties.addProperty(new PropertyCreatorsImpl(creators));
		properties.addProperty(new PropertyRelationshipsImpl(relationships));
		properties.addProperty(new PropertyCollectionsImpl(collections));
		
		ZoteroRestData data = new ZoteroRestData();
		
		PropertiesImpl.toRest(data, properties, true);
		
		assertEquals(4, data.size());
		assertTrue(data.containsKey(ZoteroKeys.Item.TAGS));
		assertTrue(data.containsKey(ZoteroKeys.Document.CREATORS));
		assertTrue(data.containsKey(ZoteroKeys.Item.RELATIONS));
		assertTrue(data.containsKey(ZoteroKeys.Item.COLLECTIONS));
	}

	@Test
	public void testInitializeCollectionProperties()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		PropertiesImpl.initializeCollectionProperties(properties);
		
		Set<String> names = properties.getPropertyNames();
		assertEquals(2, names.size());
		assertTrue(names.contains(ZoteroKeys.Collection.NAME));
		assertTrue(names.contains(ZoteroKeys.Collection.PARENT_COLLECTION));
	}

	@Test
	public void testInitializeDocumentProperties()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		PropertiesImpl.initializeDocumentProperties(ItemType.CASE, properties, null);
		
		assertEquals(19, properties.getPropertyNames().size());
	}

	@Test
	public void testInitializeAttachmentProperties()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		
		PropertiesImpl.initializeAttachmentProperties(LinkMode.IMPORTED_FILE, properties);
		
		assertEquals(12, properties.getPropertyNames().size());
		// TODO Need to check the properties here
	}

	@Test
	public void testGetPropertyNames()
	{
		Set<String> names = props.getPropertyNames();
		assertEquals(5, names.size());
		assertTrue(names.contains(ZoteroKeys.Item.VERSION));
		assertTrue(names.contains(ZoteroKeys.Item.TITLE));
		assertTrue(names.contains(ZoteroKeys.Item.ACCESS_DATE));
		assertTrue(names.contains(ZoteroKeys.Document.CREATORS));
		assertTrue(names.contains(ZoteroKeys.Item.COLLECTIONS));
	}

	@Test
	public void testAddProperty()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Item.TITLE, "Foo"));
	
		assertEquals(1, properties.getPropertyNames().size());
		assertTrue(properties.getPropertyNames().contains(ZoteroKeys.Item.TITLE));
		assertEquals("Foo", properties.getString(ZoteroKeys.Item.TITLE));
	}
	
	@Test
	public void testPutValue()
	{
		PropertiesImpl properties = new PropertiesImpl(library);
		properties.addProperty(new PropertyStringImpl(ZoteroKeys.Item.TITLE, "Foo"));
		
		properties.putValue(ZoteroKeys.Item.TITLE, "New value");
	
		assertEquals("New value", properties.getString(ZoteroKeys.Item.TITLE));
		assertTrue(((PropertyStringImpl)properties.getProperty(ZoteroKeys.Item.TITLE)).isDirty());
	}
}
