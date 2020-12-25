package zotero.apiimpl.search;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import zotero.api.constants.ItemType;
import zotero.api.util.Params;

public class SearchImplTest
{
	@Test
	public void testItemKey()
	{
		Params params = new Params();

		SearchBuilderImpl<SearchBuilderImpl<?>> search = new SearchBuilderImpl<>();
		search.apply(params::addParam);

		assertEquals(0, params.size());

		search.itemKey("foo");
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals("foo", params.get("itemKey").get(0));

		params.clear();

		search.itemKey("bar");
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals("foo,bar", params.get("itemKey").get(0));
	}

	@Test
	public void testSince()
	{
		Params params = new Params();

		SearchBuilderImpl<SearchBuilderImpl<?>> search = new SearchBuilderImpl<>();
		search.apply(params::addParam);

		assertEquals(0, params.size());

		search.since(123);
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(1, params.get("since").size());
		assertEquals("123", params.get("since").get(0));
	}

	@Test
	public void testQuickSearch()
	{
		Params params = new Params();

		SearchBuilderImpl<SearchBuilderImpl<?>> search = new SearchBuilderImpl<>();
		search.apply(params::addParam);

		assertEquals(0, params.size());

		search.quickSearch("stuff");
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(1, params.get("q").size());
		assertEquals("stuff", params.get("q").get(0));
	}

	@Test
	public void testTag()
	{
		Params params = new Params();

		SearchBuilderImpl<SearchBuilderImpl<?>> search = new SearchBuilderImpl<>();
		search.apply(params::addParam);

		assertEquals(0, params.size());

		search.tag("yes");
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(1, params.get("tag").size());
		assertEquals("yes", params.get("tag").get(0));

		params.clear();

		search.notTag("-no");
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(2, params.get("tag").size());
		assertEquals("yes", params.get("tag").get(0));
		assertEquals("-\\-no", params.get("tag").get(1));

		params.clear();

		search.orTags(Arrays.asList(new String[] { "ot1", "ot2" }));
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(3, params.get("tag").size());
		assertEquals("yes", params.get("tag").get(0));
		assertEquals("-\\-no", params.get("tag").get(1));
		assertEquals("ot1||ot2", params.get("tag").get(2));
	}

	@Test
	public void testItemType()
	{
		Params params = new Params();

		SearchBuilderImpl<SearchBuilderImpl<?>> search = new SearchBuilderImpl<>();
		search.apply(params::addParam);

		assertEquals(0, params.size());

		search.itemType(ItemType.ARTWORK);
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(1, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));

		params.clear();

		search.notItemType(ItemType.ATTACHMENT);
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(2, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));
		assertEquals("-" + ItemType.ATTACHMENT.getZoteroName(), params.get("itemType").get(1));

		params.clear();

		search.orItemTypes(Arrays.asList(new ItemType[] { ItemType.BILL, ItemType.BOOK }));
		search.apply(params::addParam);

		assertEquals(1, params.size());
		assertEquals(3, params.get("itemType").size());
		assertEquals(ItemType.ARTWORK.getZoteroName(), params.get("itemType").get(0));
		assertEquals("-" + ItemType.ATTACHMENT.getZoteroName(), params.get("itemType").get(1));
		assertEquals(ItemType.BILL.getZoteroName() + "||" + ItemType.BOOK.getZoteroName(), params.get("itemType").get(2));
	}
}
