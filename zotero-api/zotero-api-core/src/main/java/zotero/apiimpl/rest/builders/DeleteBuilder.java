package zotero.apiimpl.rest.builders;

import zotero.apiimpl.rest.RestDeleteRequest;

public interface DeleteBuilder extends Builder<DeleteBuilder>
{
	DeleteBuilder lastVersion(Integer lastVersion);

	RestDeleteRequest build();

	DeleteBuilder queryParam(String param, String value);

	DeleteBuilder itemKey(String key);
}