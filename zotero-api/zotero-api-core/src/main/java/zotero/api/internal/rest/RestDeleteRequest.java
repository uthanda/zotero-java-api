package zotero.api.internal.rest;

public interface RestDeleteRequest extends RestRequest<Boolean>
{
	RestResponse<Boolean> delete();
}
