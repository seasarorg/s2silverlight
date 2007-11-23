package examples.silverlight.service;

import org.seasar.extension.jdbc.JdbcManager;

import examples.silverlight.entity.Emp;

public class EmployeeService
{
	public JdbcManager jdbcManager;

	public Emp[] findAll()
	{
		return jdbcManager.from(Emp.class).getResultList().toArray(new Emp[0]);
	}

	public Emp find(int empno)
	{
		return jdbcManager.from(Emp.class).where("empno = ?", empno)
				.getSingleResult();
	}
}
