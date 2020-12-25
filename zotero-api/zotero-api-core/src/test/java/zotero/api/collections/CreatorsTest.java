package zotero.api.collections;

import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.apiimpl.collections.CreatorsImpl;

@RunWith(PowerMockRunner.class)
public class CreatorsTest
{
	private static JsonObject data;
	private static Creators creators;

	@BeforeClass
	public static void setup()
	{
		data = (JsonObject) JsonParser.parseReader(new InputStreamReader(CreatorsTest.class.getResourceAsStream("/zotero/testData.json")));
		JsonArray array = data.get(ZoteroRestPaths.ITEM).getAsJsonObject().get("/key=B4ERDVS4").getAsJsonObject().get("<empty>").getAsJsonObject().get("item").getAsJsonObject().get("data").getAsJsonObject().get("creators").getAsJsonArray();
		
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> creators = new Gson().fromJson(array, List.class);
		CreatorsTest.creators = CreatorsImpl.fromRest(creators);
	}
	
	@Test
	public void testRead()
	{
		assertEquals(5,creators.size());
	}
}
