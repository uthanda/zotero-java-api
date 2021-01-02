package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyDate;

public class PropertyDateTest
{
	private PropertyDate property = new PropertyDateImpl("testProperty", new Date(0));

	@Test
	public void testGetPropertyType()
	{
		assertEquals(PropertyType.DATE, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		assertEquals("testProperty", property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		assertEquals(new Date(0), property.getValue());
	}

	@Test
	public void testSetValue()
	{
		PropertyDate prop = new PropertyDateImpl("testProperty", new Date(0));
		prop.setValue(new Date(1));

		assertEquals(new Date(1), prop.getValue());
		assertTrue(((PropertyDateImpl)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyDate prop = new PropertyDateImpl("testProperty", new Date(0));
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyDateImpl) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyDate prop = new PropertyDateImpl("testProperty", new Date(0), true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyDate prop = new PropertyDateImpl("testProperty", new Date(0), true);
		prop.setValue(new Date(1));
	}
}
