package zotero.api.internal.rest.model;

import java.math.BigDecimal;

public class ZoteroRestTag
{
	private String tag = null;
	private BigDecimal type = null;

	public String getTag()
	{
		return tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public BigDecimal getType()
	{
		return type;
	}

	public void setType(BigDecimal type)
	{
		this.type = type;
	}
}
