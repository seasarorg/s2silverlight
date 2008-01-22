package org.seasar.silverlight;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MessageUtil
{
	private static final String BUNDLE = "org.seasar.silverlight.Messages";

	public static String getMessage(String key, Object... args)
	{
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE);
		String pattern = bundle.getString(key);

		String message = MessageFormat.format(pattern, args);
		return message;
	}
}
