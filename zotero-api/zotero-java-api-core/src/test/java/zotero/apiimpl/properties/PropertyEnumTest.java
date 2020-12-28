package zotero.apiimpl.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import zotero.api.constants.CreatorType;
import zotero.api.constants.PropertyType;
import zotero.api.properties.PropertyEnum;

public class PropertyEnumTest
{
	private PropertyEnum<CreatorType> property = new PropertyEnumImpl<>("testProperty", CreatorType.class, null);
	
	@Test
	public void test()
	{
		assertEquals(PropertyType.ENUM, property.getPropertyType());
		assertEquals("testProperty", property.getKey());
		assertEquals(CreatorType.class, property.getType());
		
		property.setValue(CreatorType.ARTIST);
		assertEquals(CreatorType.ARTIST, property.getValue());
		assertTrue(((PropertyEnumImpl<CreatorType>)property).isDirty());
	}
}
