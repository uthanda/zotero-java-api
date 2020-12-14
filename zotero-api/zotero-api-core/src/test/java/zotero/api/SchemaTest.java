package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import zotero.api.internal.rest.schema.ZoteroSchema;
import zotero.api.internal.rest.schema.ZoteroSchema.ZoteroField;
import zotero.api.internal.rest.schema.ZoteroSchema.ZoteroType;

public class SchemaTest
{
	private static ZoteroSchema schema;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		schema = ZoteroSchema.getCurrentSchema();
		assertNotNull(schema);
	}

	@Test
	public void testGetMajorVersion()
	{
		assertEquals(3, schema.getMajorVersion());
	}

	@Test
	public void testGetMinorVersion()
	{
		assertEquals(3, schema.getMajorVersion());
	}

	@Test
	public void testGetFields()
	{
		Map<String, ZoteroField> fields = schema.getFields();
		assertEquals(2, fields.size());
	}

	@Test
	public void testGetTypes()
	{
		List<ZoteroType> fields = schema.getTypes();
		assertEquals(36, fields.size());
	}

}
