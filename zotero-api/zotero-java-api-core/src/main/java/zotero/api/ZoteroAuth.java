package zotero.api;

import org.apache.http.HttpRequest;

public interface ZoteroAuth
{
	void applyHeaders(HttpRequest request);
}
