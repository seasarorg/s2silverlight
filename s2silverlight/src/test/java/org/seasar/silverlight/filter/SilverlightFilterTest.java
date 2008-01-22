package org.seasar.silverlight.filter;

import junit.framework.TestCase;

public class SilverlightFilterTest extends TestCase
{
	private SilverlightFilter filter = new SilverlightFilter();

	public void testDoFilter() throws Throwable
	{
		filter.doFilter(null, null, null);
	}

	@Override
	protected void setUp() throws Exception
	{
	}
}
