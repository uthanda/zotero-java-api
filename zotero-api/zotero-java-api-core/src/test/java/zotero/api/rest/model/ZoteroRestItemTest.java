package zotero.api.rest.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.gson.Gson;

import zotero.apiimpl.rest.model.ZoteroRestItem;

public class ZoteroRestItemTest
{
	@Test
	public void testDeserializeCollectionItems() throws IOException
	{
		InputStream is = ZoteroRestItemTest.class.getResourceAsStream("/zotero/api/user_collection_key_items.json");

		Gson gson = new Gson();

		ZoteroRestItem[] items = gson.fromJson(new InputStreamReader(is), ZoteroRestItem[].class);

		is.close();

		assertEquals(7, items.length);
	}

	@Test
	public void testDeserializeItemsItemKey() throws IOException
	{
		InputStream is = ZoteroRestItemTest.class.getResourceAsStream("/zotero/api/user_items_itemKey.json");

		Gson gson = new Gson();

		ZoteroRestItem item = gson.fromJson(new InputStreamReader(is), ZoteroRestItem.class);

		is.close();

		assertNotNull(item);

		assertEquals("B4ERDVS4", item.getKey());
	}

	@Test
	public void testDeserializeTop() throws IOException
	{
		InputStream is = ZoteroRestItemTest.class.getResourceAsStream("/zotero/api/users_collection_top.json");

		Gson gson = new Gson();

		ZoteroRestItem[] items = gson.fromJson(new InputStreamReader(is), ZoteroRestItem[].class);

		is.close();

		assertEquals(20, items.length);
	}

	@Test
	public void testDeserialize() throws IOException
	{
		InputStream is = ZoteroRestItemTest.class.getResourceAsStream("/zotero/api/users_collections.json");

		Gson gson = new Gson();

		ZoteroRestItem[] items = gson.fromJson(new InputStreamReader(is), ZoteroRestItem[].class);

		is.close();

		assertEquals(25, items.length);

		assertEquals("FJ3SUIFZ", items[0].getKey());
		assertEquals(2885, items[0].getVersion().intValue());
		assertEquals(3, items[0].getLinks().size());
		assertEquals(2, items[0].getMeta().size());
		assertEquals(5, items[0].getData().size());
	}

}
