package zotero.api.internal.rest.builders;

import zotero.api.ZoteroAPIKey;
import zotero.api.internal.rest.RestRequest;

public interface Builder<R>
{
	R id(String id);

	R setUsers();

	R setGroups();

	R apiKey(ZoteroAPIKey key);

	R url(String url);

	R urlParam(String param, String value);

	R type(Class<?> type);

	void apply(RestRequest<?> request);

	R applyCurrent(RestRequest<?> current);
}