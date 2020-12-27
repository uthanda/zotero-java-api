package zotero.apiimpl.rest.response;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

public class StreamResponseBuilder extends ResponseBuilder<InputStream>
{
	@Override
	public ResponseBuilder<InputStream> entity(HttpEntity entity) throws UnsupportedOperationException, IOException
	{
		response.response = entity.getContent();
		return this;
	}

	@Override
	public void noEntity()
	{
		response.errorMessage = "No content";
	}
}