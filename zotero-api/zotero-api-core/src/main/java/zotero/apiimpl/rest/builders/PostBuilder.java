package zotero.apiimpl.rest.builders;

import zotero.apiimpl.rest.RestPostRequest;

public interface PostBuilder extends Builder<PostBuilder>
{
	PostBuilder content(Object content);

	RestPostRequest build();
}