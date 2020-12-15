package zotero.api.internal.rest;

public interface RestPutRequest extends RestRequest<Boolean>
{
	RestResponse<Boolean> post();
}