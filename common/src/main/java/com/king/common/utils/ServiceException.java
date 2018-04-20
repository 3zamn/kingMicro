package com.king.common.utils;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public class ServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ServiceException()
	{
		super();
	}

	public ServiceException(String message)
	{
		super(message);
	}

	public ServiceException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ServiceException(Throwable cause)
	{
		super(cause);
	}
}
