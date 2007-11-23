package examples.silverlight.service;

import org.seasar.extension.unit.S2TestCase;

import examples.silverlight.entity.Emp;

public class EmployeeServiceTest extends S2TestCase
{
	public EmployeeService service;

	public void testFindAll()
	{
		Emp[] empList = service.findAll();
		assertEquals(14, empList.length);
	}

	public void testFind()
	{
		Emp emp = service.find(7839);
		assertEquals("KING", emp.ename);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		include("app.dicon");
	}
}
