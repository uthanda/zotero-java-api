package zotero.api.batch;

/**
 * Indicators for the result of an item commit in a batch.
 * 
 * @author Michael Oland
 * @since 1.0
 */
public enum BatchResult
{
	/**
	 * The item was successfully committed. 
	 */
	SUCCESS,
	/**
	 * The item failed to commit because of a remote API error 
	 */
	FAILED,
	/**
	 * The item was reported as unchanged by the remote API
	 */
	UNCHANGED,
	/**
	 * The item has not yet been evaluated and executed 
	 */
	UNEXECUTED,
	/**
	 * The item was rejected as invalid by the local API evaluation criteria 
	 */
	INVALID;
}
