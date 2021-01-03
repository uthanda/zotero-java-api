package zotero.apiimpl.rest.request.builders;

import static zotero.apiimpl.rest.ZoteroRest.Headers.ZOTERO_WRITE_TOKEN;

import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

import zotero.apiimpl.rest.response.ResponseBuilder;

public abstract class ContentBuilder<T,B extends BaseBuilder<T,B,R>,R extends ResponseBuilder<T>> extends BaseBuilder<T, B, R>
{
	private Object jsonObject;

	@SuppressWarnings("unchecked")
	public B jsonObject(Object jsonObject)
	{
		this.jsonObject = jsonObject;
		return (B) this;
	}

	public StringEntity serializeJson()
	{
		String json = new Gson().toJson(jsonObject);
		return new StringEntity(json, ContentType.APPLICATION_JSON);
	}

	public Object getJsonObject()
	{
		return jsonObject;
	}
	
	public B createWriteToken()
	{
		return this.header(ZOTERO_WRITE_TOKEN, UUID.randomUUID().toString().replace("-", ""));
	}
}