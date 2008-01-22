package org.seasar.silverlight.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil
{
	/**
	 * リクエストボディを取得します。
	 * 
	 * @param request 処理するリクエスト
	 * @return リクエストボディ
	 * @throws IOException
	 */
	public static String getBody(HttpServletRequest request) throws IOException
	{
		BufferedReader reader = request.getReader();

		try
		{
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null)
			{
				sb.append(str);
			}
			return new String(sb);
		}
		finally
		{
			reader.close();
		}
	}
}
