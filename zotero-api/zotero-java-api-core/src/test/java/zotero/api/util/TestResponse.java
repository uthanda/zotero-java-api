package zotero.api.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.params.HttpParams;

@SuppressWarnings("deprecation")
public class TestResponse implements CloseableHttpResponse
{
	private final int statusCode;
	private final HttpEntity entity;
	private Map<String, String> headers = new HashMap<>();

	public TestResponse(int statusCode, HttpEntity entity)
	{
		this.statusCode = statusCode;
		this.entity = entity;
	}

	@Override
	public StatusLine getStatusLine()
	{
		return new StatusLine()
		{

			@Override
			public ProtocolVersion getProtocolVersion()
			{
				return null;
			}

			@Override
			public int getStatusCode()
			{
				return statusCode;
			}

			@Override
			public String getReasonPhrase()
			{
				return null;
			}
		};
	}

	@Override
	public void setStatusLine(StatusLine statusline)
	{
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code)
	{
	}

	@Override
	public void setStatusLine(ProtocolVersion ver, int code, String reason)
	{
	}

	@Override
	public void setStatusCode(int code) throws IllegalStateException
	{
	}

	@Override
	public void setReasonPhrase(String reason) throws IllegalStateException
	{
	}

	@Override
	public HttpEntity getEntity()
	{
		return entity;
	}

	@Override
	public void setEntity(HttpEntity entity)
	{
	}

	@Override
	public Locale getLocale()
	{
		return null;
	}

	@Override
	public void setLocale(Locale loc)
	{
	}

	@Override
	public ProtocolVersion getProtocolVersion()
	{
		return null;
	}

	@Override
	public boolean containsHeader(String name)
	{
		return headers.containsKey(name);
	}

	@Override
	public Header[] getHeaders(String name)
	{
		return null;
	}

	@Override
	public Header getFirstHeader(String name)
	{
		return new Header()
		{

			@Override
			public String getName()
			{
				return name;
			}

			@Override
			public String getValue()
			{
				return headers.get(name);
			}

			@Override
			public HeaderElement[] getElements() throws ParseException
			{
				return null;
			}
		};
	}

	@Override
	public Header getLastHeader(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Header[] getAllHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addHeader(Header header)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String name, String value)
	{
		this.headers.put(name, value);
	}

	@Override
	public void setHeader(Header header)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeader(String name, String value)
	{
		this.headers.put(name, value);
	}

	@Override
	public void setHeaders(Header[] headers)
	{
	}

	@Override
	public void removeHeader(Header header)
	{
	}

	@Override
	public void removeHeaders(String name)
	{
	}

	@Override
	public HeaderIterator headerIterator()
	{
		return null;
	}

	@Override
	public HeaderIterator headerIterator(String name)
	{
		return null;
	}

	@Override
	public HttpParams getParams()
	{
		return null;
	}

	@Override
	public void setParams(HttpParams params)
	{
	}

	@Override
	public void close() throws IOException
	{
	}
}
