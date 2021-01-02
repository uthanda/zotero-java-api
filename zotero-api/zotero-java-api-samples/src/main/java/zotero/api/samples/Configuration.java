package zotero.api.samples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import zotero.api.ZoteroAPIKey;

public class Configuration
{
	private ZoteroAPIKey apiKey;
	private String userId;

	public ZoteroAPIKey getApiKey()
	{
		return apiKey;
	}

	public String getUserId()
	{
		return userId;
	}

	public static Configuration load(String file) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		
		Properties props = new Properties();
		
		props.load(fis);
		
		fis.close();
		
		Configuration config = new Configuration();
		
		config.apiKey = new ZoteroAPIKey(props.getProperty("apiKey"));
		config.userId = props.getProperty("userId");
		
		return config;
	}
}
