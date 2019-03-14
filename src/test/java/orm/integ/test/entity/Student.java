package orm.integ.test.entity;

import java.util.Date;

import orm.integ.eao.annotation.Table;
import orm.integ.eao.annotation.ForeignKey;
import orm.integ.eao.model.Entity;

@Table( name="tb_student")
public class Student extends Entity {
	
	private String name;

	private Integer sex;
	
	private Date birthday;
	
	@ForeignKey(masterClass=SchoolClass.class)
	private int schoolClassId;
	
	private String className;
	
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
