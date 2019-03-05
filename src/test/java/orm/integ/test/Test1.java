package orm.integ.test; 

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;

import orm.integ.dao.DataAccessObject;
import orm.integ.dao.sql.TabQuery;
import orm.integ.eao.EntityAccessObject;
import orm.integ.eao.cache.QueryManager;
import orm.integ.eao.model.Record;
import orm.integ.test.entity.Student;
import orm.integ.test.entity.StudentService;
import orm.integ.utils.Convertor;

public class Test1 {

	static { 
		Log4jHelper.initLogger();
	}
	
	public static void main(String[] args) throws Exception {
		//String sql = "select * from (select rownum rn, t.* from tb_student t  where rownum<=100) where rn>0";
		//DaoUtil.getDao().testQuery(sql);
		
		//SchoolClassAdapter scAdapter = new SchoolClassAdapter();
		//EntityAccessObject<SchoolClass> scEao = new EntityAccessObject<SchoolClass>(scAdapter);
		
		//DataMaker.createStudents(eao, 20);
		//DataMaker.createStudents(50, 60);
		
		//createTestData(eao);
		//createStudents(eao, 2000);
		
		testInsert();
		testInsert();

		//testTran(); 
		
	}
	
	static void makeStudents() {
		StudentService service = new StudentService();
		Random rand = new Random();
		
		TimeMonitor tm = new TimeMonitor("10000 times query");
		for (int i=0; i<10000; i++) {
			TabQuery tq = new TabQuery();
			tq.setPageInfo(rand.nextInt(100)*20+1, 10);
			service.getEao().pageQuery(tq);
		}
		tm.finish();
		System.out.println("queryByIdsCount="+EntityAccessObject.queryByIdsCount);
		QueryManager.printStat();
	}
	
	static void testTran() {
		StudentService service = new StudentService();
		service.executeTransaction("testTran");
	}
	
	static void testInsert() {
		DataAccessObject dao = DaoUtil.getDao();
		Map<String, Object> cols = new HashMap<>();
		cols.put("student_id", "1");
		cols.put("student_name", "tom");
		cols.put("school_class_id", "1");
		cols.put("create_time", new Date());
		dao.insert("tb_student", cols);
	}
	
	static void test2(EntityAccessObject<Student> eao) {
		
		eao.deleteById("s1");
		
		Student s1 = new Student();
		s1.setId("s1");
		s1.setName("小明");
		s1.setBirthday(Convertor.strToDate("20080121"));
		s1.setSchoolClassId(1);
		eao.insert(s1);
		
		s1 = eao.getById("s1");
		Record rec = eao.toRecord(s1, new String[]{"id","name","className","age"});
		String className = rec.get("className");
		String age = rec.get("age");
		Assert.assertEquals("1班", className);
		Assert.assertNotNull(age);
		
	}
	
	static void test1(EntityAccessObject<Student> eao) {
		
		eao.deleteById("s1");
		
		Student s1 = new Student();
		s1.setId("s1");
		s1.setName("小明");
		eao.insert(s1);
		
		Student s2 = eao.getById("s1");
		System.out.println("get student: name="+s2.getName());
		
		s2.setName("小华");
		eao.update(s2, new String[]{"name"});
		
	}
	

	
}
