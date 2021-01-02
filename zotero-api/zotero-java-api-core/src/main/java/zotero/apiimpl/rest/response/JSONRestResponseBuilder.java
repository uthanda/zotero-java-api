package zotero.apiimpl.rest.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class JSONRestResponseBuilder<T> extends ResponseBuilder<T>
{
	private static Logger logger = LogManager.getLogger(JSONRestResponseBuilder.class);

	private Class<T> type;

	public JSONRestResponseBuilder(Class<T> type)
	{
		this.type = type;

		logger.debug("Creating JSONRestResponseBuilder<{}>", type.getCanonicalName());
	}

	@Override
	public ResponseBuilder<T> entity(HttpEntity entity) throws UnsupportedOperationException, IOException
	{
		InputStream is = entity.getContent();

		if (logger.isDebugEnabled())
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOUtils.copy(is, bos);

			String json = new String(bos.toByteArray());

			logger.debug("Read JSON {}", json);

			response.response = new Gson().fromJson(json, type);
			
			is.close();
		}
		else
		{
			InputStreamReader reader = new InputStreamReader(is);

			response.response = new Gson().fromJson(reader, type);
			
			reader.close();
		}

		return this;
	}

	@Override
	public void noEntity()
	{
		response.errorMessage = "No content";
	}
}