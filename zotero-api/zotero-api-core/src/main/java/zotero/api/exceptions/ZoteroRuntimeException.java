package zotero.api.exceptions;

import zotero.api.constants.ZoteroExceptionType;

public class ZoteroRuntimeException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6171812790710528833L;

	private final ZoteroExceptionType type;
	private final int exceptionCode;

	public ZoteroRuntimeException(ZoteroExceptionType type, int exceptionCode, String message)
	{
		super(message);
		this.type = type;
		this.exceptionCode = exceptionCode;
	}

	public ZoteroExceptionType getType()
	{
		return type;
	}

	public int getExceptionCode()
	{
		return exceptionCode;
	}
}
