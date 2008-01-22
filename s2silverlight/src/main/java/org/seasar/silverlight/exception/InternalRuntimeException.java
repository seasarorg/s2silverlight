package org.seasar.silverlight.exception;

import org.seasar.silverlight.MessageUtil;

public class InternalRuntimeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InternalRuntimeException()
	{
		super();
	}

	public InternalRuntimeException(String messageId, Object... args)
	{
		super(MessageUtil.getMessage(messageId, args));
	}

	public InternalRuntimeException(String messageId, Throwable cause,
			Object... args)
	{
		super(MessageUtil.getMessage(messageId, args), cause);
	}

	public InternalRuntimeException(Throwable cause)
	{
		super(cause);
	}
}
