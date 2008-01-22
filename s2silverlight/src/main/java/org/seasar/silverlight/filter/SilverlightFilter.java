package org.seasar.silverlight.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.silverlight.connector.Connector;

public class SilverlightFilter implements Filter
{
	private Connector connector;

	public void init(FilterConfig config) throws ServletException
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		if ("application/json".equals(request.getContentType()) == false)
		{
			chain.doFilter(request, response);
		}

		connector.doJSON((HttpServletRequest) request,
				(HttpServletResponse) response, chain);
	}

	public void destroy()
	{
		// Do Nothings.
	}
}
