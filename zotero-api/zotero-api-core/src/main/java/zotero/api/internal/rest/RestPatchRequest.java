package zotero.api.internal.rest;

public interface RestPatchRequest extends RestRequest<Boolean>
{
	RestResponse<Boolean> patch();
}