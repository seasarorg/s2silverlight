package examples.silverlight.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class Emp
{
	@Id
	@GeneratedValue
	public Integer empno;

	public String ename;

	public String job;

	public Integer mgr;

	@Temporal(TemporalType.DATE)
	public Date hiredate;

	public BigDecimal sal;

	public BigDecimal comm;

	public Integer deptno;

	@Version
	public Integer versionno;

	@ManyToOne
	@JoinColumn(name = "deptno")
	public Dept dept;

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
