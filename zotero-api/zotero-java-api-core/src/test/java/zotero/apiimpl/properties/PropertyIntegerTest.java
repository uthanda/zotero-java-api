package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyInteger;

public class PropertyIntegerTest
{
	private PropertyInteger property = new PropertyIntegerImpl("testProperty", 0);

	@Test
	public void testGetPropertyType()
	{
		assertEquals(PropertyType.INTEGER, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		assertEquals("testProperty", property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		assertEquals(0, property.getValue().intValue());
	}

	@Test
	public void testSetValue()
	{
		PropertyIntegerImpl prop = new PropertyIntegerImpl("testProperty", 0);
		prop.setValue(1);

		assertEquals(1, prop.getValue().intValue());
		assertTrue(((PropertyIntegerImpl)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyIntegerImpl prop = new PropertyIntegerImpl("testProperty", 0);
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyIntegerImpl) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyIntegerImpl prop = new PropertyIntegerImpl("testProperty", 0, true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyIntegerImpl prop = new PropertyIntegerImpl("testProperty", 0, true);
		prop.setValue(1);
	}
	
	@Test
	public void testToRest()
	{
		PropertyIntegerImpl prop = new PropertyIntegerImpl("testProperty", 0);
		
		assertNotNull(prop.toRestValue());
		assertEquals(0, prop.toRestValue());
		
		prop.setValue(1);
		assertEquals(1, prop.toRestValue());

		prop.clearValue();
		assertEquals(Boolean.FALSE, prop.toRestValue());
	}
}
