package zotero.apiimpl;

import java.util.Date;

import zotero.api.Item;
import zotero.api.Library;
import zotero.api.collections.Collections;
import zotero.api.collections.Links;
import zotero.api.collections.Relationships;
import zotero.api.collections.Tags;
import zotero.api.constants.ItemType;
import zotero.api.properties.Properties;

public class PlaceholderItem implements Item
{
	private static final String ERROR_MESSAGE = "This is a placeholder item. Execute batch to actualize";

	private String key;

	public PlaceholderItem(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public Integer getVersion()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Library getLibrary()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public void refresh()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public void save()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public void delete()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Properties getProperties()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Links getLinks()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public ItemType getItemType()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Date getAccessDate()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Tags getTags()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Collections getCollections()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

	@Override
	public Relationships getRelationships()
	{
		throw new java.lang.UnsupportedOperationException(ERROR_MESSAGE);
	}

}
