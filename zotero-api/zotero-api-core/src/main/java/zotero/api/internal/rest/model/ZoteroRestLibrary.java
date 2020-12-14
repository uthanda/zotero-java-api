package zotero.api.internal.rest.model;

import java.math.BigDecimal;

public class ZoteroRestLibrary
{
	private ZoteroRestType type = null;

	private BigDecimal id = null;

	private String name = null;

	private ZoteroRestLinks links = null;

	public ZoteroRestType getType()
	{
		return type;
	}

	public void setType(ZoteroRestType type)
	{
		this.type = type;
	}

	public BigDecimal getId()
	{
		return id;
	}

	public void setId(BigDecimal id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ZoteroRestLinks getLinks()
	{
		return links;
	}

	public void setLinks(ZoteroRestLinks links)
	{
		this.links = links;
	}
}
