package orm.integ.test.entity;

import orm.integ.eao.model.Entity;
import orm.integ.eao.model.EntityAnno;

@EntityAnno(classId="scls", table="tb_school_class")
public class SchoolClass extends Entity {

	private int grade;  // 年级

	public int getGrade() { 
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	
}
