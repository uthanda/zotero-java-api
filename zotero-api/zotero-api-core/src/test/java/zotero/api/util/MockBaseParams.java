package zotero.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import zotero.api.ZoteroAPIKey;

public class MockBaseParams
{
	public String url;
	public String id = null;
	public Boolean isUsers = null;
	public ZoteroAPIKey key = null;
	
	public void validate()
	{
		assertNotNull(id);
		assertNotNull(isUsers);
		assertNotNull(key);
		assertNotNull(url);
		
		assertEquals(MockRestService.API_ID, id);
		assertEquals(MockRestService.API_KEY, key.getApiKey());
	}
}
