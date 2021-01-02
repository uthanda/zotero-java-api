package zotero.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;

public final class PassThruInputStream extends InputStream
{
	private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private final InputStream is;
	private Consumer<byte[]> test;

	public PassThruInputStream(HttpPost post, Consumer<byte[]> test)
	{
		this(post.getEntity(), test);
	}

	public PassThruInputStream(HttpEntity entity, Consumer<byte[]> test)
	{
		try
		{
			this.is = entity.getContent();
			this.test = test;
		}
		catch (UnsupportedOperationException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public PassThruInputStream(HttpPatch patch, Consumer<byte[]> test)
	{
		this(patch.getEntity(), test);
	}

	@Override
	public int read() throws IOException
	{
		int b = is.read();
		
		if(b == -1) {
			return b;
		}
		
		bos.write(b);
		return b;
	}

//	@Override
//	public int read(byte[] b) throws IOException
//	{
//		int length = super.read(b);
//
//		if (length > -1)
//		{
//			bos.write(b, 0, length);
//		}
//
//		return length;
//	}
//
	@Override
	public void close() throws IOException
	{
		super.close();
		
		if(test == null) {
			return;
		}
		
		test.accept(bos.toByteArray());
	}
}