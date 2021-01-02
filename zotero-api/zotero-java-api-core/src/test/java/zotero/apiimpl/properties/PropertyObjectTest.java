package zotero.apiimpl.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zotero.api.constants.PropertyType;
import zotero.api.exceptions.ZoteroRuntimeException;
import zotero.apiimpl.collections.CreatorsImpl;

public class PropertyObjectTest
{
	private PropertyObjectImpl<CreatorsImpl> property = new PropertyObjectImpl<>("testProperty", CreatorsImpl.class, new CreatorsImpl());

	@Test
	public void testGetPropertyType()
	{
		assertEquals(PropertyType.OBJECT, property.getPropertyType());
	}

	@Test
	public void testGetKey()
	{
		assertEquals("testProperty", property.getKey());
	}
	
	@Test
	public void testGetValue()
	{
		assertNotNull(property.getValue());
		assertTrue(property.getValue() instanceof CreatorsImpl);
	}

	@Test
	public void testSetValue()
	{
		PropertyObjectImpl<CreatorsImpl> prop = new PropertyObjectImpl<>("testProperty", CreatorsImpl.class, new CreatorsImpl());
		prop.setValue(null);

		assertNull(prop.getValue());
		assertTrue(((PropertyObjectImpl<?>)prop).isDirty());
	}
	
	@Test
	public void testClearValue()
	{
		PropertyObjectImpl<CreatorsImpl> prop = new PropertyObjectImpl<>("testProperty", CreatorsImpl.class, new CreatorsImpl());
		prop.clearValue();
		
		assertNull(prop.getValue());
		assertTrue(((PropertyObjectImpl<?>) prop).isDirty());
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testClearValueReadOnly()
	{
		PropertyObjectImpl<CreatorsImpl> prop = new PropertyObjectImpl<>("testProperty", CreatorsImpl.class, new CreatorsImpl(), true);
		prop.clearValue();
	}
	
	@Test(expected = ZoteroRuntimeException.class)
	public void testSetValueReadOnly()
	{
		PropertyObjectImpl<CreatorsImpl> prop = new PropertyObjectImpl<>("testProperty", CreatorsImpl.class, new CreatorsImpl(), true);
		prop.setValue(null);
	}
}
