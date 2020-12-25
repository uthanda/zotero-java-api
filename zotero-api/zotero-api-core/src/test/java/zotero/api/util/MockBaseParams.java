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
	public boolean isSpecial = false;
	
	public void validate()
	{
		assertNotNull(url);
		
		assertEquals(MockRestService.API_ID, id);
		assertEquals(MockRestService.API_KEY, key.getApiKey());
		
		if(isSpecial) {
			return;
		}

		assertNotNull(id);
		assertNotNull(isUsers);
		assertNotNull(key);
	}
}
