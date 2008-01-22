package org.seasar.silverlight.connector;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Connector
{
	void doJSON(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws IOException, ServletException;
}
