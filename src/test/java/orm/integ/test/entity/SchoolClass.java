package orm.integ.test.entity;

import orm.integ.dao.annotation.Table;
import orm.integ.eao.model.Entity;

@Table(name="tb_school_class")
public class SchoolClass extends Entity {

	private String name;
	
	private int grade;  // 年级

	public int getGrade() { 
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
