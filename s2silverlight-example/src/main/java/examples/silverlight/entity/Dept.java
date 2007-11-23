package examples.silverlight.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
public class Dept
{
	@Id
	@GeneratedValue
	public Integer deptno;

	public String dname;

	public String loc;

	@Version
	public Integer versionno;

	public Integer active;

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
