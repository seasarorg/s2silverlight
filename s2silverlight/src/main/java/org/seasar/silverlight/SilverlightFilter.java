package org.seasar.silverlight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONParseException;
import net.sf.json.JSONObject;

import org.apache.ws.java2wsdl.bytecode.ParamReader;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.util.JSONSerializer;

public class SilverlightFilter implements Filter
{
	public void init(FilterConfig config) throws ServletException
	{
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		if ("application/json".equals(request.getContentType()))
		{
			doJSON((HttpServletRequest) request, (HttpServletResponse) response);
			return;
		}

		chain.doFilter(request, response);
	}

	public void doJSON(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		String uri = request.getRequestURI();
		String path = request.getServletPath();

		String servicePath = uri.substring(uri.indexOf(path) + path.length()
				+ 1);

		if (servicePath.indexOf("/") < 0)
		{
			return;
		}

		// パスの解析
		String componentName = servicePath.substring(0, servicePath
				.indexOf("/"));
		String methodName = servicePath.substring(componentName.length() + 1);

		// コンポーネントの取得
		ComponentDef def = getComponentDefNoException(componentName);
		if (def == null)
		{
			throw new ServletException("JSON Component Name[" + componentName
					+ "] is not found.");
		}

		// コンポーネントの取得
		Object obj = def.getComponent();
		if (obj == null)
		{
			throw new ServletException("JSON Component Name[" + componentName
					+ "] not found.");
		}

		// メソッドの確認
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(def.getConcreteClass());
		if (!beanDesc.hasMethod(methodName))
		{
			throw new ServletException("JSON Component Name[" + componentName
					+ "] does not has method[" + methodName + "]");
		}

		// メソッドの取得
		Method[] methods = beanDesc.getMethods(methodName);
		if (methods.length != 1)
		{
			throw new ServletException("JSON Component Name[" + componentName
					+ "] has two ore more methods[" + methodName + "]");
		}
		Method method = methods[0];

		Object[] args = createArgs(request, obj.getClass(), method);

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

	protected Object[] createArgs(HttpServletRequest request, Class clazz,
			Method method) throws IOException
	{
		// 引数一覧の取得
		ParamReader reader = new ParamReader(clazz);
		String[] paramNames = reader.getParameterNames(method);
//		Class[] paramTypes = method.getParameterTypes();

		if (paramNames == null || paramNames.length == 0)
		{
			return null;
		}

		// 引数の作成
		String requestValue = getBody(request.getReader());

		Map requstValues = null;
		try
		{
			requstValues = (Map) JSON.decode(requestValue);
		}
		catch (JSONParseException e)
		{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		Object[] args = new Object[paramNames.length];
		for (int index = 0; index < paramNames.length; index++)
		{
			Object param = requstValues.get(paramNames[index]);

			if (param instanceof JSONObject)
			{
//				args[index] = JSONObject.toBean(input
//						.getJSONObject(paramNames[index]), paramTypes[index]);
			}
			else
			{
				args[index] = param;
			}
		}
		return args;
	}

	public void destroy()
	{
		// Do Nothings.
	}

	protected ComponentDef getComponentDefNoException(String componentName)
	{
		S2Container container = SingletonS2ContainerFactory.getContainer();

		if (!container.hasComponentDef(componentName))
		{
			return null;
		}
		return container.getComponentDef(componentName);
	}

	protected String getBody(BufferedReader reader) throws IOException
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null)
			{
				sb.append(str);
			}
			return sb.toString();
		}
		finally
		{
			reader.close();
		}
	}
}
