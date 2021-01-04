package zotero.api;

public interface Note extends Item
{
	String getNoteContent();
	
	void setNoteContent(String content);
}
