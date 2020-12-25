package zotero.apiimpl.rest.model;

import zotero.apiimpl.rest.model.ZoteroRestData.DataBuilder;

public class ZoteroRestItem
{
	public static class ItemBuilder
	{
		private boolean delta;
		private String key;
		private Integer version;
		private ZoteroRestData data;

		public ItemBuilder(boolean delta)
		{
			this.delta = delta;
		}

		public ItemBuilder key(String key)
		{
			this.key = key;
			return this;
		}
		
		public ItemBuilder version(Integer version)
		{
			this.version = version;
			return this;
		}
		
		public ItemBuilder data(ZoteroRestData data)
		{
			this.data = data;
			return this;
		}
		
		public DataBuilder dataBuilder()
		{
			this.data = new ZoteroRestData();
			
			return new ZoteroRestData.DataBuilder(delta, this.data);
		}
		
		public ZoteroRestItem build()
		{
			ZoteroRestItem item = new ZoteroRestItem();
			item.key = key;
			item.version = version;
			item.data = data;
			
			return item;
		}
	}

	private String key = null;

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
