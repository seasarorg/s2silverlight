package examples.silverlight.service;

import java.util.List;

import org.seasar.extension.jdbc.JdbcManager;

import examples.silverlight.entity.Emp;

public class EmployeeService
{
	public JdbcManager jdbcManager;

	public List<Emp> findAll()
	{
		return jdbcManager.from(Emp.class).getResultList();
	}

	public Emp find(int empno)
	{
		return jdbcManager.from(Emp.class).where("empno = ?", empno)
				.getSingleResult();
	}
}
