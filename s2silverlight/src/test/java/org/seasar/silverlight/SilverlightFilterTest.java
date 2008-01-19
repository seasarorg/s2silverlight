package org.seasar.silverlight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockHttpServletResponse;
import org.seasar.silverlight.mock.MockService;

public class SilverlightFilterTest extends S2TestCase
{
	private SilverlightFilter filter = new SilverlightFilter();

	public void testDoJson() throws Throwable
	{
		register(MockService.class);

		HttpServletRequest request = new MockHttpServletRequestImpl(
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

		MockHttpServletResponse responce = getResponse();
		filter.doJSON(request, responce);

		String result = new String(responce.getResponseBytes());
		assertEquals("{return:\"aaa\"}", result);
	}

	@Override
	protected void setUp() throws Exception
	{
	}
}
