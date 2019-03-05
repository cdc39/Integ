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

```
CREATE TABLE `tb_student` (
  `student_id` varchar(20) NOT NULL,
  `student_name` varchar(100) NOT NULL,
  `school_class_id` smallint(4) NOT NULL,
  `sex` tinyint(1) default NULL,
  `birthday` date default NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY  (`student_id`)
) DEFAULT CHARSET=utf8;
```




