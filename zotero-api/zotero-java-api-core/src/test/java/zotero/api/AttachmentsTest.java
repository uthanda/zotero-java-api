package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zotero.api.iterators.ItemIterator;
import zotero.api.util.MockRestService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClients.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.management.*" })
public class AttachmentsTest
{
	private static MockRestService service = new MockRestService();
	private static Library library;
	private static Document item;

	@BeforeClass
	public static void setUp() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for the static setup
		service.initialize();
		library = Library.createLibrary(MockRestService.API_ID, new ZoteroAPIKey(MockRestService.API_KEY));
		item = (Document) library.fetchItem("B4ERDVS4");
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for each subsequent run
		service.initialize();
	}

	@Test
	public void test() throws IOException
	{
		ItemIterator i = item.fetchChildren();

		Item itemAttachment = i.next();
		itemAttachment = i.next();

		assertTrue(itemAttachment instanceof Attachment);
		assertEquals("BV6GB7ZH", itemAttachment.getKey());

		Attachment attachment = (Attachment) itemAttachment;
		InputStream is = attachment.retrieveContent();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		IOUtils.copy(is, bos);

		// The extra "" are necessary because it's actually a
		// JsonEntity("ZoteroTestContent") that's being serialized
		assertEquals("\"ZoteroTestContent\"", new String(bos.toByteArray()));
	}
}
