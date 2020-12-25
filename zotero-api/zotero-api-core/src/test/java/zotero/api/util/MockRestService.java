package zotero.api.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.util.function.Function;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import zotero.apiimpl.rest.builders.DeleteBuilder;
import zotero.apiimpl.rest.builders.GetBuilder;
import zotero.apiimpl.rest.builders.PatchBuilder;
import zotero.apiimpl.rest.builders.PostBuilder;
import zotero.apiimpl.rest.impl.ZoteroRestDeleteRequest;
import zotero.apiimpl.rest.impl.ZoteroRestGetRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPatchRequest;
import zotero.apiimpl.rest.impl.ZoteroRestPostRequest;

public class MockRestService
{
	public static final Class<?>[] REQEST_CLASSES = { ZoteroRestGetRequest.class, ZoteroRestPostRequest.Builder.class, ZoteroRestPatchRequest.class, ZoteroRestDeleteRequest.Builder.class };
	
	public static final String API_ID = "apiId";
	public static final String API_KEY = "apiKey";

	private static JsonObject data;
	private Function<MockPostRequest, Boolean> postCallback;
	private Function<MockPatchRequest, Boolean> patchCallback;
	private Function<MockDeleteRequest, Boolean> deleteCallback;

	public void initialize() throws NoSuchMethodException, SecurityException
	{
		if (data == null)
		{
			data = (JsonObject) JsonParser.parseReader(new InputStreamReader(MockRestService.class.getResourceAsStream("/zotero/testData.json")));
		}

		PowerMockito.mockStatic(ZoteroRestGetRequest.Builder.class);
		PowerMockito.mockStatic(ZoteroRestPostRequest.Builder.class);
		PowerMockito.mockStatic(ZoteroRestPatchRequest.Builder.class);
		PowerMockito.mockStatic(ZoteroRestDeleteRequest.Builder.class);

		when(ZoteroRestGetRequest.Builder.createBuilder(any())).thenAnswer(new Answer<GetBuilder<?>>()
		{
			@SuppressWarnings("unchecked")
			@Override
			public GetBuilder<?> answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockGetRequest.MockRequestBuilder<>(invocation.getArgumentAt(0, Class.class), data);
			}
		});

		when(ZoteroRestPostRequest.Builder.createBuilder()).thenAnswer(new Answer<PostBuilder>()
		{
			@Override
			public PostBuilder answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockPostRequest.MockRequestBuilder(data, req -> {
					if (postCallback != null)
					{
						return postCallback.apply(req);
					}
					else
					{
						return Boolean.TRUE;
					}
				});
			}
		});
		
		when(ZoteroRestPatchRequest.Builder.createBuilder()).thenAnswer(new Answer<PatchBuilder>()
		{
			@Override
			public PatchBuilder answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockPatchRequest.MockPatchBuilder(data, req -> {
					if (patchCallback != null)
					{
						return patchCallback.apply(req);
					}
					else
					{
						return Boolean.TRUE;
					}
				});
			}
		});
		
		when(ZoteroRestDeleteRequest.Builder.createBuilder()).thenAnswer(new Answer<DeleteBuilder>()
		{
			@Override
			public DeleteBuilder answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockDeleteRequest.MockRequestBuilder(req -> {
					if (deleteCallback != null)
					{
						return deleteCallback.apply(req);
					}
					else
					{
						return Boolean.TRUE;
					}
				});
			}
		});
	}

	public void setPostCallbackFunction(Function<MockPostRequest, Boolean> callback)
	{
		this.postCallback = callback;
	}
	
	public void setPatchCallbackFunction(Function<MockPatchRequest, Boolean> callback)
	{
		this.patchCallback = callback;
	}

	public void setDeleteCallbackFunction(Function<MockDeleteRequest, Boolean> callback)
	{
		this.deleteCallback = callback;
	}
}