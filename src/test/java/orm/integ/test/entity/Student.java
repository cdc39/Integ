package orm.integ.test.entity;

import java.util.Date;

import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityAnno;
import orm.integ.eao.model.ForeignKey;

@EntityAnno(classId=Entities.STUDENT, table="tb_student", schema="")
public class Student extends Entity {

	private Integer sex;
	
	private Date birthday;
	
	@ForeignKey(masterClass=SchoolClass.class)
	private int schoolClassId;
	
	private String className;
	
	private Date createTime;
	
	private int age;

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getSchoolClassId() {
		return schoolClassId;
	}

	public void setSchoolClassId(int schoolClassId) {
		this.schoolClassId = schoolClassId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
