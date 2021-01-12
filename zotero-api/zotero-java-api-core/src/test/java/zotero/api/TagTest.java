package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import zotero.api.constants.TagType;
import zotero.apiimpl.TagImpl;

public class TagTest
{
	@Test
	public void testCompare()
	{
		TagImpl one = new TagImpl("Foo", TagType.SHARED, 0, null, null);
		TagImpl two = new TagImpl("Foo", TagType.CUSTOM, 0, null, null);
		TagImpl three = new TagImpl("Foo", TagType.CUSTOM, 0, null, null);
		
		assertNotEquals(one, two);
		assertNotEquals(one, three);
		assertEquals(two, three);
	}

}
