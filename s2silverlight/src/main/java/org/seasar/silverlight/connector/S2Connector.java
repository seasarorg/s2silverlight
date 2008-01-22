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
import org.seasar.silverlight.exception.InternalRuntimeException;
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
			throw new InternalRuntimeException(ILLEGAL_URL, servicePath);
		}

		String componentName = pathElems[0];
		String methodName = pathElems[1];

		Object obj = getComponent(componentName);

		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(obj.getClass());
		Method[] methods = beanDesc.getMethods(methodName);

		String requestValue = RequestUtil.getBody(request);
		Object[] args = createArgs(requestValue, obj.getClass(), methods);

		Object target = beanDesc.invoke(obj, methodName, args);

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
		catch (JSONParseException ex)
		{
			throw new InternalRuntimeException(ex);
		}

		if (jsonObj instanceof Map == false)
		{
			throw new InternalRuntimeException();
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

		throw new InternalRuntimeException();
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
