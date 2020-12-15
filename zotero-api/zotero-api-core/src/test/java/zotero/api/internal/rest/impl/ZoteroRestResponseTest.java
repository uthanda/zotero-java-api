package zotero.api.internal.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.RestGetRequest;
import zotero.api.internal.rest.RestResponse;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest.Builder;
import zotero.api.internal.rest.model.ZoteroRestItem;
import zotero.api.util.MockRestService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ZoteroRestGetRequest.class, HttpClients.class })
public class ZoteroRestResponseTest
{
	private static RestResponse<ZoteroRestItem[]> successfulWithLinks;
	private static RestResponse<ZoteroRestItem[]> successfulNoLinks;
	private static RestResponse<ZoteroRestItem[]> failed404;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		PowerMockito.mockStatic(HttpClients.class);

		when(HttpClients.createDefault()).thenAnswer(new Answer<CloseableHttpClient>()
		{
			@Override
			public CloseableHttpClient answer(InvocationOnMock invocation) throws Throwable
			{
				return createClient();
			}
		});

		successfulWithLinks = new Builder<ZoteroRestItem[]>().apiKey(new ZoteroAPIKey(MockRestService.API_KEY)).url("successfulWithLinks")
				.type(ZoteroRestItem[].class).build().get();
		
		successfulNoLinks = new Builder<ZoteroRestItem[]>().apiKey(new ZoteroAPIKey(MockRestService.API_KEY)).url("successfulNoLinks")
				.type(ZoteroRestItem[].class).build().get();
		
		failed404 = new Builder<ZoteroRestItem[]>().apiKey(new ZoteroAPIKey(MockRestService.API_KEY)).url("failed404")
				.type(ZoteroRestItem[].class).build().get();
	}

	@Test
	public void testWasSuccessful()
	{
		assertTrue(successfulWithLinks.wasSuccessful());
		assertTrue(successfulNoLinks.wasSuccessful());
		assertFalse(failed404.wasSuccessful());
	}

	@Test
	public void testGetErrorMessage()
	{
		assertNull(successfulWithLinks.getErrorMessage());
		assertNull(successfulNoLinks.getErrorMessage());
		assertEquals("Collection not found", failed404.getErrorMessage());
	}

	@Test
	public void testGetResponse()
	{
		ZoteroRestItem[] items = successfulWithLinks.getResponse();
		assertEquals(20, items.length);
		
		items = successfulNoLinks.getResponse();
		assertEquals(20, items.length);
	}

	@Test
	public void testHasNext()
	{
		assertTrue(successfulWithLinks.hasNext());
		assertFalse(successfulNoLinks.hasNext());
		assertFalse(failed404.hasNext());
	}

	@Test
	public void testGetLink()
	{
		assertNotNull(successfulWithLinks.getLink("next"));
		assertNull(successfulNoLinks.getLink("next"));
		assertNull(failed404.getLink("next"));
	}

	@Test
	public void testGetTotalResults()
	{
		assertEquals(10, successfulWithLinks.getTotalResults().intValue());
		assertEquals(10, successfulNoLinks.getTotalResults().intValue());
		assertNull(failed404.getTotalResults());
	}

	@Test
	public void testGetLastModifyVersion()
	{
		assertEquals(12345, successfulWithLinks.getLastModifyVersion().intValue());
		assertEquals(12345, successfulNoLinks.getLastModifyVersion().intValue());
		assertNull(failed404.getLastModifyVersion());
	}
	
	/// Begin the internal mocking methods

	protected static CloseableHttpClient createClient() throws IOException
	{
		CloseableHttpClient client = PowerMockito.mock(CloseableHttpClient.class);
	
		when(client.execute(any())).thenAnswer(new Answer<CloseableHttpResponse>()
		{
			@Override
			public CloseableHttpResponse answer(InvocationOnMock invocation) throws Throwable
			{
				return buildResponse(invocation.getArgumentAt(0, HttpGet.class));
			}
		});
	
		return client;
	}

	protected static CloseableHttpResponse buildResponse(HttpGet get) throws UnsupportedOperationException, IOException
	{
		if (get.getURI().getPath().contains("successfulWithLinks"))
		{
			return createSuccessfulWithLinksResponse();
		}
		else if (get.getURI().getPath().contains("failed404"))
		{
			return createFailed404();
		}
		else if (get.getURI().getPath().contains("successfulNoLinks"))
		{
			return createSuccessfulNoLinksResponse();
		}
		else
		{
			fail(get.getURI().getPath());
			return null;
		}
	}

	private static CloseableHttpResponse createSuccessfulNoLinksResponse() throws IOException
	{
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
	
		InputStream is = ZoteroRestResponseTest.class.getResourceAsStream("/zotero/api/users_collection_top.json");
	
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
	
		when(entity.getContent()).thenReturn(is);
	
		Header totalResults = PowerMockito.mock(Header.class);
		when(totalResults.getValue()).thenReturn("10");
	
		Header lastVersion = PowerMockito.mock(Header.class);
		when(lastVersion.getValue()).thenReturn("12345");
	
		Header link = PowerMockito.mock(Header.class);
		when(link.getValue()).thenReturn(
				"<https://www.zotero.org/users/5787467/collections>; rel=\"alternate\"");
	
		StatusLine statusLine = PowerMockito.mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
	
		when(response.getEntity()).thenReturn(entity);
		when(response.getFirstHeader("Total-Results")).thenReturn(totalResults);
		when(response.getFirstHeader("Last-Modified-Version")).thenReturn(lastVersion);
		when(response.getFirstHeader("Link")).thenReturn(link);
		when(response.getStatusLine()).thenReturn(statusLine);
	
		return response;
	}
	
	private static CloseableHttpResponse createSuccessfulWithLinksResponse() throws IOException
	{
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		InputStream is = ZoteroRestResponseTest.class.getResourceAsStream("/zotero/api/users_collection_top.json");
		
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		
		when(entity.getContent()).thenReturn(is);
		
		Header totalResults = PowerMockito.mock(Header.class);
		when(totalResults.getValue()).thenReturn("10");
		
		Header lastVersion = PowerMockito.mock(Header.class);
		when(lastVersion.getValue()).thenReturn("12345");
		
		Header link = PowerMockito.mock(Header.class);
		when(link.getValue()).thenReturn(
				"<https://api.zotero.org/users/5787467/collections?start=25>; rel=\"next\", <https://api.zotero.org/users/5787467/collections?start=75>; rel=\"last\", <https://www.zotero.org/users/5787467/collections>; rel=\"alternate\"");
		
		StatusLine statusLine = PowerMockito.mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
		
		when(response.getEntity()).thenReturn(entity);
		when(response.getFirstHeader("Total-Results")).thenReturn(totalResults);
		when(response.getFirstHeader("Last-Modified-Version")).thenReturn(lastVersion);
		when(response.getFirstHeader("Link")).thenReturn(link);
		when(response.getStatusLine()).thenReturn(statusLine);
		
		return response;
	}

	private static CloseableHttpResponse createFailed404() throws IOException
	{
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
	
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
	
		when(entity.getContent()).thenReturn(new ByteArrayInputStream("Collection not found".getBytes()));
	
		Header totalResults = PowerMockito.mock(Header.class);
		when(totalResults.getValue()).thenReturn("10");
	
		Header lastVersion = PowerMockito.mock(Header.class);
		when(lastVersion.getValue()).thenReturn("12345");
	
		Header link = PowerMockito.mock(Header.class);
		when(link.getValue()).thenReturn(
				"<https://api.zotero.org/users/5787467/collections?start=25>; rel=\"next\", <https://api.zotero.org/users/5787467/collections?start=75>; rel=\"last\", <https://www.zotero.org/users/5787467/collections>; rel=\"alternate\"");
	
		StatusLine statusLine = PowerMockito.mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
	
		when(response.getEntity()).thenReturn(entity);
		when(response.getStatusLine()).thenReturn(statusLine);
	
		return response;
	}

}
