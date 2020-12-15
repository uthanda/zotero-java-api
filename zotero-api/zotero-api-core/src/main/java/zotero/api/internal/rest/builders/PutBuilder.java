package zotero.api.internal.rest.builders;

import zotero.api.internal.rest.RestPutRequest;

public interface PutBuilder extends Builder<PutBuilder>
{
	PutBuilder content(Object content);

	RestPutRequest build();
}