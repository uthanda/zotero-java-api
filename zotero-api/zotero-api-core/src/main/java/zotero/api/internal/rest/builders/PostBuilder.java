package zotero.api.internal.rest.builders;

import zotero.api.internal.rest.RestPostRequest;

public interface PostBuilder extends Builder<PostBuilder>
{
	PostBuilder content(Object content);

	RestPostRequest build();
}