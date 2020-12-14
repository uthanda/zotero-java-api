package zotero.api.internal.rest.model;

import com.google.gson.annotations.SerializedName;

public enum ZoteroRestType
{
	@SerializedName("user")
	USER("user");

	private String value;

	ZoteroRestType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
}