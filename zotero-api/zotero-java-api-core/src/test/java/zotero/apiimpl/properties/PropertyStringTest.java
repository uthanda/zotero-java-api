package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyString;

public class PropertyStringTest
{
	private PropertyString property = new PropertyStringImpl("testProperty", "Foo");

	@Test
	public void testGetPropertyType()
	{
		assertEquals(PropertyType.STRING, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		assertEquals("testProperty", property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		assertEquals("Foo", property.getValue());
	}

	@Test
	public void testSetValue()
	{
		PropertyString prop = new PropertyStringImpl("testProperty", "Foo");
		prop.setValue("Bar");

		assertEquals("Bar", prop.getValue());
		assertTrue(((PropertyStringImpl)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyStringImpl prop = new PropertyStringImpl("testProperty", "Foo");
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyStringImpl) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyString prop = new PropertyStringImpl("testProperty", "Foo", true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyString prop = new PropertyStringImpl("testProperty", "Foo", true);
		prop.setValue("Bar");
	}
	
	@Test
	public void testToRest()
	{
		PropertyStringImpl prop = new PropertyStringImpl("testProperty", "Foo");
		assertEquals("Foo", prop.toRestValue());
		prop.setValue("Bar");
		assertEquals("Bar", prop.toRestValue());
		prop.clearValue();
		assertEquals(Boolean.FALSE, prop.toRestValue());
	}
}
