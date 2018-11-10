# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.6.25)
# Database: shiro
# Generation Time: 2018-11-10 10:09:00 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table bs_test
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bs_test`;

CREATE TABLE `bs_test` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `bs_test` WRITE;
/*!40000 ALTER TABLE `bs_test` DISABLE KEYS */;

INSERT INTO `bs_test` (`id`, `name`)
VALUES
	(1,'默认'),
	(2,'和谐类'),
	(3,'A类'),
	(4,'B类'),
	(5,'C类'),
	(6,'D类'),
	(7,'E类');

/*!40000 ALTER TABLE `bs_test` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table sys_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_resource`;

CREATE TABLE `sys_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT '' COMMENT '权限类型：menu、button、url',
  `name` varchar(255) NOT NULL COMMENT '权限名称',
  `permission` varchar(255) NOT NULL COMMENT '权限字符串',
  `icon` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT '0',
  `url` varchar(255) DEFAULT '',
  `description` varchar(255) DEFAULT '' COMMENT '资源描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态值',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父ID',
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime NOT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_resource_type` (`type`,`permission`) USING BTREE,
  KEY `create_by` (`create_by`),
  KEY `update_by` (`update_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='资源（权限）表';

LOCK TABLES `sys_resource` WRITE;
/*!40000 ALTER TABLE `sys_resource` DISABLE KEYS */;

INSERT INTO `sys_resource` (`id`, `type`, `name`, `permission`, `icon`, `sort`, `url`, `description`, `status`, `parent_id`, `create_by`, `create_at`, `update_by`, `update_at`)
VALUES
	(1,'菜单','系统管理','system:*','fa fa-dashboard',1,'','',1,0,0,'2015-07-01 19:33:21',NULL,'2015-10-09 10:34:05'),
	(2,'菜单','角色管理','system:role:*','fa fa-male',12,'/role/config','',1,1,0,'2015-07-01 19:38:38',NULL,'2015-07-01 19:38:38'),
	(3,'菜单','密码修改','system:password',NULL,14,'/user/password/edition','',1,1,0,'2015-07-01 19:38:51',NULL,'2015-07-01 19:39:51'),
	(4,'菜单','操作日志','system:log:*',NULL,15,'/handle/operation/log','',1,1,0,'2015-07-01 19:40:37',NULL,'2015-07-01 19:40:37'),
	(5,'URL','新增角色','system:role:create',NULL,13,'/role/addition','',1,1,0,'2015-07-01 19:41:21',NULL,'2015-10-08 16:45:43'),
	(6,'菜单','用户管理','system:admin:*','fa fa-users',11,'/user/config','',1,1,0,'2015-07-01 19:34:38',NULL,'2015-07-01 19:34:38'),
	(7,'URL','新增用户','system:admin:create','',NULL,'/user/addition','bbb',1,0,0,'2015-08-30 18:29:56',NULL,'2015-10-09 11:33:03');

/*!40000 ALTER TABLE `sys_resource` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table sys_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `remark` varchar(255) DEFAULT '',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `parent_id` bigint(20) DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime NOT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `create_by` (`create_by`),
  KEY `update_by` (`update_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;

INSERT INTO `sys_role` (`id`, `name`, `code`, `remark`, `status`, `parent_id`, `create_by`, `create_at`, `update_by`, `update_at`)
VALUES
	(1,'超级管理员','superManager','拥有所有权限',1,NULL,0,'2015-09-01 14:36:16',NULL,'2016-01-03 22:29:58'),
	(2,'系统管理员','systemManager','拥有部分权限',1,NULL,0,'2015-08-30 18:03:47',NULL,'2015-08-30 18:03:47'),
	(3,'角色1','role1','nothing 34',1,NULL,NULL,'2015-10-05 18:20:35',NULL,'2015-10-05 18:35:57');

/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table sys_role_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role_resource`;

CREATE TABLE `sys_role_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `resource_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`),
  KEY `resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `sys_role_resource` WRITE;
/*!40000 ALTER TABLE `sys_role_resource` DISABLE KEYS */;

INSERT INTO `sys_role_resource` (`id`, `role_id`, `resource_id`)
VALUES
	(1,1,1),
	(2,1,2),
	(3,1,3),
	(4,1,4),
	(5,1,5),
	(6,1,6),
	(7,1,7),
	(8,2,2);

/*!40000 ALTER TABLE `sys_role_resource` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table sys_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile_phone` varchar(255) NOT NULL DEFAULT '' COMMENT '手机号码',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `salt` varchar(255) DEFAULT '' COMMENT '加密混淆字符',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别',
  `qq` bigint(20) DEFAULT NULL COMMENT 'QQ号码',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `avatar` varchar(500) DEFAULT '' COMMENT '头像图片路径',
  `province` varchar(50) DEFAULT '' COMMENT '省',
  `city` varchar(50) DEFAULT '' COMMENT '市',
  `reg_ip` varchar(50) DEFAULT NULL COMMENT '注册时IP地址',
  `score` int(10) DEFAULT '0' COMMENT '积分值',
  `status` int(10) DEFAULT '1' COMMENT '状态：0禁用 1正常',
  `create_by` bigint(20) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;

INSERT INTO `sys_user` (`id`, `mobile_phone`, `user_name`, `nickname`, `password`, `salt`, `signature`, `gender`, `qq`, `email`, `avatar`, `province`, `city`, `reg_ip`, `score`, `status`, `create_by`, `create_at`, `update_by`, `update_at`)
VALUES
	(1,'18966668888','super','超级管理员','e10adc3949ba59abbe56e057f20f883e',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL,1,NULL,'2015-09-28 17:47:18',NULL,'2015-09-30 17:36:16'),
	(2,'13988886666','admin','系统管理员A','e10adc3949ba59abbe56e057f20f883e',NULL,NULL,NULL,1234567,'super@millinch.com',NULL,NULL,NULL,NULL,NULL,1,NULL,'2015-09-29 17:47:22',NULL,'2015-09-30 17:32:07');

/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table sys_user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_roles` (`user_id`),
  KEY `fk_role_users` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`)
VALUES
	(1,1,1),
	(2,2,2);

/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table t_coupon_subject_store
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_coupon_subject_store`;

CREATE TABLE `t_coupon_subject_store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `city_id` bigint(20) NOT NULL COMMENT '城市id',
  `city_name` varchar(64) DEFAULT '' COMMENT '城市名称',
  `store_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `store_name` varchar(64) DEFAULT '' COMMENT '店铺名称',
  `subject_id` bigint(20) NOT NULL COMMENT '优惠券活动ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `t_coupon_subject_store` WRITE;
/*!40000 ALTER TABLE `t_coupon_subject_store` DISABLE KEYS */;

INSERT INTO `t_coupon_subject_store` (`id`, `city_id`, `city_name`, `store_id`, `store_name`, `subject_id`)
VALUES
	(1,110000,'北京市',1059,'情怀小店',1),
	(4,130300,'海港市',1000,'菜菜的店',2),
	(5,110000,'北京市',1042,'天才小卖铺',2),
	(6,110000,'北京市',1048,'小布丁杂货铺',2),
	(7,110000,'北京市',1071,'王小花的店',2);

/*!40000 ALTER TABLE `t_coupon_subject_store` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table tb_token
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_token`;

CREATE TABLE `tb_token` (
  `user_id` bigint(20) NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户Token';



# Dump of table tb_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tb_user`;

CREATE TABLE `tb_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';

LOCK TABLES `tb_user` WRITE;
/*!40000 ALTER TABLE `tb_user` DISABLE KEYS */;

INSERT INTO `tb_user` (`user_id`, `username`, `mobile`, `password`, `create_time`)
VALUES
	(1,'mark','13612345678','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','2017-03-23 22:37:41');

/*!40000 ALTER TABLE `tb_user` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
