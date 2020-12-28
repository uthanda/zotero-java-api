package zotero.api;

import zotero.api.constants.LinkType;

/**
 * @author stran
 *
 */
public interface Link extends PropertiesItem
{
	String getHref();

	LinkType getType();
}