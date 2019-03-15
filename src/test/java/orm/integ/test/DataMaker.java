package orm.integ.test;

import java.util.Random;

import orm.integ.eao.EntityAccessObject;
import orm.integ.test.entity.SchoolClass;
import orm.integ.test.entity.SchoolClassService;
import orm.integ.test.entity.Student;
import orm.integ.test.entity.StudentService;
import orm.integ.utils.IdGenerator;

public class DataMaker {

	public static void main(String[] args) {
		
	}
	
	
	static void createStudents(int classCount, int studentCountPerClass) {
		
		createSchoolClasses(classCount);
		
		EntityAccessObject<Student> eao = new StudentService().getEao();
		
		eao.delete("", null, false);
		
		TimeMonitor tm = new TimeMonitor("createStudents");
		Student stu;
		String name;
		Random rand = new Random();
		int count = studentCountPerClass*classCount;
		for (int i=0; i<count; i++) {
			stu = new Student();
			name = IdGenerator.createRandomStr(12, false);
			stu.setName(name);
			stu.setSex(rand.nextInt(2));
			stu.setSchoolClassId(rand.nextInt(classCount)+1);
			eao.insert(stu);
		}
		tm.finish("create "+count+" students");
	}
	
	static void createSchoolClasses(int count) {
		
		EntityAccessObject<SchoolClass> eao = new SchoolClassService().getEao();

		eao.delete("", null, false);
		
		SchoolClass sc;
		double classNumPerGrade = count/6.0;
		int grade;
		for (int i=1; i<=count; i++) {
			sc = new SchoolClass();
			sc.setId(i);
			sc.setName("class "+i);
			grade = new Double((i-1)/classNumPerGrade).intValue()+1;
			System.out.println("i="+i+", grade="+grade);
			sc.setGrade(grade);
			eao.insert(sc);
		}
		
	}
	
}
