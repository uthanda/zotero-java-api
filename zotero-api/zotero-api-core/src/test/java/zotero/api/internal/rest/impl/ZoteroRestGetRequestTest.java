package zotero.api.internal.rest.impl;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.ZoteroRestPaths;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest.Builder;

public class ZoteroRestGetRequestTest
{
	@Test
	public void testBuildUrl() throws URISyntaxException
	{
		ZoteroRestGetRequest.Builder<String> builder = (Builder<String>) ZoteroRestGetRequest.Builder.createBuilder(String.class);
		
		ZoteroRestGetRequest<String> request = (ZoteroRestGetRequest<String>) builder
				.setUsers()
				.apiKey(new ZoteroAPIKey("key"))
				.id("12345")
				.url(ZoteroRestPaths.ITEM)
				.urlParam("key", "theKey")
				.queryParam("param1", "p1")
				.queryParam("param1", "p2")
				.build();
		
		assertEquals("https://api.zotero.org/users/12345/items/theKey?param1=p1&param1=p2",request.buildURL().toString());
	}
}
