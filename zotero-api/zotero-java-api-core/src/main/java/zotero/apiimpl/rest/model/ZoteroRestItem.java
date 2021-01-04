package zotero.apiimpl.rest.model;

public class ZoteroRestItem
{
	private String key = null;
	
	private String tag = null;

	private Integer version = null;

	private ZoteroRestLibrary library = null;

	private ZoteroRestLinks links = null;

	private ZoteroRestData data = null;

	private ZoteroRestMeta meta = null;

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public Integer getVersion()
	{
		return version;
	}

	public void setVersion(Integer version)
	{
		this.version = version;
	}

	public ZoteroRestLibrary getLibrary()
	{
		return library;
	}

	public void setLibrary(ZoteroRestLibrary library)
	{
		this.library = library;
	}

	public ZoteroRestLinks getLinks()
	{
		return links;
	}

	public void setLinks(ZoteroRestLinks links)
	{
		this.links = links;
	}

	public ZoteroRestData getData()
	{
		return data;
	}

	public void setData(ZoteroRestData data)
	{
		this.data = data;
	}

	public ZoteroRestMeta getMeta()
	{
		return meta;
	}

	public void setMeta(ZoteroRestMeta meta)
	{
		this.meta = meta;
	}
}
