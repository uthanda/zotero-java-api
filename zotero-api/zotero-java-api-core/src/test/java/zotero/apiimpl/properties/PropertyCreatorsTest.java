package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import zotero.api.collections.Creators;
import zotero.api.constants.CreatorType;
import zotero.api.constants.PropertyType;
import zotero.api.constants.ZoteroKeys;
import zotero.apiimpl.collections.CreatorsImpl;

public class PropertyCreatorsTest
{
	@Test
	public void testGetPropertyType()
	{
		PropertyCreatorsImpl property = new PropertyCreatorsImpl(new CreatorsImpl(null));
		assertEquals(PropertyType.OBJECT, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		PropertyCreatorsImpl property = new PropertyCreatorsImpl(new CreatorsImpl(null));
		assertEquals(ZoteroKeys.Document.CREATORS, property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		PropertyCreatorsImpl property = new PropertyCreatorsImpl(new CreatorsImpl(null));
		assertNotNull(property.getValue());
		assertTrue(property.getValue() instanceof CreatorsImpl);
	}

	@Test
	public void testIsDirtyOnChangeItem()
	{
		PropertyCreatorsImpl property = new PropertyCreatorsImpl(new CreatorsImpl(null));
		assertNotNull(property.getValue());
		assertTrue(property.getValue() instanceof CreatorsImpl);
		
		Creators creators = property.getValue();
		assertFalse(property.isDirty());
		
		creators.add(CreatorType.ARTIST, "John", "Smith");
		assertTrue(property.isDirty());
	}

	@Test
	public void testToRest()
	{
		PropertyCreatorsImpl property = new PropertyCreatorsImpl(new CreatorsImpl(null));
		assertNotNull(property.getValue());
		assertTrue(property.getValue() instanceof CreatorsImpl);
		
		Creators creators = property.getValue();
		
		creators.add(CreatorType.ARTIST, "John", "Smith");
		
		Object restValue = property.toRestValue();
		assertNotNull(restValue);
		
		assertTrue(restValue instanceof List);
	}
	
	@Test
	public void testSetValue()
	{
		PropertyCreatorsImpl prop = new PropertyCreatorsImpl(new CreatorsImpl(null));
		prop.setValue(null);

		assertNull(prop.getValue());
		assertTrue(((PropertyObjectImpl<?>)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyCreatorsImpl prop = new PropertyCreatorsImpl(new CreatorsImpl(null));
		prop.clearValue();
		
		assertNotNull(prop.getValue());
		assertEquals(0, prop.getValue().size());
		assertTrue(((PropertyObjectImpl<?>) prop).isDirty());
		
		Object rest = prop.toRestValue();
		assertTrue(rest instanceof Boolean);
		assertEquals(Boolean.FALSE, rest);
	}
}
