package zotero.api;

import java.io.InputStream;
import java.util.Date;

import zotero.api.constants.AttachmentType;

public interface Attachment extends Item
{
	AttachmentType getType();
	
	String getTitle();
	
	void setTitle(String title);

	Date getAccessDate();
	
	String getContentType();
	
	void setContentType(String type);
	
	String getMD5();
	
	String getFilename();
	
	String getCharset();
	
	InputStream getContent();
}

/**
 * {
    "itemType": "attachment",
    "linkMode": "linked_file",
    "title": "",
    "accessDate": "",
    "note": "",
    "tags": [],
    "collections": [],
    "relations": {},
    "contentType": "",
    "charset": "",
    "path": ""
    }
{
    "itemType": "attachment",
    "linkMode": "imported_file",
    "title": "",
    "accessDate": "",
    "note": "",
    "tags": [],
    "collections": [],
    "relations": {},
    "contentType": "",
    "charset": "",
    "filename": "",
    "md5": null,
    "mtime": null
}{
    "itemType": "attachment",
    "linkMode": "imported_url",
    "title": "",
    "accessDate": "",
    "url": "",
    "note": "",
    "tags": [],
    "collections": [],
    "relations": {},
    "contentType": "",
    "charset": "",
    "filename": "",
    "md5": null,
    "mtime": null
}{
    "itemType": "attachment",
    "linkMode": "linked_url",
    "title": "",
    "accessDate": "",
    "url": "",
    "note": "",
    "tags": [],
    "collections": [],
    "relations": {},
    "contentType": "",
    "charset": ""
}
 */
