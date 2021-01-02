package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zotero.api.constants.CreatorType;
import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.api.properties.PropertyEnum;

public class PropertyEnumTest
{
	private PropertyEnum<CreatorType> property = new PropertyEnumImpl<>("testProperty", CreatorType.class, CreatorType.ARTIST);

	@Test
	public void testGetPropertyType()
	{
		assertEquals(PropertyType.ENUM, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		assertEquals("testProperty", property.getKey());
	}

	@Test
	public void testGetType()
	{
		assertEquals(CreatorType.class, property.getType());
	}
	
	@Test
	public void testGetValue()
	{
		assertEquals(CreatorType.ARTIST, property.getValue());
	}

	@Test
	public void testSetValue()
	{
		PropertyEnum<CreatorType> prop = new PropertyEnumImpl<>("testProperty", CreatorType.class, null);
		prop.setValue(CreatorType.ARTIST);

		assertEquals(CreatorType.ARTIST, prop.getValue());
		assertTrue(((PropertyEnumImpl<CreatorType>) prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyEnum<CreatorType> prop = new PropertyEnumImpl<>("testProperty", CreatorType.class, CreatorType.ARTIST);
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyEnumImpl<CreatorType>) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyEnum<CreatorType> prop = new PropertyEnumImpl<>("testProperty", CreatorType.class, CreatorType.ARTIST, true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyEnum<CreatorType> prop = new PropertyEnumImpl<>("testProperty", CreatorType.class, CreatorType.ARTIST, true);
		prop.setValue(CreatorType.ARTIST);
	}
}
