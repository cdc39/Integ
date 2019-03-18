package orm.integ.test;


import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import orm.integ.dao.sql.SqlQuery;
import orm.integ.dao.sql.TabQuery;
import orm.integ.eao.EntityAccessObject;
import orm.integ.eao.model.PageData;
import orm.integ.eao.model.Record;
import orm.integ.test.entity.SchoolClass;
import orm.integ.test.entity.SchoolClassService;
import orm.integ.test.entity.Student;
import orm.integ.test.entity.StudentService;
import orm.integ.utils.Convertor;
import orm.integ.utils.IdGenerator;


public class EAOTest {
	
	static StudentService studentService;
	static SchoolClassService classService;
	static EntityAccessObject<Student> studentEao;
	
	@BeforeClass
	public static void beforeAll() {
		
		Log4jHelper.initLogger();
		studentService = new StudentService();
		classService = new SchoolClassService();
		
		studentEao = studentService.getEao();
		
		classService.getEao().delete("", null, false);
		
		SchoolClass sc = new SchoolClass();
		sc.setId(1);
		sc.setName("1班");
		sc.setGrade(1);
		classService.getEao().insert(sc);
		
	}
	
	@Before
	public void before() {
		studentService.getEao().delete("", null, false);
	}
	
	@Test
	public void testFieldMapping() {
		
		Student s1 = new Student();
		s1.setId("s1");
		s1.setName("小明");
		s1.setBirthday(Convertor.strToDate("20080121"));
		s1.setSchoolClassId(1);
		studentEao.insert(s1);
		
		s1 = studentEao.getById("s1");
		String[] fields = new String[]{"id","name","className","age"};
		Record rec = studentEao.toRecord(s1, fields);
		String className = rec.getString("className");
		String age = rec.getString("age");
		
		Assert.assertEquals("1班", className);
		Assert.assertNotNull(age);
		
		//Printer.print(rec, "record");
		
	}
	
	@Test
	public void testCUD() {
		
		final String id = "s1";
		
		Student s1 = new Student();
		s1.setId(id);
		s1.setName("小明");
		studentEao.insert(s1);
		
		Student s2 = studentEao.getById(id);
		Assert.assertNotNull(s2);
		
		s2.setName("小华");
		studentEao.update(s2);
		
		Assert.assertEquals(s2.getName(), "小华");
		
		studentEao.deleteById(id, true);
		Student s4 = studentEao.getById(id);
		Assert.assertNull(s4);
		
	}
	
	@Test
	public void testQuery() {

		Student s1 = new Student();
		s1.setId("s1");
		s1.setName("小明");
		s1.setSchoolClassId(1);
		studentEao.insert(s1);

		Student s2 = new Student();
		s2.setId("s2");
		s2.setName("小华");
		s2.setSchoolClassId(2);
		studentEao.insert(s2);
		
		TabQuery req = new TabQuery();
		req.addWhereItem("school_class_id=?", 2);
		List<Student> list = studentEao.query(req);
		Assert.assertEquals(1, list.size());
		
		SqlQuery sqlReq = new SqlQuery("select * from tb_student where student_id=?", "s1");
		list = studentEao.query(sqlReq);
		Assert.assertEquals(1, list.size());

		int count = studentEao.queryCount("school_class_id=?", 2);
		Assert.assertEquals(1, count);

	}

	@Test
	public void testPageQuery() {
		for (int i=0; i<100; i++) {
			Student s1 = new Student();
			s1.setName(IdGenerator.createRandomStr(12, false));
			s1.setSchoolClassId(3);
			studentEao.insert(s1);
		}
		TabQuery tq = new TabQuery();
		tq.addWhereItem("school_class_id=?", 3);
		tq.setPageInfo(21, 10);
		PageData page = studentEao.pageQuery(tq);
		Assert.assertEquals(100, page.getTotalCount());
		Assert.assertEquals(10, page.getList().size());
	}

}
