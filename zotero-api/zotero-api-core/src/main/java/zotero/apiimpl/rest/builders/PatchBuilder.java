package zotero.apiimpl.rest.builders;

import zotero.apiimpl.rest.RestPatchRequest;

public interface PatchBuilder extends Builder<PatchBuilder>
{
	PatchBuilder content(Object content);

	PatchBuilder itemKey(String key);

	RestPatchRequest build();

	PatchBuilder versionNumber(Integer versionNumber);
}