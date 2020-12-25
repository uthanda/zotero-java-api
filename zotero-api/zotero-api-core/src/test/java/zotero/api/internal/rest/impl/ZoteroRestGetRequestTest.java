package zotero.api.internal.rest.impl;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpGet.class, ZoteroRestGetRequest.class})
public class ZoteroRestGetRequestTest
{
	@Mock
	private HttpGet get;
	
	@Test
	public void test() throws Exception
	{
		PowerMock.expectNew(HttpGet.class).andReturn(get);
	}
}
