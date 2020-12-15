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

import zotero.api.internal.rest.builders.GetBuilder;
import zotero.api.internal.rest.builders.PutBuilder;
import zotero.api.internal.rest.impl.ZoteroRestGetRequest;
import zotero.api.internal.rest.impl.ZoteroRestPutRequest;

public class MockRestService
{
	public static final String API_ID = "apiId";
	public static final String API_KEY = "apiKey";

	private static JsonObject data;
	private Function<MockPutRequest, Boolean> putCallback;

	public void initialize() throws NoSuchMethodException, SecurityException
	{
		if (data == null)
		{
			data = (JsonObject) JsonParser.parseReader(new InputStreamReader(MockRestService.class.getResourceAsStream("/zotero/testData.json")));
		}

		PowerMockito.mockStatic(ZoteroRestGetRequest.Builder.class);
		PowerMockito.mockStatic(ZoteroRestPutRequest.Builder.class);

		when(ZoteroRestGetRequest.Builder.createBuilder(any())).thenAnswer(new Answer<GetBuilder<?>>()
		{
			@Override
			public GetBuilder<?> answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockGetRequest.MockRequestBuilder<>(invocation.getArgumentAt(0, Class.class), data);
			}
		});

		when(ZoteroRestPutRequest.Builder.createBuilder()).thenAnswer(new Answer<PutBuilder>()
		{
			@Override
			public PutBuilder answer(InvocationOnMock invocation) throws Throwable
			{
				return new MockPutRequest.MockRequestBuilder(data, req -> {
					if (putCallback != null)
					{
						return putCallback.apply(req);
					}
					else
					{
						return Boolean.TRUE;
					}
				});
			}
		});
	}

	public void setPutCallbackFunction(Function<MockPutRequest, Boolean> callback)
	{
		this.putCallback = callback;
	}
}