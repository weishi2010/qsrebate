-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: rebate
-- ------------------------------------------------------
-- Server version	5.6.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `activity_link` varchar(200) NOT NULL COMMENT '活动链接',
  `img_url` varchar(200) NOT NULL COMMENT '图片地址',
  `remark` varchar(200) NOT NULL COMMENT '描述',
  `benefit` varchar(200) NOT NULL COMMENT '利益描述',
  `begin_time` datetime NOT NULL COMMENT '活动开始时间',
  `end_time` datetime NOT NULL COMMENT '活动结束时间',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_category` int(11) NOT NULL COMMENT '一级分类',
  `first_category_name` varchar(200) NOT NULL COMMENT '一级分类名称',
  `second_category` int(11) NOT NULL COMMENT '二级分类',
  `second_category_name` varchar(200) NOT NULL COMMENT '二级分类名称',
  `third_category` int(11) NOT NULL COMMENT '三级分类',
  `third_category_name` varchar(200) NOT NULL COMMENT '三级分类名称',
  `source` int(11) NOT NULL COMMENT '来源',
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7814 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commission`
--

DROP TABLE IF EXISTS `commission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(200) NOT NULL,
  `total_commission` decimal(10,0) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint(20) NOT NULL COMMENT '商品编号',
  `coupon_price` decimal(10,0) NOT NULL COMMENT '券后价',
  `coupon_link` varchar(200) NOT NULL COMMENT '券链接',
  `type` tinyint(4) NOT NULL COMMENT '类型',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `extract_detail`
--

DROP TABLE IF EXISTS `extract_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `extract_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `open_id` varchar(200) NOT NULL COMMENT '微信openId',
  `extract_date` datetime NOT NULL COMMENT '提现时间',
  `extract_price` decimal(10,0) NOT NULL COMMENT '提现金额',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_summary`
--

DROP TABLE IF EXISTS `order_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `open_id` varbinary(200) NOT NULL COMMENT '微信openId',
  `summary_date` datetime NOT NULL COMMENT '统计日期',
  `click_count` int(11) NOT NULL COMMENT '点击数',
  `active_order_count` int(11) NOT NULL COMMENT '有效订单数量',
  `active_order_price` decimal(10,0) NOT NULL COMMENT '有效订单金额',
  `estimated _income` decimal(10,0) NOT NULL COMMENT '预估收入',
  `order_source` int(11) NOT NULL COMMENT '下单来源',
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `first_category` int(11) NOT NULL,
  `first_category_name` varchar(200) NOT NULL,
  `second_category` int(11) NOT NULL,
  `second_category_name` varchar(200) NOT NULL,
  `third_category` int(11) NOT NULL,
  `third_category_name` varchar(200) NOT NULL,
  `original_price` double NOT NULL COMMENT '原价',
  `stock` int(11) NOT NULL COMMENT '库存',
  `commission_ratio_pc` double NOT NULL COMMENT 'PC返佣比例',
  `commission_ratio_wl` double NOT NULL COMMENT '移动返佣比例',
  `product_type` int(11) NOT NULL,
  `distribution` int(11) NOT NULL COMMENT '配送方式',
  `status` tinyint(4) NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `img_url` varchar(200) NOT NULL COMMENT '图片地址',
  `is_rebate` tinyint(4) NOT NULL COMMENT '是否返佣',
  `sort_weight` int(11) NOT NULL COMMENT '排序权重',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2543 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rebate_detail`
--

DROP TABLE IF EXISTS `rebate_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rebate_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `open_id` varchar(200) NOT NULL COMMENT '微信openId',
  `order_id` bigint(20) NOT NULL COMMENT '订单号',
  `product_count` int(11) NOT NULL COMMENT '商品数量',
  `commission` double NOT NULL COMMENT '佣金金额',
  `commission_ratio` double NOT NULL COMMENT '佣金比例',
  `rebate_ratio` double NOT NULL COMMENT '分成比例',
  `submit_date` datetime NOT NULL COMMENT '下单时间',
  `finish_date` datetime NOT NULL COMMENT '订单完成时间',
  `order_status` tinyint(4) NOT NULL COMMENT '订单状态',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `price` double NOT NULL DEFAULT '0',
  `product_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `id` tinyint(11) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(200) NOT NULL COMMENT '微信openId',
  `wx_image` varchar(200) NOT NULL COMMENT '微信头像',
  `phone` varchar(200) NOT NULL COMMENT '手机号',
  `nick_name` varchar(200) NOT NULL COMMENT '昵称',
  `email` varchar(200) NOT NULL COMMENT '邮箱',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-16 23:55:16
