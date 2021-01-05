package zotero.apiimpl.rest.model;

public enum SerializationMode
{
	/**
	 * This mode will instruct the system to generate the whole object regardless of changes. 
	 */
	FULL,
	/**
	 * This mode will instruct the system to only serialize changes.
	 */
	PARTIAL
}
