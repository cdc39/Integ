package orm.integ.test.entity;

import java.util.Calendar;
import java.util.Date;

import orm.integ.dao.DataAccessObject;
import orm.integ.eao.EntityAccessService;
import orm.integ.eao.IdGenerator;
import orm.integ.eao.model.EntityConfig;
import orm.integ.test.DaoUtil;

public class StudentService extends EntityAccessService<Student> {
	
	@Override
	public Object createNewId() {
		return IdGenerator.createRandomStr(12, false);
	}
	@Override
	public DataAccessObject getDao() {
		return DaoUtil.getDao();
	}
	
	@Override
	public void setEntityConfig(EntityConfig config) {
		config.setNameMapping("className", "schoolClassId");
	}
	
	public void tran1() {
		Student s = new Student();
		s.setName("张三");
		eao.insert(s);
		s.setName("李四");
		eao.update(s, "name");
		throw new RuntimeException("模拟发生错误");
	}
	
	static void testTran() {
		StudentService service = new StudentService();
		service.executeTransaction("tran1");
	}	
	
	@Override
	protected void fillExtendFields(Student stu) {
		if (stu.getBirthday()!=null) {
			int age = getYearDis(stu.getBirthday(), new Date());
			stu.setAge(age);
		}
	}
	
	private int getYearDis(Date dateStart, Date dateEnd) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(dateStart);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(dateEnd);
		int y1 = c1.get(Calendar.YEAR);
		int y2 = c2.get(Calendar.YEAR);
		int dis = (y2-y1);
		return dis;
	}
	
}
