package zotero.api.util;

import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.function.Function;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MockRestService
{
	public static final String API_ID = "apiId";
	public static final String API_KEY = "apiKey";

	private static JsonObject data;

	private Function<HttpGet, CloseableHttpResponse> get = fakeGet;
	private Function<HttpPost, CloseableHttpResponse> post = postSuccess;
	private Function<HttpPatch, CloseableHttpResponse> patch = patchSuccess;
	private Function<HttpPut, CloseableHttpResponse> put = putSuccess;
	private Function<HttpDelete, CloseableHttpResponse> delete = deleteSuccess;

	public void initialize() throws NoSuchMethodException, SecurityException, ClientProtocolException, IOException
	{
		if (data == null)
		{
			data = (JsonObject) JsonParser.parseReader(new InputStreamReader(MockRestService.class.getResourceAsStream("/zotero/testData.json")));
		}

		CloseableHttpClient client = PowerMockito.mock(CloseableHttpClient.class);
		PowerMockito.when(client.execute(any())).thenAnswer(new Answer<CloseableHttpResponse>()
		{
			@Override
			public CloseableHttpResponse answer(InvocationOnMock invocation) throws Throwable
			{
				HttpUriRequest request = invocation.getArgument(0);

				if (request instanceof HttpGet)
				{
					return get.apply((HttpGet) request);
				}
				else if (request instanceof HttpPost)
				{
					return post.apply((HttpPost) request);
				}
				else if (request instanceof HttpPatch)
				{
					return patch.apply((HttpPatch) request);
				}
				else if (request instanceof HttpPut)
				{
					return put.apply((HttpPut) request);
				}
				else if (request instanceof HttpDelete)
				{
					return delete.apply((HttpDelete) request);
				}
				else
				{
					throw new IllegalStateException("Invalid request type");
				}
			}
		});

		PowerMockito.mockStatic(HttpClients.class);
		PowerMockito.when(HttpClients.createDefault()).thenReturn(client);
	}

	public void setGet(Function<HttpGet, CloseableHttpResponse> get)
	{
		this.get = get;
	}

	public void setPost(Function<HttpPost, CloseableHttpResponse> post)
	{
		this.post = post;
	}

	public void setPatch(Function<HttpPatch, CloseableHttpResponse> patch)
	{
		this.patch = patch;
	}

	public void setPut(Function<HttpPut, CloseableHttpResponse> put)
	{
		this.put = put;
	}

	public void setDelete(Function<HttpDelete, CloseableHttpResponse> delete)
	{
		this.delete = delete;
	}

	public static Function<HttpGet, CloseableHttpResponse> fakeGet = get -> {

		URI uri = get.getURI();

		String path = uri.getPath();
		String query = uri.getQuery();
		query = query != null ? query : "<empty>";

		if (!data.has(path))
		{
			try
			{
				return new TestResponse(HttpURLConnection.HTTP_NOT_FOUND, new StringEntity(String.format("Not found: '%s'.'%s'", path, query)));
			}
			catch (UnsupportedEncodingException e1)
			{
				throw new RuntimeException(e1);
			}
		}

		JsonObject pathNode = data.get(path).getAsJsonObject();

		if (!pathNode.has(query))
		{
			String format = String.format("Not found: '%s'.'%s'", path, query);
			return new TestResponse(HttpURLConnection.HTTP_NOT_FOUND, createStringEntity(format));
		}

		JsonObject node = pathNode.get(query).getAsJsonObject();
		JsonObject headers = node.get("headers").getAsJsonObject();
		JsonElement item = node.get("item");

		HttpEntity entity = new InputStreamEntity(new ByteArrayInputStream(item.toString().getBytes()), ContentType.APPLICATION_JSON);

		TestResponse response = new TestResponse(HttpURLConnection.HTTP_OK, entity);

		// Add the headers
		headers.entrySet().forEach(e -> response.addHeader(e.getKey(), e.getValue().getAsString()));

		response.setEntity(entity);

		return response;
	};

	private static StringEntity createStringEntity(String format)
	{
		try
		{
			return new StringEntity(format);
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new RuntimeException(e1);
		}
	}

	public static Function<HttpPost, CloseableHttpResponse> postSuccess = post -> {
		return new TestResponse(HttpURLConnection.HTTP_OK, createStringEntity(""));
	};

	public static Function<HttpDelete, CloseableHttpResponse> deleteSuccess = delete -> {
		return new TestResponse(HttpURLConnection.HTTP_OK, createStringEntity(""));
	};

	public static Function<HttpPatch, CloseableHttpResponse> patchSuccess = patch -> {
		return new TestResponse(HttpURLConnection.HTTP_OK, createStringEntity(""));
	};

	public static Function<HttpPut, CloseableHttpResponse> putSuccess = put -> {
		return new TestResponse(HttpURLConnection.HTTP_OK, createStringEntity(""));
	};
}