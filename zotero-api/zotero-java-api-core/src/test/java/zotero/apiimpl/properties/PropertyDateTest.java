package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;

public class PropertyDateTest
{
	@Test
	public void testGetPropertyType()
	{
		PropertyDateImpl property = new PropertyDateImpl("testProperty", new Date(0));
		assertEquals(PropertyType.DATE, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		PropertyDateImpl property = new PropertyDateImpl("testProperty", new Date(0));
		assertEquals("testProperty", property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		PropertyDateImpl property = new PropertyDateImpl("testProperty", new Date(0));
		assertEquals(new Date(0), property.getValue());
	}

	@Test
	public void testSetValue()
	{
		PropertyDateImpl prop = new PropertyDateImpl("testProperty", new Date(0));
		prop.setValue(new Date(1));

		assertEquals(new Date(1), prop.getValue());
		assertTrue(((PropertyDateImpl)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyDateImpl prop = new PropertyDateImpl("testProperty", new Date(0));
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyDateImpl) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyDateImpl prop = new PropertyDateImpl("testProperty", new Date(0), true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyDateImpl prop = new PropertyDateImpl("testProperty", new Date(0), true);
		prop.setValue(new Date(1));
	}
	
	@Test
	public void testToRest()
	{
		PropertyDateImpl prop = new PropertyDateImpl("testProperty", new Date(0));
		assertNull(prop.toRestValue());
		prop.setValue(new Date(1));
		assertEquals(1, prop.toRestValue());
		prop.clearValue();
		assertEquals(Boolean.FALSE, prop.toRestValue());
	}
}
