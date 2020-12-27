package zotero.apiimpl.rest.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;

import com.google.gson.Gson;

public class JSONRestResponseBuilder<T> extends ResponseBuilder<T>
{
	private Class<T> type;

	public JSONRestResponseBuilder(Class<T> type)
	{
		this.type = type;
	}

	@Override
	public ResponseBuilder<T> entity(HttpEntity entity) throws UnsupportedOperationException, IOException
	{
		InputStream is = entity.getContent();

		InputStreamReader reader = new InputStreamReader(is);

		response.response = new Gson().fromJson(reader, type);

		reader.close();

		return this;
	}

	@Override
	public void noEntity()
	{
		response.errorMessage = "No content";
	}
}