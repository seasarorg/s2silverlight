package org.seasar.silverlight.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockHttpServletResponse;
import org.seasar.silverlight.mock.MockService;

public class S2ConnectorTest extends S2TestCase
{
	private S2Connector connector = new S2Connector();

	public void testGetServicePath() throws Exception
	{
		String result = connector.getServicePath(request);
		assertEquals("/mockService/testMethod", result);
	}

	public void testGetServicePath_Root() throws Exception
	{
		HttpServletRequest request = new MockHttpServletRequestImpl(
				getServletContext(), "/s2sl");

		String result = connector.getServicePath(request);
		assertEquals("", result);
	}

	public void testDoJson() throws Exception
	{
		register(MockService.class);

		MockHttpServletResponse responce = getResponse();
		connector.doJSON(request, responce, null);

		String result = new String(responce.getResponseBytes());
		assertEquals("{return:\"aaa\"}", result);
	}

	private HttpServletRequest request = new MockHttpServletRequestImpl(
			getServletContext(), "/s2sl")
	{
		@Override
		public String getRequestURI()
		{
			return "/s2sl/mockService/testMethod";
		}

		@Override
		public BufferedReader getReader() throws IOException
		{
			String input = "{str:\"aaa\"}";
			return new BufferedReader(new StringReader(input));
		}
	};

}
