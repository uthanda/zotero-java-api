package zotero.apiimpl.rest.response;

import java.io.IOException;

import org.apache.http.HttpEntity;

public class SuccessResponseBuilder extends ResponseBuilder<Boolean>
{
	@Override
	public ResponseBuilder<Boolean> entity(HttpEntity entity) throws UnsupportedOperationException, IOException
	{
		response.response = Boolean.TRUE;
		return this;
	}

	@Override
	public void noEntity()
	{
		response.response = Boolean.TRUE;
	}
}