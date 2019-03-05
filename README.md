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

Integ ORM框架仅用了约48K的代码，就实现了与Hibernate、MyBatis几乎一致的功能。

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

建实体类

```java
@EntityAnno(classId="stud", table="tb_student")
public class Student extends Entity {
	private Integer sex;
	@ForeignKey(masterClass=SchoolClass.class)
	private int schoolClassId;
	private String className;
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

建Service类

```java
public class StudentService extends EntityAccessService<Student> {
	@Override
	public DataAccessObject getDao() {
		return DaoUtil.getDao();
	}
	@Override
	public void setEntityConfig(EntityConfig config) {
		config.addNameMapping("className", "schoolClassId");
	}
	@Override
	protected void fillExtendFields(Student stu) {	}
}

public class SchoolClassService extends EntityAccessService<SchoolClass> {
	@Override
	public DataAccessObject getDao() {
		return DaoUtil.getDao();
	}
	@Override
	public void setEntityConfig(EntityConfig config) { }
	@Override
	protected void fillExtendFields(SchoolClass entity) { }
}
```

增加

```java
	Student s1 = new Student();
	s1.setId(id);
	s1.setName("小明");
	studentEao.insert(s1);

```

、修改、删除

、修改、删除


