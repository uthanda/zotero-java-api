package zotero.api.internal.rest.builders;

import zotero.api.internal.rest.RestDeleteRequest;

public interface DeleteBuilder extends Builder<DeleteBuilder>
{
	DeleteBuilder lastVersion(Integer lastVersion);

	RestDeleteRequest build();

	DeleteBuilder queryParam(String param, String value);

	DeleteBuilder itemKey(String key);
}