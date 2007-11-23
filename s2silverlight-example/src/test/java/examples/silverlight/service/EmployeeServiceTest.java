package examples.silverlight.service;

import java.util.List;

import org.seasar.extension.unit.S2TestCase;

import examples.silverlight.entity.Emp;

public class EmployeeServiceTest extends S2TestCase
{
	public EmployeeService service;

	public void testFindAll()
	{
		List<Emp> empList = service.findAll();
		assertEquals(14, empList.size());
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
