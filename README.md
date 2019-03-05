# 简单又强大的新一代Java持久层框架：Integ ORM

## Integ ORM简介

每一个涉及数据库的Java应用系统都需要解决从关系数据库数据到Java对象的映射问题，又称为Object Relation Mapping问题，可简写为O/R Mapping或ORM。解决ORM问题的框架又被称为持久层框架。一个优良的持久层框架可以大大提高Java系统的开发效率和质量。

Integ 是极其简单又功能完整的Java持久层框架，它的主要功能特点有：

* 支持增删改查询
* 支持属性映射
* 支持分页查询
* 支持单表标准化查询和自由SQL查询
* 支持数据缓存和查询缓存
* 支持事务
* 支持多种数据库，当前支持MySQL,Oracle,SQLServer,PostgreSQL

Integ中最重要的一个思路，就是把ORM问题划分为两个层次：DAO层（数据访问层）和EAO层（实体对象访问层）。
在这个指导思想下，Integ ORM框架用了很少的代码，就实现了与Hibernate、MyBatis几乎一致的功能。

Integ的使用方法也是非常简单。

## Integ的使用方法

先建表

```sql
CREATE TABLE `tb_student` (
  `student_id` varchar(20) NOT NULL,
  `student_name` varchar(100) NOT NULL,
  `school_class_id` smallint(4) NOT NULL,
  `sex` tinyint(1) default NULL,
  `birthday` date default NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY  (`student_id`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `tb_school_class` (
  `school_class_id` smallint(4) NOT NULL,
  `school_class_name` varchar(100) NOT NULL,
  `grade` tinyint(2) NOT NULL,
  `create_time` datetime default NULL,
  PRIMARY KEY  (`school_class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

定义实体类

```java
@EntityAnno(classId="stud", table="tb_student")
public class Student extends Entity {
	private Integer sex;
	@ForeignKey(masterClass=SchoolClass.class)
	private int schoolClassId;
	private String className;  // 映射属性
	public Integer getSex() { return sex; }
	public void setSex(Integer sex) { this.sex = sex; }
	public int getSchoolClassId() { return schoolClassId; }
	public void setSchoolClassId(int schoolClassId) { this.schoolClassId = schoolClassId; }
	public String getClassName() { return className; }
	public void setClassName(String className) { this.className = className; }
}

@EntityAnno(classId="scls", table="tb_school_class")
public class SchoolClass extends Entity {
	private int grade;  // 年级
	public int getGrade() { return grade; }
	public void setGrade(int grade) { this.grade = grade; }
}

```

定义Service类，以及增加/修改/删除/查询 处理示范

```java

public class StudentService extends EntityAccessService<Student> {
	@Override
	public DataAccessObject getDao() {
		return DaoUtil.getDao();
	}
	@Override
	public void setEntityConfig(EntityConfig config) {
		config.addNameMapping("className", "schoolClassId");  // 关联映射属性配置，取值来自 SchoolClass.name
	}
	@Override
	protected void fillExtendFields(Student stu) {	}
	
	// 增加/修改/删除 操作示范
	public void testCUD() {
		
		// 增加
		Student st = new Student();
		st.setId("s1");
		st.setName("小明");
		st.setSchoolClassId(1);
		eao.insert(st);

		// 修改
		st = eao.getById("s1");
		st.setName("小华");
		eao.update(st, "name");
		
		// 删除
		eao.deleteById("s1");
	}

	// 查询操作示范
	public void testQuery() {

		// 单表模式化查询
		TabQuery req = new TabQuery();
		req.addWhereItem("school_class_id=?", 1);
		List<Student> list1 = eao.query(req);

		// 自由SQL查询
		SqlQuery sqlReq = new SqlQuery("select * from tb_student where student_id=?", "s1");
		List<Student> list2 = eao.query(sqlReq);

		// 查询记录数
		int count = eao.queryCount("school_class_id=?", 1);

		// 分页查询
		TabQuery tq = new TabQuery();
		tq.addWhereItem("school_class_id=?", 1);
		tq.setPageInfo(21, 10);  // 从第21行开始，查询10行
		PageData page = eao.pageQuery(tq);		
		
	}
	
	// 事务样板
	public void tran1() {
		Student s = new Student();
		s.setName("张三");
		eao.insert(s);
		s.setName("李四");
		eao.update(s, "name");
		throw new RuntimeException("模拟发生错误");
	}	
	
	// 事务调用
	public void testTran() {
		executeTransaction("testTran");
	}
	
}

```

每一个Service类里面都会包含一个eao对象和一个dao对象，eao（EntityAccessObject类型）对象用于处理特定类型（比如Student）的实体对象，dao（DataAccessObject）对象用于数据库相关操作。eao以dao为基础，为Service层提供支持。



## 总结

ORM问题域的需求，是需要直接操作对象而不是操作数据库，需要增删改查功能，需要缓存，需要事务，需要广泛的适应性，灵活的扩展性，这些Integ都能提供。未来要适配几十种数据库，适配Redis，适配分库分表，要改起来都不算难事。因此 Integ 可以说是功能完备的ORM框架。

同时Integ还做到了极简。目前Integ代码只有48K，发展到究极形态，估计也不会超过300K。

Hibernate较EJB更轻量级
MyBatis较Hibernate更轻量级
Integ较MyBatis更轻量级

轻量级不代表功能弱，比如MyBatis是没有提供分页查询功能的，有大牛写了个广受欢迎的MyBatis的分页查询的插件PageHelper，就用去了200K代码。然而在Integ里面不用插件，已经天然集成了分页查询功能。

代码少说明结构简单，逻辑简单。面对简单的结构和逻辑，不论是学习，还是要修改扩展，都会更简单，更轻松，可以省下来很多宝贵的时间。

如果你对Hibernate复杂的注解搞得头晕脑胀、战战兢兢，如果你对MyBatic动态条件查询的实现方式（在配置文件中而不是在java代码中）感到怪异，如果你厌烦了隔绝了底层代码，让逻辑陡然复杂起来的动态代理，那么来试试 Integ 吧，你会发现这里有更简洁、更优雅的解决方案。

欢迎大家下载使用。并欢迎提出改进意见，帮助测试、修改，一起努力让Integ变得更好。谢谢！
