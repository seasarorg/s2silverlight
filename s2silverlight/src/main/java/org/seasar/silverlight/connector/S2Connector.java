package org.seasar.silverlight.connector;

import static org.seasar.silverlight.ErrorCode.ILLEGAL_URL;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.java2wsdl.bytecode.ParamReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.JSONSerializer;
import org.seasar.silverlight.MessageUtil;
import org.seasar.silverlight.util.RequestUtil;

public class S2Connector implements Connector
{
	public void doJSON(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		String servicePath = getServicePath(request);

		String[] pathElems = StringUtils.split(servicePath, "/");
		if (pathElems.length != 2)
		{
			String message = MessageUtil.getMessage(ILLEGAL_URL, servicePath);
			throw new ServletException(message);
		}

		String componentName = pathElems[0];
		String methodName = pathElems[1];

		Object obj;
		try
		{
			obj = getComponent(componentName);
		}
		catch (RuntimeException ex)
		{
			throw new ServletException(ex);
		}

		// SeasarはRuntimeExceptionで統一しているので、
		// この周辺実装はそれに合わせるべき。

		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(obj.getClass());
		Method[] methods = beanDesc.getMethods(methodName);

		String requestValue = RequestUtil.getBody(request);
		Object[] args = createArgs(requestValue, obj.getClass(), methods);

		Object target = null;
		try
		{
			target = beanDesc.invoke(obj, methodName, args);
		}
		catch (Exception e)
		{
			throw new ServletException(
					"An error occurred while invoking method. "
							+ e.getMessage(), e);
		}

		Map<String, Object> returnValue = new HashMap<String, Object>();
		returnValue.put("return", target);
		String result = JSONSerializer.serialize(returnValue);

		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.getOutputStream().write(result.getBytes());
	}

	protected Object[] createArgs(String body, Class<?> clazz, Method[] methods)
			throws IOException
	{
		Object jsonObj = null;
		try
		{
			jsonObj = JSON.decode(body);
		}
		catch (JSONParseException e)
		{
			// TODO: Exception
			throw new IOException();
		}

		if (jsonObj instanceof Map == false)
		{
			// TODO: Exception
			throw new IOException();
		}

		Map<?, ?> requestMap = (Map<?, ?>) jsonObj;

		for (Method method : methods)
		{
			Object[] args = doCreateArgs(requestMap, clazz, method);

			if (args != null)
			{
				return args;
			}
		}

		// TODO: Exception
		throw new IOException();
	}

	protected Object[] doCreateArgs(Map<?, ?> requestMap, Class<?> clazz,
			Method method) throws IOException
	{
		Map<String, Integer> paramMap = getParamMap(clazz, method);
		Class<?>[] paramTypes = method.getParameterTypes();

		Object[] args = new Object[requestMap.size()];
		for (Entry<?, ?> entry : requestMap.entrySet())
		{
			if (paramMap.containsKey(entry.getKey()) == false)
			{
				return null;
			}

			Integer argNum = paramMap.get(entry.getKey());
			Object reqestParam = entry.getValue();

			Class<?> requestClass = reqestParam.getClass();
			Class<?> paramClass = paramTypes[argNum.intValue()];

			if (paramClass.isAssignableFrom(requestClass) == false)
			{
				return null;
			}
			else
			{
				args[argNum.intValue()] = reqestParam;
			}
		}

		return args;
	}

	protected Map<String, Integer> getParamMap(Class<?> clazz, Method method)
			throws IOException
	{
		ParamReader reader = new ParamReader(clazz);
		String[] paramNames = reader.getParameterNames(method);

		Map<String, Integer> paramMap = null;
		if (paramNames != null)
		{
			paramMap = new HashMap<String, Integer>(paramNames.length);
			for (int index = 0; index < paramNames.length; index++)
			{
				paramMap.put(paramNames[index], Integer.valueOf(index));
			}
		}
		else
		{
			paramMap = new HashMap<String, Integer>(0);
		}

		reader.close();

		return paramMap;
	}

	protected String getServicePath(HttpServletRequest request)
	{
		String uri = request.getRequestURI();
		String path = request.getServletPath();

		String servicePath = uri.substring(uri.indexOf(path) + path.length());

		return servicePath;
	}

	protected Object getComponent(String componentName)
	{
		// TODO: returns accessible component only
		return SingletonS2Container.getComponent(componentName);
	}
}
