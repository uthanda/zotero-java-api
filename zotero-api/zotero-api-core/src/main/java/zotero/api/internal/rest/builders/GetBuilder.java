package zotero.api.internal.rest.builders;

import zotero.api.internal.rest.RestGetRequest;

public interface GetBuilder<T> extends Builder<GetBuilder<T>>
{
	GetBuilder<T> lastVersion(Integer lastVersion);

	GetBuilder<T> applyCurrent(RestGetRequest<T> current);

	GetBuilder<T> queryParam(String param, String value);

	RestGetRequest<T> build();
	
	GetBuilder<T> specialUrl(String url);
}