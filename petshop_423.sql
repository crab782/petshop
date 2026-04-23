-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: cg
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text COLLATE utf8mb4_unicode_ci,
  `end_time` timestamp NULL DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `organizer` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '娲诲姩缁勭粐鑰',
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '娲诲姩鍦扮偣',
  `max_participants` int DEFAULT '0' COMMENT '鏈?ぇ鍙備笌浜烘暟',
  `current_participants` int DEFAULT '0' COMMENT '褰撳墠鍙備笌浜烘暟',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `detail_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `district` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_default` bit(1) NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKda8tuywtf0gb6sedwk7la1pgi` (`user_id`),
  CONSTRAINT `FKda8tuywtf0gb6sedwk7la1pgi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (2,'superadmin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','2024-01-01 00:00:00','2024-01-01 00:00:00'),(3,'operator','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','2024-01-01 00:00:00','2024-01-01 00:00:00'),(6,'admin','$2a$10$XIhTckH2R7h1ty/yq7hNAus/f/maHTq1Pp2wWwuomghLJix6kLBYe','2026-04-22 15:35:51','2026-04-22 15:35:51');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `announcement` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
INSERT INTO `announcement` VALUES (1,'平台上线公告','尊敬的用户，我们的宠物服务平台正式上线了！感谢您的支持与信任，我们将竭诚为您提供优质的宠物服务。','2024-01-01 03:00:00','2024-01-01 03:00:00',NULL),(2,'春节营业时间调整通知','春节期间（2024年2月10日-2月17日），部分商家营业时间有所调整，请提前预约。','2024-01-20 03:00:00','2024-01-20 03:00:00',NULL),(3,'新功能上线：在线预约系统升级','我们的在线预约系统已完成升级，新增了智能推荐、快速预约等功能，欢迎体验！','2024-02-01 03:00:00','2024-02-01 03:00:00',NULL),(4,'宠物健康知识讲座预告','我们将于2024年3月举办宠物健康知识讲座，邀请知名宠物医生分享养宠经验，敬请关注！','2024-02-15 03:00:00','2024-02-15 03:00:00',NULL),(5,'会员积分活动开启','即日起至2024年3月31日，注册会员可享受双倍积分优惠，积分可兑换精美礼品！','2024-02-20 03:00:00','2024-02-20 03:00:00',NULL),(6,'平台服务协议更新通知','我们更新了平台服务协议，请仔细阅读相关条款，继续使用即表示同意新协议。','2024-03-01 03:00:00','2024-03-01 03:00:00',NULL),(7,'春季宠物护理小贴士','春季是宠物换毛期，请注意给宠物梳毛、补充营养，定期进行健康检查。','2024-03-10 03:00:00','2024-03-10 03:00:00',NULL),(8,'商家入驻优惠政策','新商家入驻可享受首月免佣金优惠，名额有限，先到先得！','2024-03-15 03:00:00','2024-03-15 03:00:00',NULL),(9,'宠物摄影大赛即将开始','我们将于4月举办宠物摄影大赛，欢迎上传您家萌宠的精彩瞬间，赢取丰厚奖品！','2024-03-20 03:00:00','2024-03-20 03:00:00',NULL),(10,'系统维护通知','系统将于2024年3月25日凌晨2:00-6:00进行维护升级，届时服务将暂停，请提前安排。','2024-03-22 03:00:00','2024-03-22 03:00:00',NULL);
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `service_id` int NOT NULL,
  `pet_id` int NOT NULL,
  `appointment_time` datetime NOT NULL,
  `status` enum('pending','confirmed','completed','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `total_price` decimal(10,2) DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '棰勭害鍗曞彿',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `pet_id` (`pet_id`),
  KEY `idx_appointment_merchant_id` (`merchant_id`),
  KEY `idx_appointment_user_id` (`user_id`),
  KEY `idx_appointment_service_id` (`service_id`),
  KEY `idx_appointment_status` (`status`),
  KEY `idx_appointment_appointment_time` (`appointment_time`),
  KEY `idx_appointment_order_no` (`order_no`),
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `appointment_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `appointment_ibfk_4` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4008 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES (1,1,1,1,1,'2024-02-01 10:00:00','completed',88.00,'希望能洗得干净一点','2024-01-25 01:00:00','2024-02-01 04:00:00',NULL),(2,1,1,2,1,'2024-02-05 14:00:00','completed',168.00,'剪个可爱的造型','2024-01-26 01:00:00','2024-02-05 08:00:00',NULL),(3,2,2,4,3,'2024-02-03 09:00:00','completed',298.00,'全面体检','2024-01-27 01:00:00','2024-02-03 03:00:00',NULL),(4,2,2,5,3,'2024-02-10 10:00:00','completed',128.00,'打疫苗','2024-01-28 01:00:00','2024-02-10 03:00:00',NULL),(5,3,3,7,5,'2024-02-08 14:00:00','completed',388.00,'训练坐下和握手','2024-01-29 01:00:00','2024-02-08 08:00:00',NULL),(6,3,4,9,6,'2024-02-12 15:00:00','completed',268.00,'做一次SPA','2024-01-30 01:00:00','2024-02-12 09:00:00',NULL),(7,4,5,12,7,'2024-02-15 10:00:00','completed',588.00,'拍一组艺术照','2024-01-31 01:00:00','2024-02-15 05:00:00',NULL),(8,5,6,14,9,'2024-02-18 09:00:00','completed',68.00,'接送服务','2024-02-01 01:00:00','2024-02-18 02:00:00',NULL),(9,6,8,17,11,'2024-02-20 14:00:00','completed',168.00,'按摩放松','2024-02-02 01:00:00','2024-02-20 08:00:00',NULL),(10,7,10,19,13,'2024-02-22 10:00:00','completed',128.00,'游泳锻炼','2024-02-03 01:00:00','2024-02-22 03:00:00',NULL),(11,8,1,1,15,'2024-02-25 11:00:00','confirmed',88.00,'洗澡服务','2024-02-04 01:00:00','2024-02-04 01:00:00',NULL),(12,9,2,4,17,'2024-02-26 09:00:00','confirmed',298.00,'体检服务','2024-02-05 01:00:00','2024-02-05 01:00:00',NULL),(13,10,3,7,19,'2024-02-27 14:00:00','confirmed',388.00,'训练服务','2024-02-06 01:00:00','2024-02-06 01:00:00',NULL),(14,11,4,9,21,'2024-02-28 15:00:00','confirmed',268.00,'SPA服务','2024-02-07 01:00:00','2024-02-07 01:00:00',NULL),(15,12,5,12,23,'2024-02-29 10:00:00','confirmed',588.00,'摄影服务','2024-02-08 01:00:00','2024-02-08 01:00:00',NULL),(16,13,6,14,25,'2024-03-01 09:00:00','pending',68.00,'接送服务','2024-02-09 01:00:00','2024-02-09 01:00:00',NULL),(17,14,8,17,26,'2024-03-02 14:00:00','pending',168.00,'按摩服务','2024-02-10 01:00:00','2024-02-10 01:00:00',NULL),(18,15,10,19,27,'2024-03-03 10:00:00','pending',128.00,'游泳服务','2024-02-11 01:00:00','2024-02-11 01:00:00',NULL),(19,16,1,2,28,'2024-03-04 11:00:00','pending',168.00,'美容服务','2024-02-12 01:00:00','2024-02-12 01:00:00',NULL),(20,17,2,5,29,'2024-03-05 09:00:00','pending',128.00,'疫苗服务','2024-02-13 01:00:00','2024-02-13 01:00:00',NULL),(21,18,3,8,30,'2024-03-06 14:00:00','pending',488.00,'行为矫正','2024-02-14 01:00:00','2024-02-14 01:00:00',NULL),(22,19,4,10,1,'2024-03-07 15:00:00','pending',38.00,'指甲修剪','2024-02-15 01:00:00','2024-02-15 01:00:00',NULL),(23,20,5,13,2,'2024-03-08 10:00:00','pending',288.00,'婚介服务','2024-02-16 01:00:00','2024-02-16 01:00:00',NULL),(24,1,6,15,3,'2024-03-09 09:00:00','pending',188.00,'上门服务','2024-02-17 01:00:00','2024-02-17 01:00:00',NULL),(25,2,8,18,4,'2024-03-10 14:00:00','pending',198.00,'牙齿清洁','2024-02-18 01:00:00','2024-02-18 01:00:00',NULL),(26,3,10,20,5,'2024-03-11 10:00:00','pending',168.00,'瑜伽课程','2024-02-19 01:00:00','2024-02-19 01:00:00',NULL),(27,4,1,3,7,'2024-02-01 18:00:00','cancelled',128.00,'寄养服务','2024-01-20 01:00:00','2024-01-30 02:00:00',NULL),(28,5,2,6,9,'2024-02-05 09:00:00','cancelled',98.00,'驱虫服务','2024-01-21 01:00:00','2024-02-01 02:00:00',NULL),(29,6,3,7,11,'2024-02-10 14:00:00','cancelled',388.00,'训练服务','2024-01-22 01:00:00','2024-02-08 02:00:00',NULL),(30,7,4,11,13,'2024-02-15 09:00:00','cancelled',58.00,'耳道清洁','2024-01-23 01:00:00','2024-02-10 02:00:00',NULL),(31,8,5,12,15,'2024-02-20 10:00:00','cancelled',588.00,'摄影服务','2024-01-24 01:00:00','2024-02-15 02:00:00',NULL),(32,9,6,16,17,'2024-03-12 09:00:00','pending',98.00,'日托服务','2024-02-20 01:00:00','2024-02-20 01:00:00',NULL),(33,10,8,21,19,'2024-03-13 11:00:00','pending',228.00,'造型服务','2024-02-21 01:00:00','2024-02-21 01:00:00',NULL),(34,11,10,22,21,'2024-03-14 09:00:00','pending',888.00,'绝育手术','2024-02-22 01:00:00','2024-02-22 01:00:00',NULL),(35,12,1,23,23,'2024-03-15 14:00:00','pending',0.00,'保险咨询','2024-02-23 01:00:00','2024-02-23 01:00:00',NULL),(36,13,2,24,25,'2024-03-16 15:00:00','pending',98.00,'营养咨询','2024-02-24 01:00:00','2024-02-24 01:00:00',NULL),(37,14,3,25,26,'2024-03-17 10:00:00','pending',688.00,'生日派对','2024-02-25 01:00:00','2024-02-25 01:00:00',NULL),(38,15,4,26,27,'2024-03-18 09:00:00','pending',1288.00,'殡葬服务','2024-02-26 01:00:00','2024-02-26 01:00:00',NULL),(39,16,8,27,28,'2024-03-19 14:00:00','pending',188.00,'心理咨询','2024-02-27 01:00:00','2024-02-27 01:00:00',NULL),(40,17,10,28,29,'2024-03-20 10:00:00','pending',288.00,'服装定制','2024-02-28 01:00:00','2024-02-28 01:00:00',NULL),(2001,2001,2001,2001,2001,'2024-01-25 14:00:00','pending',88.00,'需要给狗狗剪指甲','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL),(2002,2001,2002,2002,2002,'2024-01-20 10:30:00','confirmed',150.00,'年度体检','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL),(2003,2001,2003,2003,2001,'2024-01-15 09:00:00','completed',50.00,'寄养3天','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL),(2004,2001,2001,2001,2002,'2024-01-10 15:00:00','cancelled',88.00,'临时有事取消','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL),(3001,3001,3001,3001,3001,'2024-01-25 14:00:00','pending',88.00,'需要给狗狗剪指甲','2026-04-22 14:34:03','2026-04-22 14:34:03',NULL),(3002,3001,3002,3002,3002,'2024-01-20 10:30:00','confirmed',150.00,'年度体检','2026-04-22 14:34:03','2026-04-22 14:34:03',NULL),(3003,3001,3003,3003,3001,'2024-01-15 09:00:00','completed',50.00,'寄养3天','2026-04-22 14:34:03','2026-04-22 14:34:03',NULL),(3004,3001,3001,3001,3002,'2024-01-10 15:00:00','cancelled',88.00,'临时有事取消','2026-04-22 14:34:03','2026-04-22 14:34:03',NULL),(4001,4001,4001,4001,4001,'2024-01-25 14:00:00','pending',88.00,'需要给狗狗剪指甲','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL),(4002,4001,4002,4002,4002,'2024-01-20 10:30:00','confirmed',150.00,'年度体检','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL),(4003,4001,4003,4003,4001,'2024-01-15 09:00:00','completed',50.00,'寄养3天','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL),(4004,4001,4001,4001,4002,'2024-01-10 15:00:00','cancelled',88.00,'临时有事取消','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL),(4005,1,4006,4004,1,'2026-04-20 10:00:00','completed',50.00,NULL,'2026-04-22 15:59:14','2026-04-22 15:59:14',NULL),(4006,2,4006,4005,3,'2026-04-21 14:00:00','completed',80.00,NULL,'2026-04-22 15:59:14','2026-04-22 15:59:14',NULL),(4007,3,4006,4006,5,'2026-04-22 09:00:00','completed',100.00,NULL,'2026-04-22 15:59:14','2026-04-22 15:59:14',NULL);
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `quantity` int NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `product_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3d704slv66tw6x5hmbm6p2x3u` (`product_id`),
  KEY `FKl70asp4l4w0jmbm1tqyofho4o` (`user_id`),
  CONSTRAINT `FK3d704slv66tw6x5hmbm6p2x3u` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKl70asp4l4w0jmbm1tqyofho4o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text COLLATE utf8mb4_unicode_ci,
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `merchant_id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_count` int NOT NULL,
  `sort` int NOT NULL,
  `status` enum('enabled','disabled') COLLATE utf8mb4_unicode_ci DEFAULT 'enabled',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `product_id` int DEFAULT NULL,
  `service_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `merchant_id` (`merchant_id`),
  KEY `FKbg4txsew6x3gl6r9swcq190hg` (`product_id`),
  KEY `FK523w88i2ia25cqq2hahcs66yk` (`service_id`),
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `FK523w88i2ia25cqq2hahcs66yk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `FKbg4txsew6x3gl6r9swcq190hg` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,1,1,'2024-02-01 02:00:00',NULL,NULL),(2,1,2,'2024-02-02 02:00:00',NULL,NULL),(3,2,3,'2024-02-03 02:00:00',NULL,NULL),(4,2,4,'2024-02-04 02:00:00',NULL,NULL),(5,3,5,'2024-02-05 02:00:00',NULL,NULL),(6,3,6,'2024-02-06 02:00:00',NULL,NULL),(7,4,8,'2024-02-07 02:00:00',NULL,NULL),(8,4,10,'2024-02-08 02:00:00',NULL,NULL),(9,5,1,'2024-02-09 02:00:00',NULL,NULL),(10,5,2,'2024-02-10 02:00:00',NULL,NULL),(11,6,3,'2024-02-11 02:00:00',NULL,NULL),(12,6,4,'2024-02-12 02:00:00',NULL,NULL),(13,7,5,'2024-02-13 02:00:00',NULL,NULL),(14,7,6,'2024-02-14 02:00:00',NULL,NULL),(15,8,8,'2024-02-15 02:00:00',NULL,NULL),(16,8,10,'2024-02-16 02:00:00',NULL,NULL),(17,9,1,'2024-02-17 02:00:00',NULL,NULL),(18,9,2,'2024-02-18 02:00:00',NULL,NULL),(19,10,3,'2024-02-19 02:00:00',NULL,NULL),(20,10,4,'2024-02-20 02:00:00',NULL,NULL),(2001,2001,2001,'2026-04-22 14:31:59',NULL,NULL),(2002,2001,2002,'2026-04-22 14:31:59',NULL,NULL),(2003,2001,2003,'2026-04-22 14:31:59',NULL,NULL),(2004,2001,2004,'2026-04-22 14:31:59',NULL,NULL),(4001,4001,4001,'2026-04-22 14:36:04',NULL,NULL),(4002,4001,4002,'2026-04-22 14:36:04',NULL,NULL),(4003,4001,4003,'2026-04-22 14:36:04',NULL,NULL),(4004,4001,4004,'2026-04-22 14:36:04',NULL,NULL);
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_comment`
--

DROP TABLE IF EXISTS `forum_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `forum_comment_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_post` (`id`),
  CONSTRAINT `forum_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_comment`
--

LOCK TABLES `forum_comment` WRITE;
/*!40000 ALTER TABLE `forum_comment` DISABLE KEYS */;
INSERT INTO `forum_comment` VALUES (1,1,2,'写得真好！我也是新手，学到了很多。','2024-02-01 07:00:00'),(2,1,3,'英短蓝猫真的很可爱，我也想养一只！','2024-02-01 08:00:00'),(3,2,1,'金毛真的很聪明，训练起来应该不难。','2024-02-02 07:00:00'),(4,2,4,'请问训练大概花了多长时间？','2024-02-02 08:00:00'),(5,3,5,'柯基真的太可爱了，特别是那个小短腿！','2024-02-03 07:00:00'),(6,3,6,'柯基的腰真的要注意保护，容易得腰椎病。','2024-02-03 08:00:00'),(7,4,7,'我也遇到同样的问题，期待大家的解答！','2024-02-04 07:00:00'),(8,4,8,'可以试试给猫咪梳毛，每天梳一梳会好很多。','2024-02-04 08:00:00'),(9,5,9,'这个时间表太有用了，收藏了！','2024-02-05 07:00:00'),(10,5,10,'请问幼猫第一针疫苗应该什么时候打？','2024-02-05 08:00:00'),(11,6,11,'选择猫粮真的太难了，品牌太多了。','2024-02-06 07:00:00'),(12,6,12,'建议选择无谷猫粮，对猫咪健康更好。','2024-02-06 08:00:00'),(13,7,13,'拉布拉多真的很温顺，很适合家庭养。','2024-02-07 07:00:00'),(14,7,14,'训练拉布拉多需要很大的耐心。','2024-02-07 08:00:00'),(15,8,15,'哈士奇拆家是出了名的，我家也是！','2024-02-08 07:00:00'),(16,8,16,'可以多带它出去运动，消耗精力。','2024-02-08 08:00:00'),(17,9,17,'绝育确实有利有弊，要慎重考虑。','2024-02-09 07:00:00'),(18,9,18,'我家猫咪绝育后性格变得更温顺了。','2024-02-09 08:00:00'),(19,10,19,'猫狗双全真的很幸福！','2024-02-10 07:00:00'),(20,10,20,'让它们从小一起长大，会更容易相处。','2024-02-10 08:00:00'),(21,11,1,'边牧真的超级聪明，学东西特别快！','2024-02-11 07:00:00'),(22,11,2,'边牧需要大量的运动，不然会很焦躁。','2024-02-11 08:00:00'),(23,12,3,'雪纳瑞的造型真的很多样，很可爱！','2024-02-12 07:00:00'),(24,12,4,'请问美容大概需要多少钱？','2024-02-12 08:00:00'),(25,13,5,'柴犬的微笑真的太治愈了！','2024-02-13 07:00:00'),(26,13,6,'柴犬有时候也很倔强，不好训练。','2024-02-13 08:00:00'),(27,14,7,'贵宾犬的毛色护理确实很重要。','2024-02-14 07:00:00'),(28,14,8,'可以用一些美毛产品，效果不错。','2024-02-14 08:00:00'),(29,15,9,'加菲猫真的很可爱，但是要注意眼部护理。','2024-02-15 07:00:00'),(30,15,10,'加菲猫容易有呼吸道问题，要注意。','2024-02-15 08:00:00'),(31,16,11,'选择玩具真的要看宠物的喜好。','2024-02-16 07:00:00'),(32,16,12,'我家猫咪最喜欢逗猫棒，百玩不厌。','2024-02-16 08:00:00'),(33,17,13,'带宠物旅行真的要准备很多东西。','2024-02-17 07:00:00'),(34,17,14,'航空箱是必备的，要提前准备。','2024-02-17 08:00:00'),(35,18,15,'折耳猫的健康问题确实要重视。','2024-02-18 07:00:00'),(36,18,16,'建议定期带折耳猫去做检查。','2024-02-18 08:00:00'),(37,19,17,'培养良好习惯真的很重要！','2024-02-19 07:00:00'),(38,19,18,'从小培养比长大后再训练容易得多。','2024-02-19 08:00:00'),(39,20,19,'宠物摄影真的很有趣！','2024-02-20 07:00:00'),(40,20,20,'请问用什么相机拍的效果比较好？','2024-02-20 08:00:00');
/*!40000 ALTER TABLE `forum_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post`
--

DROP TABLE IF EXISTS `forum_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `view_count` int DEFAULT '0',
  `like_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `forum_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post`
--

LOCK TABLES `forum_post` WRITE;
/*!40000 ALTER TABLE `forum_post` DISABLE KEYS */;
INSERT INTO `forum_post` VALUES (1,1,'新手养猫指南：如何照顾你的第一只猫','大家好，我是一名新手铲屎官，最近刚养了一只英短蓝猫。想和大家分享一下我的养猫经验，希望对其他新手有所帮助。首先，要准备好猫粮、猫砂、猫窝等基本用品...',1256,89,'2024-02-01 06:00:00','2024-02-01 06:00:00'),(2,2,'金毛犬的训练心得分享','我家豆豆是一只3岁的金毛，经过一年的训练，现在已经学会了坐下、握手、趴下、等待等基本指令。今天想和大家分享一下我的训练心得...',986,67,'2024-02-02 06:00:00','2024-02-02 06:00:00'),(3,3,'柯基犬的日常护理注意事项','养柯基的朋友都知道，柯基虽然可爱，但是护理起来也有不少注意事项。今天想和大家聊聊柯基的日常护理，包括饮食、运动、毛发护理等方面...',756,45,'2024-02-03 06:00:00','2024-02-03 06:00:00'),(4,4,'猫咪掉毛严重怎么办？','最近我家咪咪掉毛特别严重，家里到处都是猫毛，真的很头疼。有没有什么好的方法可以减少猫咪掉毛呢？求大家支招！',1589,102,'2024-02-04 06:00:00','2024-02-04 06:00:00'),(5,5,'宠物疫苗接种时间表','很多新手铲屎官可能不知道宠物应该在什么时候接种疫苗。今天我整理了一份详细的疫苗接种时间表，希望对大家有所帮助...',2345,156,'2024-02-05 06:00:00','2024-02-05 06:00:00'),(6,6,'如何选择适合自己宠物的猫粮/狗粮','市面上的宠物粮品牌太多了，让人眼花缭乱。今天想和大家聊聊如何选择适合自己宠物的猫粮/狗粮，主要从营养成分、品牌信誉、价格等方面考虑...',1890,134,'2024-02-06 06:00:00','2024-02-06 06:00:00'),(7,7,'拉布拉多犬的性格特点与训练技巧','拉布拉多是非常聪明的犬种，性格温顺，很适合家庭饲养。今天想和大家分享一下拉布拉多的性格特点以及训练技巧...',1123,78,'2024-02-07 06:00:00','2024-02-07 06:00:00'),(8,8,'哈士奇拆家怎么办？','我家毛毛是一只2岁的哈士奇，最近开始疯狂拆家，沙发、鞋子、电线都被咬坏了。有没有什么好的方法可以阻止它拆家？求支招！',2156,189,'2024-02-08 06:00:00','2024-02-08 06:00:00'),(9,9,'宠物绝育的利与弊','关于宠物绝育，一直有很多争议。今天想和大家客观地讨论一下宠物绝育的利与弊，帮助大家做出更好的决定...',1678,145,'2024-02-09 06:00:00','2024-02-09 06:00:00'),(10,10,'如何让猫咪和狗狗和平相处','很多家庭同时养了猫和狗，但是让它们和平相处并不容易。今天想和大家分享一些让猫咪和狗狗和平相处的小技巧...',1345,98,'2024-02-10 06:00:00','2024-02-10 06:00:00'),(11,11,'边境牧羊犬的智商到底有多高？','边牧是公认的最聪明的犬种之一。今天想和大家聊聊边牧的智商到底有多高，以及如何发挥它们的聪明才智...',987,67,'2024-02-11 06:00:00','2024-02-11 06:00:00'),(12,12,'雪纳瑞的美容造型分享','雪纳瑞的造型真的很多样化，今天想和大家分享一些雪纳瑞的美容造型，包括标准造型、泰迪造型、婴儿造型等...',876,56,'2024-02-12 06:00:00','2024-02-12 06:00:00'),(13,13,'柴犬的微笑为什么那么治愈？','柴犬的微笑真的太治愈了！今天想和大家聊聊柴犬为什么会有这样的表情，以及如何让柴犬保持开心...',1567,123,'2024-02-13 06:00:00','2024-02-13 06:00:00'),(14,14,'贵宾犬的毛色护理技巧','贵宾犬的毛色很容易褪色或者变黄，今天想和大家分享一些贵宾犬毛色护理的技巧，让你的贵宾保持漂亮的毛色...',765,45,'2024-02-14 06:00:00','2024-02-14 06:00:00'),(15,15,'加菲猫的饲养注意事项','加菲猫因为脸部扁平，有一些特殊的饲养注意事项。今天想和大家分享一下加菲猫的饲养经验...',654,34,'2024-02-15 06:00:00','2024-02-15 06:00:00'),(16,16,'如何给宠物选择合适的玩具','市面上的宠物玩具太多了，如何选择合适的玩具呢？今天想和大家聊聊如何根据宠物的性格和喜好选择玩具...',543,23,'2024-02-16 06:00:00','2024-02-16 06:00:00'),(17,17,'宠物旅行必备物品清单','想带宠物出去旅行，但是不知道要准备什么？今天我整理了一份宠物旅行必备物品清单，希望对大家有所帮助...',432,12,'2024-02-17 06:00:00','2024-02-17 06:00:00'),(18,18,'折耳猫的健康问题及护理','折耳猫虽然可爱，但是有一些遗传性的健康问题。今天想和大家聊聊折耳猫的健康问题以及日常护理...',321,11,'2024-02-18 06:00:00','2024-02-18 06:00:00'),(19,19,'如何培养宠物的良好习惯','良好的习惯要从小培养，今天想和大家分享一些培养宠物良好习惯的方法，包括定点排便、不乱叫、不咬人等...',2345,167,'2024-02-19 06:00:00','2024-02-19 06:00:00'),(20,20,'宠物摄影技巧分享','想给宠物拍出好看的照片吗？今天想和大家分享一些宠物摄影的技巧，包括光线、角度、构图等方面...',1876,134,'2024-02-20 06:00:00','2024-02-20 06:00:00');
/*!40000 ALTER TABLE `forum_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_person` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `logo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('pending','approved','rejected') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '鍟嗗?璇勫垎',
  `rejection_reason` text COLLATE utf8mb4_unicode_ci COMMENT '瀹℃牳鎷掔粷鍘熷洜',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_merchant_status` (`status`),
  KEY `idx_merchant_rating` (`rating`)
) ENGINE=InnoDB AUTO_INCREMENT=4007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES (1,'萌宠乐园','张经理','13900139001','merchant1@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','北京市朝阳区建国路88号','https://picsum.photos/seed/merchant1/200/200','approved','2024-01-01 02:00:00','2024-01-01 02:00:00',0.00,NULL),(2,'爱宠之家','李经理','13900139002','merchant2@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','上海市浦东新区陆家嘴环路100号','https://picsum.photos/seed/merchant2/200/200','approved','2024-01-02 02:00:00','2024-01-02 02:00:00',0.00,NULL),(3,'宠物天堂','王经理','13900139003','merchant3@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','广州市天河区天河路385号','https://picsum.photos/seed/merchant3/200/200','approved','2024-01-03 02:00:00','2024-01-03 02:00:00',0.00,NULL),(4,'毛孩子宠物店','赵经理','13900139004','merchant4@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','深圳市南山区科技园南路18号','https://picsum.photos/seed/merchant4/200/200','approved','2024-01-04 02:00:00','2024-01-04 02:00:00',0.00,NULL),(5,'快乐宠物坊','孙经理','13900139005','merchant5@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','杭州市西湖区文三路398号','https://picsum.photos/seed/merchant5/200/200','approved','2024-01-05 02:00:00','2024-01-05 02:00:00',0.00,NULL),(6,'宠物世界','周经理','13900139006','merchant6@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','成都市锦江区春熙路88号','https://picsum.photos/seed/merchant6/200/200','approved','2024-01-06 02:00:00','2024-01-06 02:00:00',0.00,NULL),(7,'萌宠驿站','吴经理','13900139007','merchant7@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','武汉市江汉区解放大道688号','https://picsum.photos/seed/merchant7/200/200','pending','2024-01-07 02:00:00','2024-01-07 02:00:00',0.00,NULL),(8,'宠物乐园','郑经理','13900139008','merchant8@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','南京市鼓楼区中山路18号','https://picsum.photos/seed/merchant8/200/200','approved','2024-01-08 02:00:00','2024-01-08 02:00:00',0.00,NULL),(9,'爱宠生活馆','刘经理','13900139009','merchant9@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','西安市雁塔区小寨东路128号','https://picsum.photos/seed/merchant9/200/200','rejected','2024-01-09 02:00:00','2024-01-09 02:00:00',0.00,NULL),(10,'宠物小镇','陈经理','13900139010','merchant10@pet.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','重庆市渝中区解放碑步行街88号','https://picsum.photos/seed/merchant10/200/200','approved','2024-01-10 02:00:00','2024-01-10 02:00:00',0.00,NULL),(101,'爱心宠物美容会所','张经理','13888888881','grooming1@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市朝阳区建国路88号',NULL,'approved','2026-04-22 14:29:10','2026-04-22 14:29:10',0.00,NULL),(102,'宠物健康医院','李医生','13888888882','hospital1@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市海淀区中关村大街1号',NULL,'approved','2026-04-22 14:29:10','2026-04-22 14:29:10',0.00,NULL),(103,'快乐宠物寄养中心','王主管','13888888883','boarding1@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市丰台区丰台路100号',NULL,'approved','2026-04-22 14:29:10','2026-04-22 14:29:10',0.00,NULL),(104,'宠物用品专卖店','赵老板','13888888884','store1@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市西城区西单大街120号',NULL,'approved','2026-04-22 14:29:10','2026-04-22 14:29:10',0.00,NULL),(2001,'爱心宠物美容会所','张经理','13999999991','grooming2@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市朝阳区建国路88号',NULL,'approved','2026-04-22 14:31:59','2026-04-22 14:31:59',0.00,NULL),(2002,'宠物健康医院','李医生','13999999992','hospital2@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市海淀区中关村大街1号',NULL,'approved','2026-04-22 14:31:59','2026-04-22 14:31:59',0.00,NULL),(2003,'快乐宠物寄养中心','王主管','13999999993','boarding2@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市丰台区丰台路100号',NULL,'approved','2026-04-22 14:31:59','2026-04-22 14:31:59',0.00,NULL),(2004,'宠物用品专卖店','赵老板','13999999994','store2@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市西城区西单大街120号',NULL,'approved','2026-04-22 14:31:59','2026-04-22 14:31:59',0.00,NULL),(3001,'爱心宠物美容会所','张经理','13777777771','grooming3@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市朝阳区建国路88号',NULL,'approved','2026-04-22 14:34:03','2026-04-22 14:34:03',0.00,NULL),(3002,'宠物健康医院','李医生','13777777772','hospital3@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市海淀区中关村大街1号',NULL,'approved','2026-04-22 14:34:03','2026-04-22 14:34:03',0.00,NULL),(3003,'快乐宠物寄养中心','王主管','13777777773','boarding3@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市丰台区丰台路100号',NULL,'approved','2026-04-22 14:34:03','2026-04-22 14:34:03',0.00,NULL),(3004,'宠物用品专卖店','赵老板','13777777774','store3@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市西城区西单大街120号',NULL,'approved','2026-04-22 14:34:03','2026-04-22 14:34:03',0.00,NULL),(4001,'爱心宠物美容会所','张经理','13666666661','grooming4@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市朝阳区建国路88号',NULL,'approved','2026-04-22 14:36:04','2026-04-22 14:36:04',0.00,NULL),(4002,'宠物健康医院','李医生','13666666662','hospital4@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市海淀区中关村大街1号',NULL,'approved','2026-04-22 14:36:04','2026-04-22 14:36:04',0.00,NULL),(4003,'快乐宠物寄养中心','王主管','13666666663','boarding4@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市丰台区丰台路100号',NULL,'approved','2026-04-22 14:36:04','2026-04-22 14:36:04',0.00,NULL),(4004,'宠物用品专卖店','赵老板','13666666664','store4@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','北京市西城区西单大街120号',NULL,'approved','2026-04-22 14:36:04','2026-04-22 14:36:04',0.00,NULL),(4005,'测试商家8','测试联系人','13999999998','','$2a$10$xLan.YkHKFXn5y2JDZpRI.cv/6KCdVX5B8tgpd7J91GSO2f0aD0c.','测试地址','','approved','2026-04-22 14:47:45','2026-04-22 14:47:57',0.00,NULL),(4006,'商家13912340001','','13912340001','','$2a$10$M.sQPD8k6TM2GINd2FtaQeIS1mHOBXsniuE/qtn6AZpo2XZYnSy2y','商家13912340001的地址','','approved','2026-04-22 15:21:50','2026-04-22 15:26:24',0.00,NULL);
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_settings`
--

DROP TABLE IF EXISTS `merchant_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appointment_notification` bit(1) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_open` bit(1) NOT NULL,
  `notification_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_notification` bit(1) NOT NULL,
  `review_notification` bit(1) NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `merchant_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3ixetpfe9t8y15y1o411rm2al` (`merchant_id`),
  CONSTRAINT `FKtii8sr13yjdy0k100rig8ymb1` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_settings`
--

LOCK TABLES `merchant_settings` WRITE;
/*!40000 ALTER TABLE `merchant_settings` DISABLE KEYS */;
INSERT INTO `merchant_settings` VALUES (1,_binary '','2026-04-22 16:43:34',_binary '','email',_binary '',_binary '','2026-04-22 16:43:34',4006);
/*!40000 ALTER TABLE `merchant_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` bit(1) NOT NULL,
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `admin_id` int DEFAULT NULL,
  `admin_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `detail` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target_id` int DEFAULT NULL,
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_a7ujv987la0i7a0o91ueevchc` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet`
--

DROP TABLE IF EXISTS `pet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `breed` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` enum('male','female') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `pet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet`
--

LOCK TABLES `pet` WRITE;
/*!40000 ALTER TABLE `pet` DISABLE KEYS */;
INSERT INTO `pet` VALUES (1,1,'小白','狗','萨摩耶',2,'male','https://picsum.photos/seed/pet1/150/150','活泼可爱的小萨摩耶，喜欢玩耍','2024-01-02 02:00:00','2024-01-02 02:00:00'),(2,1,'小黑','猫','英短',1,'female','https://picsum.photos/seed/pet2/150/150','温顺的英短蓝猫，喜欢睡觉','2024-01-03 02:00:00','2024-01-03 02:00:00'),(3,2,'豆豆','狗','金毛',3,'male','https://picsum.photos/seed/pet3/150/150','聪明的金毛寻回犬，非常听话','2024-01-04 02:00:00','2024-01-04 02:00:00'),(4,2,'咪咪','猫','布偶猫',2,'female','https://picsum.photos/seed/pet4/150/150','漂亮的布偶猫，性格温顺','2024-01-05 02:00:00','2024-01-05 02:00:00'),(5,3,'旺财','狗','柯基',2,'male','https://picsum.photos/seed/pet5/150/150','短腿柯基，走路摇摇晃晃','2024-01-06 02:00:00','2024-01-06 02:00:00'),(6,3,'橘子','猫','橘猫',3,'male','https://picsum.photos/seed/pet6/150/150','胖胖的橘猫，特别爱吃','2024-01-07 02:00:00','2024-01-07 02:00:00'),(7,4,'球球','狗','泰迪',1,'female','https://picsum.photos/seed/pet7/150/150','棕色泰迪，毛发卷卷的','2024-01-08 02:00:00','2024-01-08 02:00:00'),(8,4,'花花','猫','三花猫',2,'female','https://picsum.photos/seed/pet8/150/150','三花猫妈妈，很会照顾人','2024-01-09 02:00:00','2024-01-09 02:00:00'),(9,5,'大黄','狗','中华田园犬',4,'male','https://picsum.photos/seed/pet9/150/150','忠诚的中华田园犬','2024-01-10 02:00:00','2024-01-10 02:00:00'),(10,5,'雪球','猫','波斯猫',1,'male','https://picsum.photos/seed/pet10/150/150','白色的波斯猫，眼睛是蓝色的','2024-01-11 02:00:00','2024-01-11 02:00:00'),(11,6,'小宝','狗','比熊',2,'female','https://picsum.photos/seed/pet11/150/150','白色比熊，像棉花糖一样','2024-01-12 02:00:00','2024-01-12 02:00:00'),(12,6,'小美','猫','美短',1,'female','https://picsum.photos/seed/pet12/150/150','银色虎斑美短，非常活泼','2024-01-13 02:00:00','2024-01-13 02:00:00'),(13,7,'阿福','狗','拉布拉多',3,'male','https://picsum.photos/seed/pet13/150/150','黑色的拉布拉多，很聪明','2024-01-14 02:00:00','2024-01-14 02:00:00'),(14,7,'小橘','猫','橘猫',2,'male','https://picsum.photos/seed/pet14/150/150','另一只橘猫，也很胖','2024-01-15 02:00:00','2024-01-15 02:00:00'),(15,8,'毛毛','狗','哈士奇',2,'male','https://picsum.photos/seed/pet15/150/150','二哈，经常拆家','2024-01-16 02:00:00','2024-01-16 02:00:00'),(16,8,'小灰','猫','英短',3,'male','https://picsum.photos/seed/pet16/150/150','灰色英短，很安静','2024-01-17 02:00:00','2024-01-17 02:00:00'),(17,9,'豆包','狗','法斗',1,'male','https://picsum.photos/seed/pet17/150/150','可爱的法斗，喜欢打呼噜','2024-01-18 02:00:00','2024-01-18 02:00:00'),(18,9,'小雪','猫','暹罗猫',2,'female','https://picsum.photos/seed/pet18/150/150','重点色暹罗，很粘人','2024-01-19 02:00:00','2024-01-19 02:00:00'),(19,10,'旺旺','狗','博美',2,'female','https://picsum.photos/seed/pet19/150/150','小巧的博美，叫声很响','2024-01-20 02:00:00','2024-01-20 02:00:00'),(20,10,'小黑','猫','黑猫',1,'male','https://picsum.photos/seed/pet20/150/150','神秘的黑猫，眼睛是绿色的','2024-01-21 02:00:00','2024-01-21 02:00:00'),(21,11,'乐乐','狗','边境牧羊犬',2,'male','https://picsum.photos/seed/pet21/150/150','聪明的边牧，会接飞盘','2024-01-22 02:00:00','2024-01-22 02:00:00'),(22,11,'小蓝','猫','英短蓝猫',1,'male','https://picsum.photos/seed/pet22/150/150','蓝色英短，圆脸很可爱','2024-01-23 02:00:00','2024-01-23 02:00:00'),(23,12,'多多','狗','雪纳瑞',3,'male','https://picsum.photos/seed/pet23/150/150','灰色的雪纳瑞，胡子很长','2024-01-24 02:00:00','2024-01-24 02:00:00'),(24,12,'小花','猫','狸花猫',2,'female','https://picsum.photos/seed/pet24/150/150','狸花猫，花纹很漂亮','2024-01-25 02:00:00','2024-01-25 02:00:00'),(25,13,'皮皮','狗','柴犬',1,'male','https://picsum.photos/seed/pet25/150/150','柴犬，笑起来很治愈','2024-01-26 02:00:00','2024-01-26 02:00:00'),(26,14,'小黄','狗','金毛',2,'male','https://picsum.photos/seed/pet26/150/150','另一只金毛，也很聪明','2024-01-27 02:00:00','2024-01-27 02:00:00'),(27,15,'妞妞','狗','贵宾犬',3,'female','https://picsum.photos/seed/pet27/150/150','粉色贵宾，很爱美','2024-01-28 02:00:00','2024-01-28 02:00:00'),(28,16,'小胖','猫','加菲猫',2,'male','https://picsum.photos/seed/pet28/150/150','加菲猫，脸扁扁的','2024-01-29 02:00:00','2024-01-29 02:00:00'),(29,17,'欢欢','狗','萨摩耶',1,'female','https://picsum.photos/seed/pet29/150/150','另一只萨摩耶，也很白','2024-01-30 02:00:00','2024-01-30 02:00:00'),(30,18,'小乖','猫','折耳猫',2,'female','https://picsum.photos/seed/pet30/150/150','苏格兰折耳猫，耳朵耷拉着','2024-01-31 02:00:00','2024-01-31 02:00:00'),(2001,2001,'小白','狗','萨摩耶',3,'male',NULL,'活泼可爱的萨摩耶','2026-04-22 14:31:59','2026-04-22 14:31:59'),(2002,2001,'小黑','猫','英短',2,'female',NULL,'温顺的英短黑猫','2026-04-22 14:31:59','2026-04-22 14:31:59'),(3001,3001,'小白','狗','萨摩耶',3,'male',NULL,'活泼可爱的萨摩耶','2026-04-22 14:34:03','2026-04-22 14:34:03'),(3002,3001,'小黑','猫','英短',2,'female',NULL,'温顺的英短黑猫','2026-04-22 14:34:03','2026-04-22 14:34:03'),(4001,4001,'小白','狗','萨摩耶',3,'male',NULL,'活泼可爱的萨摩耶','2026-04-22 14:36:04','2026-04-22 14:36:04'),(4002,4001,'小黑','猫','英短',2,'female',NULL,'温顺的英短黑猫','2026-04-22 14:36:04','2026-04-22 14:36:04');
/*!40000 ALTER TABLE `pet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price` decimal(10,2) NOT NULL,
  `stock` int NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `low_stock_threshold` int DEFAULT NULL,
  `status` enum('enabled','disabled') COLLATE utf8mb4_unicode_ci DEFAULT 'enabled',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '鍟嗗搧璇勫垎',
  `sales_volume` int DEFAULT '0' COMMENT '閿?噺',
  PRIMARY KEY (`id`),
  KEY `idx_product_merchant_id` (`merchant_id`),
  KEY `idx_product_status` (`status`),
  KEY `idx_product_rating` (`rating`),
  KEY `idx_product_sales_volume` (`sales_volume`),
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,1,'皇家宠物猫 2kg','法国皇家室内成宠物猫，营养均衡',128.00,100,'https://picsum.photos/seed/product1/300/300','2024-01-02 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(2,1,'皇家宠物狗 2kg','法国皇家小型犬成犬粮',138.00,150,'https://picsum.photos/seed/product2/300/300','2024-01-02 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(3,1,'渴望宠物猫 1.8kg','加拿大渴望六种鱼宠物猫',268.00,80,'https://picsum.photos/seed/product3/300/300','2024-01-02 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(4,1,'渴望宠物狗 2kg','加拿大渴望原味宠物狗',288.00,60,'https://picsum.photos/seed/product4/300/300','2024-01-02 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(5,1,'冠能宠物猫 1.5kg','冠能室内宠物猫，呵护泌尿健康',168.00,120,'https://picsum.photos/seed/product5/300/300','2024-01-02 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(6,2,'宠物自动喂食器','智能定时喂食器，5L大容量',298.00,50,'https://picsum.photos/seed/product6/300/300','2024-01-03 04:00:00','2024-01-03 04:00:00',NULL,NULL,'enabled',0.00,0),(7,2,'宠物饮水机','静音循环过滤饮水机',188.00,80,'https://picsum.photos/seed/product7/300/300','2024-01-03 04:00:00','2024-01-03 04:00:00',NULL,NULL,'enabled',0.00,0),(8,2,'猫砂盆','全封闭猫砂盆，防臭防溅',168.00,60,'https://picsum.photos/seed/product8/300/300','2024-01-03 04:00:00','2024-01-03 04:00:00',NULL,NULL,'enabled',0.00,0),(9,2,'猫爬架','多层猫爬架，带猫窝',388.00,30,'https://picsum.photos/seed/product9/300/300','2024-01-03 04:00:00','2024-01-03 04:00:00',NULL,NULL,'enabled',0.00,0),(10,2,'狗笼','可折叠狗笼，适合中小型犬',258.00,40,'https://picsum.photos/seed/product10/300/300','2024-01-03 04:00:00','2024-01-03 04:00:00',NULL,NULL,'enabled',0.00,0),(11,3,'宠物牵引绳','可伸缩牵引绳，5米',58.00,200,'https://picsum.photos/seed/product11/300/300','2024-01-04 04:00:00','2024-01-04 04:00:00',NULL,NULL,'enabled',0.00,0),(12,3,'宠物项圈','真皮项圈，舒适耐用',38.00,300,'https://picsum.photos/seed/product12/300/300','2024-01-04 04:00:00','2024-01-04 04:00:00',NULL,NULL,'enabled',0.00,0),(13,3,'宠物衣服','冬季保暖衣服，多款可选',68.00,150,'https://picsum.photos/seed/product13/300/300','2024-01-04 04:00:00','2024-01-04 04:00:00',NULL,NULL,'enabled',0.00,0),(14,3,'宠物鞋子','防滑宠物鞋，4只装',48.00,180,'https://picsum.photos/seed/product14/300/300','2024-01-04 04:00:00','2024-01-04 04:00:00',NULL,NULL,'enabled',0.00,0),(15,3,'宠物帽子','可爱造型帽，拍照神器',28.00,250,'https://picsum.photos/seed/product15/300/300','2024-01-04 04:00:00','2024-01-04 04:00:00',NULL,NULL,'enabled',0.00,0),(16,4,'宠物洗毛液','温和无刺激洗毛液，500ml',68.00,100,'https://picsum.photos/seed/product16/300/300','2024-01-05 04:00:00','2024-01-05 04:00:00',NULL,NULL,'enabled',0.00,0),(17,4,'宠物护毛素','滋润护毛素，让毛发柔顺',58.00,100,'https://picsum.photos/seed/product17/300/300','2024-01-05 04:00:00','2024-01-05 04:00:00',NULL,NULL,'enabled',0.00,0),(18,4,'宠物耳液','清洁耳液，预防耳螨',48.00,120,'https://picsum.photos/seed/product18/300/300','2024-01-05 04:00:00','2024-01-05 04:00:00',NULL,NULL,'enabled',0.00,0),(19,4,'宠物眼药水','温和眼药水，清洁眼部',38.00,150,'https://picsum.photos/seed/product19/300/300','2024-01-05 04:00:00','2024-01-05 04:00:00',NULL,NULL,'enabled',0.00,0),(20,4,'宠物牙膏套装','宠物牙膏+牙刷套装',58.00,100,'https://picsum.photos/seed/product20/300/300','2024-01-05 04:00:00','2024-01-05 04:00:00',NULL,NULL,'enabled',0.00,0),(21,5,'猫玩具球','发光猫玩具球，3个装',18.00,500,'https://picsum.photos/seed/product21/300/300','2024-01-06 04:00:00','2024-01-06 04:00:00',NULL,NULL,'enabled',0.00,0),(22,5,'逗猫棒','羽毛逗猫棒，猫咪最爱',15.00,600,'https://picsum.photos/seed/product22/300/300','2024-01-06 04:00:00','2024-01-06 04:00:00',NULL,NULL,'enabled',0.00,0),(23,5,'狗咬胶','天然牛皮狗咬胶，磨牙洁齿',38.00,300,'https://picsum.photos/seed/product23/300/300','2024-01-06 04:00:00','2024-01-06 04:00:00',NULL,NULL,'enabled',0.00,0),(24,5,'飞盘','耐用飞盘，户外玩耍必备',25.00,400,'https://picsum.photos/seed/product24/300/300','2024-01-06 04:00:00','2024-01-06 04:00:00',NULL,NULL,'enabled',0.00,0),(25,5,'宠物毛绒玩具','可爱毛绒玩具，多种款式',35.00,250,'https://picsum.photos/seed/product25/300/300','2024-01-06 04:00:00','2024-01-06 04:00:00',NULL,NULL,'enabled',0.00,0),(26,6,'宠物尿垫','吸水尿垫，50片装',58.00,200,'https://picsum.photos/seed/product26/300/300','2024-01-07 04:00:00','2024-01-07 04:00:00',NULL,NULL,'enabled',0.00,0),(27,6,'猫砂','豆腐猫砂，6L装',68.00,150,'https://picsum.photos/seed/product27/300/300','2024-01-07 04:00:00','2024-01-07 04:00:00',NULL,NULL,'enabled',0.00,0),(28,6,'宠物湿巾','清洁湿巾，80片装',28.00,300,'https://picsum.photos/seed/product28/300/300','2024-01-07 04:00:00','2024-01-07 04:00:00',NULL,NULL,'enabled',0.00,0),(29,6,'宠物拾便袋','可降解拾便袋，15卷装',38.00,250,'https://picsum.photos/seed/product29/300/300','2024-01-07 04:00:00','2024-01-07 04:00:00',NULL,NULL,'enabled',0.00,0),(30,6,'宠物除臭剂','天然除臭剂，500ml',48.00,180,'https://picsum.photos/seed/product30/300/300','2024-01-07 04:00:00','2024-01-07 04:00:00',NULL,NULL,'enabled',0.00,0),(31,8,'宠物营养膏','综合营养膏，120g',88.00,100,'https://picsum.photos/seed/product31/300/300','2024-01-08 04:00:00','2024-01-08 04:00:00',NULL,NULL,'enabled',0.00,0),(32,8,'宠物钙片','补钙片，200片装',68.00,120,'https://picsum.photos/seed/product32/300/300','2024-01-08 04:00:00','2024-01-08 04:00:00',NULL,NULL,'enabled',0.00,0),(33,8,'宠物益生菌','调理肠胃益生菌，30包',98.00,80,'https://picsum.photos/seed/product33/300/300','2024-01-08 04:00:00','2024-01-08 04:00:00',NULL,NULL,'enabled',0.00,0),(34,8,'宠物鱼油','深海鱼油，美毛护肤',128.00,60,'https://picsum.photos/seed/product34/300/300','2024-01-08 04:00:00','2024-01-08 04:00:00',NULL,NULL,'enabled',0.00,0),(35,8,'宠物维生素','复合维生素，100片装',58.00,100,'https://picsum.photos/seed/product35/300/300','2024-01-08 04:00:00','2024-01-08 04:00:00',NULL,NULL,'enabled',0.00,0),(36,10,'宠物窝','保暖宠物窝，可拆洗',168.00,50,'https://picsum.photos/seed/product36/300/300','2024-01-09 04:00:00','2024-01-09 04:00:00',NULL,NULL,'enabled',0.00,0),(37,10,'宠物床','豪华宠物床，舒适透气',258.00,30,'https://picsum.photos/seed/product37/300/300','2024-01-09 04:00:00','2024-01-09 04:00:00',NULL,NULL,'enabled',0.00,0),(38,10,'猫抓板','瓦楞纸猫抓板，保护家具',38.00,200,'https://picsum.photos/seed/product38/300/300','2024-01-09 04:00:00','2024-01-09 04:00:00',NULL,NULL,'enabled',0.00,0),(39,10,'宠物垫子','夏季凉垫，透气舒适',68.00,100,'https://picsum.photos/seed/product39/300/300','2024-01-09 04:00:00','2024-01-09 04:00:00',NULL,NULL,'enabled',0.00,0),(40,10,'宠物毯子','柔软宠物毯，多尺寸可选',48.00,150,'https://picsum.photos/seed/product40/300/300','2024-01-09 04:00:00','2024-01-09 04:00:00',NULL,NULL,'enabled',0.00,0),(41,1,'宠物航空箱','IATA标准航空箱，适合旅行',358.00,40,'https://picsum.photos/seed/product41/300/300','2024-01-10 04:00:00','2024-01-10 04:00:00',NULL,NULL,'enabled',0.00,0),(42,2,'宠物背包','太空舱宠物背包，透气舒适',268.00,60,'https://picsum.photos/seed/product42/300/300','2024-01-11 04:00:00','2024-01-11 04:00:00',NULL,NULL,'enabled',0.00,0),(43,3,'宠物推车','可折叠宠物推车，出行方便',488.00,25,'https://picsum.photos/seed/product43/300/300','2024-01-12 04:00:00','2024-01-12 04:00:00',NULL,NULL,'enabled',0.00,0),(44,4,'宠物梳子','专业宠物梳，去浮毛',38.00,200,'https://picsum.photos/seed/product44/300/300','2024-01-13 04:00:00','2024-01-13 04:00:00',NULL,NULL,'enabled',0.00,0),(45,5,'宠物指甲剪','安全指甲剪，带LED灯',28.00,300,'https://picsum.photos/seed/product45/300/300','2024-01-14 04:00:00','2024-01-14 04:00:00',NULL,NULL,'enabled',0.00,0),(46,6,'宠物剃毛器','静音剃毛器，全身水洗',158.00,80,'https://picsum.photos/seed/product46/300/300','2024-01-15 04:00:00','2024-01-15 04:00:00',NULL,NULL,'enabled',0.00,0),(47,8,'宠物吹风机','专业宠物吹风机，低噪音',268.00,50,'https://picsum.photos/seed/product47/300/300','2024-01-16 04:00:00','2024-01-16 04:00:00',NULL,NULL,'enabled',0.00,0),(48,10,'宠物零食','鸡肉干零食，200g',48.00,200,'https://picsum.photos/seed/product48/300/300','2024-01-17 04:00:00','2024-01-17 04:00:00',NULL,NULL,'enabled',0.00,0),(49,1,'宠物猫罐头','金枪鱼宠物猫罐头，12罐装',88.00,100,'https://picsum.photos/seed/product49/300/300','2024-01-18 04:00:00','2026-04-22 15:05:44',NULL,NULL,'enabled',0.00,0),(50,2,'狗零食','牛肉粒零食，500g',68.00,150,'https://picsum.photos/seed/product50/300/300','2024-01-19 04:00:00','2024-01-19 04:00:00',NULL,NULL,'enabled',0.00,0),(2001,2004,'宠物粮食 成犬专用','高品质成犬粮，营养均衡',258.90,100,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,'enabled',0.00,0),(2002,2004,'宠物玩具 发声球','狗狗喜爱的发声玩具',39.50,200,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,'enabled',0.00,0),(2003,2004,'宠物牵引绳','结实耐用的牵引绳',49.90,150,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,'enabled',0.00,0),(3001,3004,'宠物粮食 成犬专用','高品质成犬粮，营养均衡',258.90,100,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,NULL,'enabled',0.00,0),(3002,3004,'宠物玩具 发声球','狗狗喜爱的发声玩具',39.50,200,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,NULL,'enabled',0.00,0),(3003,3004,'宠物牵引绳','结实耐用的牵引绳',49.90,150,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,NULL,'enabled',0.00,0),(4001,4004,'宠物粮食 成犬专用','高品质成犬粮，营养均衡',258.90,100,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,'enabled',0.00,0),(4002,4004,'宠物玩具 发声球','狗狗喜爱的发声玩具',39.50,200,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,'enabled',0.00,0),(4003,4004,'宠物牵引绳','结实耐用的牵引绳',49.90,150,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,'enabled',0.00,0);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_order`
--

DROP TABLE IF EXISTS `product_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('pending','paid','shipped','completed','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `shipping_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `freight` decimal(10,2) DEFAULT NULL,
  `logistics_company` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logistics_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `pay_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipped_at` datetime(6) DEFAULT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_s3pfxadnwjydoovmdb0ddt95d` (`order_no`),
  KEY `idx_product_order_merchant_id` (`merchant_id`),
  KEY `idx_product_order_user_id` (`user_id`),
  KEY `idx_product_order_status` (`status`),
  KEY `idx_product_order_order_no` (`order_no`),
  CONSTRAINT `product_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `product_order_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_order`
--

LOCK TABLES `product_order` WRITE;
/*!40000 ALTER TABLE `product_order` DISABLE KEYS */;
INSERT INTO `product_order` VALUES (1,1,1,266.00,'completed','北京市朝阳区建国路88号院1号楼101室','2024-02-01 02:00:00','2024-02-05 07:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,1,2,486.00,'completed','北京市朝阳区建国路88号院1号楼101室','2024-02-02 03:00:00','2024-02-06 08:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,2,3,194.00,'completed','上海市浦东新区陆家嘴环路100号金茂大厦','2024-02-03 04:00:00','2024-02-07 09:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,2,4,184.00,'completed','上海市浦东新区陆家嘴环路100号金茂大厦','2024-02-04 05:00:00','2024-02-08 10:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,3,5,93.00,'completed','广州市天河区天河路385号太古汇','2024-02-05 06:00:00','2024-02-09 11:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,3,6,194.00,'completed','广州市天河区天河路385号太古汇','2024-02-06 07:00:00','2024-02-10 12:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,4,8,284.00,'completed','深圳市南山区科技园南路18号腾讯大厦','2024-02-07 08:00:00','2024-02-11 13:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,4,10,274.00,'completed','深圳市南山区科技园南路18号腾讯大厦','2024-02-08 09:00:00','2024-02-12 14:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(9,5,1,426.00,'completed','杭州市西湖区文三路398号东信大厦','2024-02-09 10:00:00','2024-02-13 15:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,5,2,456.00,'completed','杭州市西湖区文三路398号东信大厦','2024-02-10 11:00:00','2024-02-14 15:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,6,3,88.00,'completed','成都市锦江区春熙路88号IFS国际金融中心','2024-02-11 12:00:00','2024-02-15 15:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(12,6,4,106.00,'completed','成都市锦江区春熙路88号IFS国际金融中心','2024-02-12 13:00:00','2024-02-16 15:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,7,5,60.00,'shipped','武汉市江汉区解放大道688号武汉广场','2024-02-13 14:00:00','2024-02-18 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(14,7,6,134.00,'shipped','武汉市江汉区解放大道688号武汉广场','2024-02-14 15:00:00','2024-02-19 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(15,8,8,196.00,'shipped','南京市鼓楼区中山路18号德基广场','2024-02-15 02:00:00','2024-02-20 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(16,8,10,216.00,'shipped','南京市鼓楼区中山路18号德基广场','2024-02-16 03:00:00','2024-02-21 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(17,9,1,138.00,'paid','西安市雁塔区小寨东路128号赛格国际','2024-02-17 04:00:00','2024-02-17 04:30:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(18,9,2,188.00,'paid','西安市雁塔区小寨东路128号赛格国际','2024-02-18 05:00:00','2024-02-18 05:30:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(19,10,3,48.00,'paid','重庆市渝中区解放碑步行街88号环球金融中心','2024-02-19 06:00:00','2024-02-19 06:30:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(20,10,4,68.00,'paid','重庆市渝中区解放碑步行街88号环球金融中心','2024-02-20 07:00:00','2024-02-20 07:30:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,11,5,35.00,'pending','北京市海淀区中关村大街1号海龙大厦','2024-02-21 08:00:00','2024-02-21 08:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,11,6,58.00,'pending','北京市海淀区中关村大街1号海龙大厦','2024-02-22 09:00:00','2024-02-22 09:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,12,8,88.00,'pending','上海市徐汇区漕溪北路88号圣爱广场','2024-02-23 10:00:00','2024-02-23 10:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,12,10,48.00,'pending','上海市徐汇区漕溪北路88号圣爱广场','2024-02-24 11:00:00','2024-02-24 11:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,13,1,268.00,'cancelled','广州市越秀区中山五路219号中旅商业城','2024-02-01 12:00:00','2024-02-02 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,14,2,298.00,'cancelled','深圳市福田区福华三路88号财富大厦','2024-02-02 13:00:00','2024-02-03 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,15,3,25.00,'cancelled','杭州市上城区延安路98号银泰百货','2024-02-03 14:00:00','2024-02-04 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(28,16,4,58.00,'cancelled','成都市武侯区人民南路四段1号来福士广场','2024-02-04 15:00:00','2024-02-05 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(29,17,5,15.00,'cancelled','武汉市武昌区中南路1号中商广场','2024-02-05 02:00:00','2024-02-06 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(30,18,6,68.00,'cancelled','南京市玄武区中山路18号德基广场','2024-02-06 03:00:00','2024-02-07 02:00:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2001,2001,2004,258.90,'pending','北京市朝阳区建国路88号','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,NULL,NULL,NULL,'PS202401200001',NULL,NULL,NULL,NULL,NULL),(2002,2001,2004,79.00,'paid','北京市朝阳区建国路88号','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,NULL,NULL,NULL,'PS202401190002',NULL,NULL,NULL,NULL,NULL),(2003,2001,2004,49.90,'shipped','北京市朝阳区建国路88号','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,NULL,NULL,NULL,'PS202401180003',NULL,NULL,NULL,NULL,NULL),(2004,2001,2004,129.90,'completed','北京市朝阳区建国路88号','2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,NULL,NULL,NULL,NULL,'PS202401150004',NULL,NULL,NULL,NULL,NULL),(4001,4001,4004,258.90,'pending','北京市朝阳区建国路88号','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,NULL,NULL,NULL,'PS202404200001',NULL,NULL,NULL,NULL,NULL),(4002,4001,4004,79.00,'paid','北京市朝阳区建国路88号','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,NULL,NULL,NULL,'PS202404190002',NULL,NULL,NULL,NULL,NULL),(4003,4001,4004,49.90,'shipped','北京市朝阳区建国路88号','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,NULL,NULL,NULL,'PS202404180003',NULL,NULL,NULL,NULL,NULL),(4004,4001,4004,129.90,'completed','北京市朝阳区建国路88号','2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,NULL,NULL,NULL,NULL,'PS202404150004',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `product_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_order_item`
--

DROP TABLE IF EXISTS `product_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_order_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `product_order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `product_order` (`id`),
  CONSTRAINT `product_order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_order_item`
--

LOCK TABLES `product_order_item` WRITE;
/*!40000 ALTER TABLE `product_order_item` DISABLE KEYS */;
INSERT INTO `product_order_item` VALUES (1,1,1,1,128.00),(2,1,5,1,138.00),(3,2,6,1,298.00),(4,2,7,1,188.00),(5,3,11,2,58.00),(6,3,12,2,38.00),(7,4,16,1,68.00),(8,4,17,1,58.00),(9,4,18,1,48.00),(10,5,21,2,18.00),(11,5,22,2,15.00),(12,5,23,1,38.00),(13,6,26,1,58.00),(14,6,27,1,68.00),(15,6,28,2,28.00),(16,7,31,2,88.00),(17,7,32,1,68.00),(18,7,33,1,98.00),(19,8,36,1,168.00),(20,8,38,1,38.00),(21,8,39,1,68.00),(22,9,3,1,268.00),(23,9,4,1,288.00),(24,9,49,1,88.00),(25,10,41,1,358.00),(26,10,42,1,268.00),(27,10,50,1,68.00),(28,11,13,1,68.00),(29,11,14,1,48.00),(30,12,19,1,38.00),(31,12,20,1,58.00),(32,12,45,1,28.00),(33,13,24,1,25.00),(34,13,25,1,35.00),(35,14,28,1,28.00),(36,14,29,1,38.00),(37,14,30,1,48.00),(38,14,48,1,48.00),(39,15,34,1,128.00),(40,15,35,1,58.00),(41,15,50,1,68.00),(42,16,37,1,258.00),(43,16,40,1,48.00),(44,16,49,1,88.00),(45,17,2,1,138.00),(46,18,8,1,168.00),(47,18,48,1,48.00),(48,19,15,1,28.00),(49,19,21,1,18.00),(50,20,16,1,68.00),(51,21,22,1,15.00),(52,21,23,1,38.00),(53,21,45,1,28.00),(54,22,26,1,58.00),(55,23,31,1,88.00),(56,24,48,1,48.00),(57,25,3,1,268.00),(58,26,6,1,298.00),(59,27,24,1,25.00),(60,28,28,2,28.00),(2001,2001,2001,1,258.90),(2002,2002,2002,2,39.50),(2003,2003,2003,1,49.90),(2004,2004,2001,1,129.90),(4001,4001,4001,1,258.90),(4002,4002,4002,2,39.50),(4003,4003,4003,1,49.90),(4004,4004,4001,1,129.90);
/*!40000 ALTER TABLE `product_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `merchant_id` int NOT NULL,
  `service_id` int NOT NULL,
  `appointment_id` int NOT NULL,
  `rating` int NOT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `reply_content` text COLLATE utf8mb4_unicode_ci,
  `reply_time` timestamp NULL DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '璇勪环鍥剧墖',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `appointment_id` (`appointment_id`),
  KEY `idx_review_merchant_id` (`merchant_id`),
  KEY `idx_review_service_id` (`service_id`),
  KEY `idx_review_rating` (`rating`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`),
  CONSTRAINT `review_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `review_ibfk_4` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`),
  CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=4010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,1,1,1,1,5,'服务非常好，洗得很干净，工作人员很专业！','2024-02-01 04:30:00',NULL,NULL,NULL,NULL),(2,1,1,2,2,5,'造型设计得很漂亮，我家小白现在更可爱了！','2024-02-05 08:30:00',NULL,NULL,NULL,NULL),(3,2,2,4,3,4,'体检很全面，医生也很专业，就是等了挺久。','2024-02-03 03:30:00',NULL,NULL,NULL,NULL),(4,2,2,5,4,5,'打疫苗很顺利，医生很温柔，豆豆没有害怕。','2024-02-10 03:30:00',NULL,NULL,NULL,NULL),(5,3,3,7,5,5,'训练效果很好，旺财现在会坐下和握手了！','2024-02-08 08:30:00',NULL,NULL,NULL,NULL),(6,3,4,9,6,4,'SPA服务不错，橘子很享受，就是价格有点贵。','2024-02-12 09:30:00',NULL,NULL,NULL,NULL),(7,4,5,12,7,5,'摄影师很专业，拍的照片很漂亮，值得推荐！','2024-02-15 05:30:00',NULL,NULL,NULL,NULL),(8,5,6,14,8,4,'接送服务很准时，司机师傅很热情。','2024-02-18 02:30:00',NULL,NULL,NULL,NULL),(9,6,8,17,9,5,'按摩师手法很好，小宝做完很放松。','2024-02-20 08:30:00',NULL,NULL,NULL,NULL),(10,7,10,19,10,4,'游泳设施不错，阿福玩得很开心。','2024-02-22 03:30:00',NULL,NULL,NULL,NULL),(11,1,2,4,3,5,'第二次来体检了，服务一如既往的好！','2024-02-10 04:00:00',NULL,NULL,NULL,NULL),(12,2,3,7,5,4,'训练师很有耐心，效果不错。','2024-02-15 04:00:00',NULL,NULL,NULL,NULL),(13,3,4,9,6,5,'SPA环境很好，服务也很周到。','2024-02-20 04:00:00',NULL,NULL,NULL,NULL),(14,4,5,12,7,5,'照片拍得很美，后期修图也很自然。','2024-02-25 04:00:00',NULL,NULL,NULL,NULL),(15,5,6,14,8,4,'接送服务方便快捷，省了不少心。','2024-03-01 04:00:00',NULL,NULL,NULL,NULL),(16,6,8,17,9,5,'按摩服务很专业，下次还会来。','2024-03-05 04:00:00',NULL,NULL,NULL,NULL),(17,7,10,19,10,4,'游泳池水质很好，环境也很干净。','2024-03-10 04:00:00',NULL,NULL,NULL,NULL),(18,8,1,1,11,5,'洗澡服务很棒，毛毛现在香香的。','2024-03-15 04:00:00',NULL,NULL,NULL,NULL),(19,9,2,4,12,4,'体检报告很详细，医生解释得很清楚。','2024-03-20 04:00:00',NULL,NULL,NULL,NULL),(20,10,3,7,13,5,'训练效果显著，旺旺进步很大。','2024-03-25 04:00:00',NULL,NULL,NULL,NULL),(21,11,4,9,14,4,'SPA服务不错，环境很舒适。','2024-03-30 04:00:00',NULL,NULL,NULL,NULL),(22,12,5,12,15,5,'摄影团队很专业，照片质量很高。','2024-04-01 04:00:00',NULL,NULL,NULL,NULL),(23,1,3,7,5,5,'第三次来训练了，效果越来越好。','2024-04-05 04:00:00',NULL,NULL,NULL,NULL),(24,2,4,9,6,4,'SPA服务很享受，就是预约有点难。','2024-04-10 04:00:00',NULL,NULL,NULL,NULL),(25,3,5,12,7,5,'照片拍得很有创意，非常喜欢！','2024-04-15 04:00:00',NULL,NULL,NULL,NULL),(26,4,6,14,8,4,'接送服务准时，态度也很好。','2024-04-20 04:00:00',NULL,NULL,NULL,NULL),(27,5,8,17,9,5,'按摩手法很专业，小宝很享受。','2024-04-25 04:00:00',NULL,NULL,NULL,NULL),(28,6,10,19,10,4,'游泳设施齐全，安全措施做得很好。','2024-04-30 04:00:00',NULL,NULL,NULL,NULL),(29,7,1,2,2,5,'美容服务一如既往的好！','2024-05-01 04:00:00',NULL,NULL,NULL,NULL),(30,8,2,5,4,4,'疫苗服务很专业，下次还会来。','2024-05-05 04:00:00',NULL,NULL,NULL,NULL),(4001,4001,4001,4001,4003,5,'服务非常好，狗狗很喜欢','2026-04-22 14:36:04',NULL,NULL,NULL,NULL),(4002,4001,4002,4002,4002,4,'医生很专业，检查很仔细','2026-04-22 14:36:04',NULL,NULL,NULL,NULL),(4003,4001,4003,4003,4003,5,'寄养环境很好，狗狗很开心','2026-04-22 14:36:04',NULL,NULL,NULL,NULL),(4007,1,4006,4004,4005,5,'服务非常好！','2026-04-22 15:59:36',NULL,NULL,NULL,NULL),(4008,2,4006,4005,4006,4,'效果不错','2026-04-22 15:59:36',NULL,NULL,NULL,NULL),(4009,3,4006,4006,4007,5,'很放心在这里寄养','2026-04-22 15:59:36',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `role_id` int NOT NULL,
  `permission_id` int NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKa6jx8n8xkesmjmv6jqug6bg68` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKf8yllw1ecvwqy3ehyxawqa1qp` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduled_task`
--

DROP TABLE IF EXISTS `scheduled_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduled_task` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `cron_expression` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `last_execute_time` timestamp NULL DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `next_execute_time` timestamp NULL DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduled_task`
--

LOCK TABLES `scheduled_task` WRITE;
/*!40000 ALTER TABLE `scheduled_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `scheduled_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `search_history`
--

DROP TABLE IF EXISTS `search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `search_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `keyword` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_created` (`user_id`,`created_at` DESC),
  CONSTRAINT `FKp5pug3072mbsc7bwdt1mrtbl6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `search_history`
--

LOCK TABLES `search_history` WRITE;
/*!40000 ALTER TABLE `search_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `search_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price` decimal(10,2) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '鏈嶅姟璇勫垎',
  `reservation_count` int DEFAULT '0' COMMENT '棰勭害娆℃暟',
  PRIMARY KEY (`id`),
  KEY `idx_service_merchant_id` (`merchant_id`),
  KEY `idx_service_status` (`status`),
  KEY `idx_service_rating` (`rating`),
  CONSTRAINT `service_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,1,'宠物洗澡','专业宠物洗澡服务，包含吹干、梳理',88.00,60,'https://picsum.photos/seed/service1/300/200','2024-01-02 03:00:00','2024-01-02 03:00:00',NULL,NULL,0.00,0),(2,1,'宠物美容','专业造型设计，让您的宠物焕然一新',168.00,120,'https://picsum.photos/seed/service2/300/200','2024-01-02 03:00:00','2024-01-02 03:00:00',NULL,NULL,0.00,0),(3,1,'宠物寄养','舒适环境，专人照顾，让您安心出行',128.00,1440,'https://picsum.photos/seed/service3/300/200','2024-01-02 03:00:00','2024-01-02 03:00:00',NULL,NULL,0.00,0),(4,2,'宠物体检','全面健康检查，预防疾病',298.00,90,'https://picsum.photos/seed/service4/300/200','2024-01-03 03:00:00','2024-01-03 03:00:00',NULL,NULL,0.00,0),(5,2,'疫苗接种','正规疫苗，专业注射',128.00,30,'https://picsum.photos/seed/service5/300/200','2024-01-03 03:00:00','2024-01-03 03:00:00',NULL,NULL,0.00,0),(6,2,'驱虫服务','体内外驱虫，保护宠物健康',98.00,30,'https://picsum.photos/seed/service6/300/200','2024-01-03 03:00:00','2024-01-03 03:00:00',NULL,NULL,0.00,0),(7,3,'宠物训练','基础服从训练，纠正不良行为',388.00,60,'https://picsum.photos/seed/service7/300/200','2024-01-04 03:00:00','2024-01-04 03:00:00',NULL,NULL,0.00,0),(8,3,'行为矫正','专业行为矫正，解决宠物行为问题',488.00,90,'https://picsum.photos/seed/service8/300/200','2024-01-04 03:00:00','2024-01-04 03:00:00',NULL,NULL,0.00,0),(9,4,'宠物SPA','精油SPA，放松身心',268.00,90,'https://picsum.photos/seed/service9/300/200','2024-01-05 03:00:00','2024-01-05 03:00:00',NULL,NULL,0.00,0),(10,4,'指甲修剪','专业指甲修剪，防止抓伤',38.00,20,'https://picsum.photos/seed/service10/300/200','2024-01-05 03:00:00','2024-01-05 03:00:00',NULL,NULL,0.00,0),(11,4,'耳道清洁','专业耳道清洁，预防耳部疾病',58.00,20,'https://picsum.photos/seed/service11/300/200','2024-01-05 03:00:00','2024-01-05 03:00:00',NULL,NULL,0.00,0),(12,5,'宠物摄影','专业宠物摄影，记录美好瞬间',588.00,120,'https://picsum.photos/seed/service12/300/200','2024-01-06 03:00:00','2024-01-06 03:00:00',NULL,NULL,0.00,0),(13,5,'宠物婚介','为您的宠物寻找另一半',288.00,60,'https://picsum.photos/seed/service13/300/200','2024-01-06 03:00:00','2024-01-06 03:00:00',NULL,NULL,0.00,0),(14,6,'宠物接送','专车接送，安全便捷',68.00,60,'https://picsum.photos/seed/service14/300/200','2024-01-07 03:00:00','2024-01-07 03:00:00',NULL,NULL,0.00,0),(15,6,'上门服务','专业上门服务，方便快捷',188.00,90,'https://picsum.photos/seed/service15/300/200','2024-01-07 03:00:00','2024-01-07 03:00:00',NULL,NULL,0.00,0),(16,6,'宠物日托','白天托管，晚上接回',98.00,480,'https://picsum.photos/seed/service16/300/200','2024-01-07 03:00:00','2024-01-07 03:00:00',NULL,NULL,0.00,0),(17,8,'宠物按摩','专业按摩，缓解疲劳',168.00,60,'https://picsum.photos/seed/service17/300/200','2024-01-08 03:00:00','2024-01-08 03:00:00',NULL,NULL,0.00,0),(18,8,'牙齿清洁','专业洗牙，口腔护理',198.00,45,'https://picsum.photos/seed/service18/300/200','2024-01-08 03:00:00','2024-01-08 03:00:00',NULL,NULL,0.00,0),(19,10,'宠物游泳','室内恒温泳池，锻炼身体',128.00,60,'https://picsum.photos/seed/service19/300/200','2024-01-09 03:00:00','2024-01-09 03:00:00',NULL,NULL,0.00,0),(20,10,'宠物瑜伽','与宠物一起做瑜伽，增进感情',168.00,60,'https://picsum.photos/seed/service20/300/200','2024-01-09 03:00:00','2024-01-09 03:00:00',NULL,NULL,0.00,0),(21,1,'宠物造型','创意造型设计，个性十足',228.00,150,'https://picsum.photos/seed/service21/300/200','2024-01-10 03:00:00','2024-01-10 03:00:00',NULL,NULL,0.00,0),(22,2,'绝育手术','专业绝育手术，安全可靠',888.00,120,'https://picsum.photos/seed/service22/300/200','2024-01-11 03:00:00','2024-01-11 03:00:00',NULL,NULL,0.00,0),(23,3,'宠物保险咨询','专业宠物保险咨询服务',0.00,30,'https://picsum.photos/seed/service23/300/200','2024-01-12 03:00:00','2024-01-12 03:00:00',NULL,NULL,0.00,0),(24,4,'宠物营养咨询','专业营养师一对一咨询',98.00,45,'https://picsum.photos/seed/service24/300/200','2024-01-13 03:00:00','2024-01-13 03:00:00',NULL,NULL,0.00,0),(25,5,'宠物生日派对','为您的宠物举办生日派对',688.00,180,'https://picsum.photos/seed/service25/300/200','2024-01-14 03:00:00','2024-01-14 03:00:00',NULL,NULL,0.00,0),(26,6,'宠物殡葬服务','体面的告别仪式',1288.00,240,'https://picsum.photos/seed/service26/300/200','2024-01-15 03:00:00','2024-01-15 03:00:00',NULL,NULL,0.00,0),(27,8,'宠物心理咨询','专业宠物心理咨询服务',188.00,60,'https://picsum.photos/seed/service27/300/200','2024-01-16 03:00:00','2024-01-16 03:00:00',NULL,NULL,0.00,0),(28,10,'宠物服装定制','量身定制宠物服装',288.00,180,'https://picsum.photos/seed/service28/300/200','2024-01-17 03:00:00','2024-01-17 03:00:00',NULL,NULL,0.00,0),(29,1,'宠物接送机','机场接送服务',188.00,120,'https://picsum.photos/seed/service29/300/200','2024-01-18 03:00:00','2024-01-18 03:00:00',NULL,NULL,0.00,0),(30,2,'宠物急救','24小时宠物急救服务',388.00,60,'https://picsum.photos/seed/service30/300/200','2024-01-19 03:00:00','2024-01-19 03:00:00',NULL,NULL,0.00,0),(2001,2001,'宠物洗澡美容套餐','包含洗澡、剪毛、修指甲等服务',88.00,90,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,'1',0.00,0),(2002,2002,'宠物健康体检','全面的宠物健康检查',150.00,60,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,'1',0.00,0),(2003,2003,'宠物寄养服务','提供舒适的寄养环境',50.00,1440,NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59',NULL,'1',0.00,0),(3001,3001,'宠物洗澡美容套餐','包含洗澡、剪毛、修指甲等服务',88.00,90,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,'1',0.00,0),(3002,3002,'宠物健康体检','全面的宠物健康检查',150.00,60,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,'1',0.00,0),(3003,3003,'宠物寄养服务','提供舒适的寄养环境',50.00,1440,NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03',NULL,'1',0.00,0),(4001,4001,'宠物洗澡美容套餐','包含洗澡、剪毛、修指甲等服务',88.00,90,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,'1',0.00,0),(4002,4002,'宠物健康体检','全面的宠物健康检查',150.00,60,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,'1',0.00,0),(4003,4003,'宠物寄养服务','提供舒适的寄养环境',50.00,1440,NULL,'2026-04-22 14:36:04','2026-04-22 14:36:04',NULL,'1',0.00,0),(4004,4006,'宠物洗澡','专业宠物洗澡服务',50.00,60,NULL,'2026-04-22 15:57:49','2026-04-22 15:57:49',NULL,'enabled',5.00,10),(4005,4006,'宠物美容','专业宠物美容造型',80.00,90,NULL,'2026-04-22 15:57:49','2026-04-22 15:57:49',NULL,'enabled',4.50,5),(4006,4006,'宠物寄养','安全舒适的宠物寄养服务',100.00,1440,NULL,'2026-04-22 15:57:49','2026-04-22 15:57:49',NULL,'enabled',4.00,3);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `id` int NOT NULL AUTO_INCREMENT,
  `contact_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `footer_text` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icp_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `site_description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `site_keywords` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `site_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `alipay_app_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alipay_notify_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alipay_private_key` text COLLATE utf8mb4_unicode_ci,
  `alipay_public_key` text COLLATE utf8mb4_unicode_ci,
  `allowed_file_types` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `email_from` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_port` int DEFAULT NULL,
  `email_smtp` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_file_size` bigint DEFAULT NULL,
  `sms_api_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sms_api_secret` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sms_provider` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sms_sign_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `upload_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_app_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_app_secret` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_mch_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat_pay_cert` text COLLATE utf8mb4_unicode_ci,
  `wechat_pay_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_settings`
--

LOCK TABLES `system_settings` WRITE;
/*!40000 ALTER TABLE `system_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` enum('active','disabled') COLLATE utf8mb4_unicode_ci DEFAULT 'active',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=4004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'张三','zhangsan@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138001','https://picsum.photos/seed/user1/100/100','2024-01-01 01:00:00','2024-01-01 01:00:00','active'),(2,'李四','lisi@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138002','https://picsum.photos/seed/user2/100/100','2024-01-02 01:00:00','2024-01-02 01:00:00','active'),(3,'王五','wangwu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138003','https://picsum.photos/seed/user3/100/100','2024-01-03 01:00:00','2024-01-03 01:00:00','active'),(4,'赵六','zhaoliu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138004','https://picsum.photos/seed/user4/100/100','2024-01-04 01:00:00','2024-01-04 01:00:00','active'),(5,'孙七','sunqi@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138005','https://picsum.photos/seed/user5/100/100','2024-01-05 01:00:00','2024-01-05 01:00:00','active'),(6,'周八','zhouba@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138006','https://picsum.photos/seed/user6/100/100','2024-01-06 01:00:00','2024-01-06 01:00:00','active'),(7,'吴九','wujiu@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138007','https://picsum.photos/seed/user7/100/100','2024-01-07 01:00:00','2024-01-07 01:00:00','active'),(8,'郑十','zhengshi@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138008','https://picsum.photos/seed/user8/100/100','2024-01-08 01:00:00','2024-01-08 01:00:00','active'),(9,'刘明','liuming@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138009','https://picsum.photos/seed/user9/100/100','2024-01-09 01:00:00','2024-01-09 01:00:00','active'),(10,'陈红','chenhong@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138010','https://picsum.photos/seed/user10/100/100','2024-01-10 01:00:00','2024-01-10 01:00:00','active'),(11,'杨洋','yangyang@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138011','https://picsum.photos/seed/user11/100/100','2024-01-11 01:00:00','2024-01-11 01:00:00','active'),(12,'黄丽','huangli@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138012','https://picsum.photos/seed/user12/100/100','2024-01-12 01:00:00','2024-01-12 01:00:00','active'),(13,'林峰','linfeng@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138013','https://picsum.photos/seed/user13/100/100','2024-01-13 01:00:00','2024-01-13 01:00:00','active'),(14,'何芳','hefang@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138014','https://picsum.photos/seed/user14/100/100','2024-01-14 01:00:00','2024-01-14 01:00:00','active'),(15,'马超','machao@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138015','https://picsum.photos/seed/user15/100/100','2024-01-15 01:00:00','2024-01-15 01:00:00','active'),(16,'高洁','gaojie@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138016','https://picsum.photos/seed/user16/100/100','2024-01-16 01:00:00','2024-01-16 01:00:00','active'),(17,'罗伟','luowei@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138017','https://picsum.photos/seed/user17/100/100','2024-01-17 01:00:00','2024-01-17 01:00:00','active'),(18,'梁静','liangjing@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138018','https://picsum.photos/seed/user18/100/100','2024-01-18 01:00:00','2024-01-18 01:00:00','active'),(19,'宋强','songqiang@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138019','https://picsum.photos/seed/user19/100/100','2024-01-19 01:00:00','2024-01-19 01:00:00','active'),(20,'谢敏','xiemin@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13800138020','https://picsum.photos/seed/user20/100/100','2024-01-20 01:00:00','2024-01-20 01:00:00','active'),(26,'用户一','user1@qq.com','$2a$10$rD9D.p77foidqn0qHb0H6OB0hgIi9fkZXTnM/XkNeUDOSEDysr.2G','13412340001',NULL,'2026-04-20 16:17:45','2026-04-20 16:17:45','active'),(28,'13900139001','','$2a$10$i6lXN2dv339Q/b.MwxXIcugTtHGnN6Jr/q1BwIRuH2kgfMQzxpjbm','13900139001',NULL,'2026-04-20 17:17:59','2026-04-20 17:17:59','active'),(31,'用户13412340002','','$2a$10$jcN0fkzbN8KncBeN5XrhX.gwcKSutw4/204Jx6Nf3Fyp8/7c8MCI.','13412340002',NULL,'2026-04-20 17:45:36','2026-04-20 17:45:36','active'),(2001,'测试用户','user2@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','13999999995',NULL,'2026-04-22 14:31:59','2026-04-22 14:31:59','active'),(3001,'测试用户','user3@example.com','$2a$10$eS5vZq7s8j5f6g7h8i9j0k','13777777775',NULL,'2026-04-22 14:34:03','2026-04-22 14:34:03','active'),(4001,'测试用户','user4@example.com','.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH','13666666665',NULL,'2026-04-22 14:36:04','2026-04-22 14:39:09','active'),(4002,'测试用户999','','$2a$10$TdqCb5lw2UPBuubBhhIEiu1zJ6XCfhhe4wVfy0bRfuBSmW5B8RZ8i','13999999999',NULL,'2026-04-22 14:46:00','2026-04-22 14:46:00','active'),(4003,'TestAdmin','','$2a$10$XIhTckH2R7h1ty/yq7hNAus/f/maHTq1Pp2wWwuomghLJix6kLBYe','13900000001',NULL,'2026-04-22 15:35:22','2026-04-22 15:35:22','active');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-23 20:51:01
