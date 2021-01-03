package zotero.api.constants;

/**
 *
 * @author stran
 */
public final class ZoteroExceptionCodes
{
	public static final class IO
	{
		private IO()
		{
		}

		public static final int READ_DATA = 0x0;
		public static final int MISSING_ALGORITM = 0x1;
		public static final int API_ERROR = 0x2;
		public static final int IO_ERROR = 0x3;
	}

	public static final class Unknown
	{
		public static final int INTERNAL_ERROR = 0x0;

		private Unknown()
		{
		}
	}

	private ZoteroExceptionCodes()
	{
	}

	/**
	 *
	 * @author stran
	 */
	public static final class Data
	{
		private Data()
		{
		}

		/**
		 * Indicates that an operation was attempted on an item that has been
		 * deleted.
		 */
		public static final int OBJECT_DELETED = 0x0;
		public static final int UNSUPPORTED_ENUM_VALUE = 0x1;
		public static final int INVALID_ATTACHMENT_TYPE = 0x2;
		public static final int PROPERTY_READ_ONLY = 0x3;
		public static final int ATTACHMENT_PENDING = 0x4;
		public static final int ATTACHMENT_NO_CONTENT = 0x5;
		public static final int INVALID_TAG_RESPONSE = 0x6;
		public static final int ATTACHMENT_MISSING_PARAM = 0x7;
		public static final int INVALID_PROPERTY = 0x8;
		public static final int MIME_NOT_RESOLVED = 0x9;
		public static final int MD5_ERROR = 0xA;
	}
}
