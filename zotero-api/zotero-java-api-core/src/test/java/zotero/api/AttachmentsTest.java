package zotero.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import zotero.api.constants.LinkMode;
import zotero.api.constants.ZoteroKeys;
import zotero.api.iterators.AttachmentIterator;
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
		library = Library.createLibrary(MockRestService.API_ID.toString(), new ZoteroAPIKey(MockRestService.API_KEY));
		item = (Document) library.fetchItem("B4ERDVS4");
	}

	@Before
	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		// Initialize the mock service for each subsequent run
		service.initialize();
	}

	@Test
	public void testRetrieve() throws IOException
	{
		AttachmentIterator i = item.fetchAttachments();

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

	@Test
	public void testCreateLinkedFile()
	{
		Attachment attachment = library.createAttachment(LinkMode.LINKED_FILE);
		attachment.setTitle("Test Attachment");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.PATH, "path/to/file");
		attachment.save();

		assertNotNull(attachment.getKey());
	}

	@Test
	public void testCreateLinkedUrl()
	{
		Attachment attachment = library.createAttachment(LinkMode.LINKED_URL);
		attachment.setTitle("Zotero Website");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.URL, "http://www.zotero.org/");
		attachment.save();

		assertNotNull(attachment.getKey());
	}

	@Test
	public void testCreateImportedUrl()
	{
		Attachment attachment = library.createAttachment(LinkMode.IMPORTED_URL);
		attachment.setTitle("Zotero Website");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.URL, "http://www.zotero.org/");
		attachment.save();

		assertNotNull(attachment.getKey());
	}

	@Test
	public void testCreateImportedFile()
	{
		service.setPost(post -> {
			HttpEntity entity = post.getEntity();

			if (entity.getContentType().getValue().startsWith(ContentType.MULTIPART_FORM_DATA.getMimeType()))
			{
				validateFormData(post);
				return MockRestService.createSimpleResponse(HttpURLConnection.HTTP_CREATED);
			}

			return MockRestService.postSuccess.apply(post);

		});

		Attachment attachment = library.createAttachment(LinkMode.IMPORTED_FILE);
		attachment.setTitle("Zotero Sample Attachment");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.FILENAME, "zotero.properties");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.MTIME, "1609673828440");
		attachment.getProperties().putValue(ZoteroKeys.AttachmentKeys.CONTENT_TYPE, "text/plain");
		attachment.provideContent(new ByteArrayInputStream("testContent".getBytes()), 11L, "a9d008afc51e435b813611042192eb74");
//		attachment.save();
//
//		assertNotNull(attachment.getKey());
	}

	private void validateFormData(HttpPost post)
	{
		try
		{
			performValidation(post);
		}
		catch (IOException | RuntimeException | MessagingException e)
		{
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void performValidation(HttpPost post) throws RuntimeException, IOException, JsonSyntaxException, MessagingException
	{
		// Long way around the horn, but we need to get the response to ensure we're building the post correctly
		CloseableHttpResponse response = MockRestService.getEntityFromData("/users/12345678/items/VBMZJCM7/file", "md5=a9d008afc51e435b813611042192eb74&filename=zotero.properties&filesize=11&mtime=1609673828440", "POST");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.getEntity().writeTo(bos);

		String json = new String(bos.toByteArray());

		Map<String, Object> body = new Gson().fromJson(json, Map.class);
		Map<String, String> params = (Map<String, String>) body.get("params");

		assertEquals(body.get("url"), post.getURI().toString());

		HttpEntity entity = post.getEntity();

		bos = new ByteArrayOutputStream();

		entity.writeTo(bos);

		MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(bos.toByteArray(), ContentType.MULTIPART_FORM_DATA.getMimeType()));

		for (int i = 0; i < multipart.getCount(); i++)
		{

			BodyPart part = multipart.getBodyPart(0);
			assertNotNull(part);

			String name = part.getHeader("Content-Disposition")[0].substring(17).replace("\"", "");

			bos = new ByteArrayOutputStream();

			IOUtils.copy(part.getDataHandler().getDataSource().getInputStream(), bos);

			assertEquals(params.get(name), new String(bos.toByteArray()));
		}
	}
}
