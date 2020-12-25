package zotero.api.constants;

/**
 *
 * @author stran
 */
public final class ZoteroExceptionCodes
{
	private ZoteroExceptionCodes() {}
	
	/**
	 *
	 * @author stran
	 */
	public static final class Data
	{
		private Data() {}
		
		/**
		 * Indicates that an operation was attempted on an item that has been deleted.
		 */
		public static final int OBJECT_DELETED = 0x0;
	}
}
