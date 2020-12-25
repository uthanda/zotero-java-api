package zotero.api.internal.rest.builders;

import zotero.api.internal.rest.RestPatchRequest;

public interface PatchBuilder extends Builder<PatchBuilder>
{
	PatchBuilder content(Object content);

	PatchBuilder itemKey(String key);

	RestPatchRequest build();

	PatchBuilder versionNumber(Integer versionNumber);
}