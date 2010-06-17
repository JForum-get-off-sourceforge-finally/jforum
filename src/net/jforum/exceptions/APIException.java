/*
 * Created on 04/09/2006 21:32:40
 */
package net.jforum.exceptions;

/**
 * @author Rafael Steil
 * @version $Id: APIException.java,v 1.1 2006/09/05 00:53:32 rafaelsteil Exp $
 */
public class APIException extends RuntimeException
{
	private static final long serialVersionUID = 5069796445984449394L;

	public APIException(final String message)
	{
		super(message);
	}
}
