package zotero.apiimpl.rest.response;

import java.io.IOException;

import org.apache.http.HttpEntity;

public class SuccessResponseBuilder extends ResponseBuilder<Void>
{
	@Override
	public ResponseBuilder<Void> entity(HttpEntity entity) throws UnsupportedOperationException, IOException
	{
		return this;
	}

	@Override
	public void noEntity()
	{
	}
}