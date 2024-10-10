-- MySQL dump 10.13  Distrib 8.0.38, for macos14 (arm64)
--
-- Host: 127.0.0.1    Database: pizzare
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `Order_ID` int NOT NULL,
  `Customer_ID` int NOT NULL,
  `Total_Amount` decimal(5,2) DEFAULT NULL,
  `Order_Date` date DEFAULT NULL,
  `DeliveryDriver_ID` int DEFAULT NULL,
  `Order_Status` varchar(50) DEFAULT NULL,
  `Order_StartTime` time DEFAULT NULL,
  `Order_ETA` time DEFAULT NULL,
  `Order_EndTime` time DEFAULT NULL,
  `Batch_ID` int DEFAULT NULL,
  `Postcode` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`Order_ID`),
  KEY `Customer_ID` (`Customer_ID`),
  KEY `DeliveryDriver_ID` (`DeliveryDriver_ID`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`Customer_ID`) REFERENCES `customers` (`Customer_ID`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`DeliveryDriver_ID`) REFERENCES `deliverydrivers` (`DeliveryDriver_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (25310218,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(38618256,2,0.42,'2024-10-09',1,'Order Confirmed','18:26:25',NULL,NULL,NULL,NULL),(71476980,2,6.10,'2024-10-09',1,'Canceled','13:59:36',NULL,'13:59:38',NULL,NULL),(87164578,2,0.31,'2024-10-10',3,'Canceled','09:02:36',NULL,'09:03:02',NULL,NULL),(100577386,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(124909637,2,6.10,'2024-10-09',1,'Order Confirmed',NULL,NULL,NULL,1829077909,'6211'),(240006486,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(241194452,2,61.04,'2024-10-09',1,'Order Confirmed','16:58:32',NULL,NULL,NULL,NULL),(276631028,2,6.10,'2024-10-09',1,'Order Confirmed','17:00:02',NULL,NULL,NULL,NULL),(281528356,2,3.05,'2024-10-09',NULL,'Order Confirmed','18:55:22',NULL,NULL,NULL,NULL),(293171485,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(368998201,2,3.05,'2024-10-09',1,'Canceled','15:46:38',NULL,'15:46:41',NULL,NULL),(377427536,2,6.10,'2024-10-09',1,'Order Confirmed','12:55:08',NULL,NULL,NULL,NULL),(377577305,2,10.68,'2024-10-09',1,'Order Confirmed','12:35:05',NULL,NULL,NULL,NULL),(381326802,2,14.92,'2024-10-09',1,'Order Confirmed','12:48:29',NULL,NULL,NULL,NULL),(425896967,2,3.05,'2024-10-09',1,'Canceled','15:50:50',NULL,'15:50:55',NULL,NULL),(454792736,2,10.68,'2024-10-09',NULL,'Canceled','19:07:31',NULL,'19:07:33',NULL,NULL),(500619292,2,6.10,'2024-10-09',2,'Order Confirmed','21:08:05',NULL,NULL,NULL,NULL),(514872783,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(533841761,2,16.74,'2024-10-09',1,'Order Confirmed',NULL,NULL,NULL,514977534,'6211'),(543331904,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(657396725,2,3.90,'2024-10-09',NULL,'Canceled','19:50:26',NULL,'19:50:28',NULL,NULL),(664789782,2,0.31,'2024-10-09',1,'Canceled','13:53:42',NULL,'13:53:47',NULL,NULL),(778072043,2,3.05,'2024-10-09',5,'Order Confirmed','21:14:48',NULL,NULL,NULL,NULL),(792996499,2,10.68,'2024-10-09',1,'Canceled','13:12:49',NULL,'13:13:19',NULL,NULL),(1002190061,2,55.39,'2024-10-09',1,'Order Received','03:50:52',NULL,NULL,NULL,NULL),(1010895774,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1023505538,2,16.79,'2024-10-09',1,'Order Confirmed','23:30:21',NULL,NULL,NULL,NULL),(1035776511,2,11.60,'2024-10-09',NULL,'Order Confirmed','18:38:25',NULL,NULL,NULL,NULL),(1044222381,2,10.68,'2024-10-09',1,'Order Confirmed','11:56:29',NULL,NULL,NULL,NULL),(1077873878,2,0.95,'2024-10-09',1,'Canceled','13:56:52',NULL,'13:56:54',NULL,NULL),(1078380496,2,3.05,'2024-10-09',NULL,'Order Confirmed','18:41:23',NULL,NULL,NULL,NULL),(1089585298,2,3.05,'2024-10-09',2,'Order Confirmed','23:36:22',NULL,NULL,NULL,NULL),(1188298130,2,3.05,'2024-10-09',1,'Order Confirmed',NULL,NULL,NULL,612948759,'6211'),(1196232591,2,0.61,'2024-10-09',1,'Canceled','13:20:00',NULL,'13:20:21',NULL,NULL),(1199710221,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1212901701,2,3.05,'2024-10-09',1,'Order Confirmed','12:49:20',NULL,NULL,NULL,NULL),(1216091405,2,3.05,'2024-10-09',1,'Canceled','20:16:10',NULL,'20:16:17',NULL,NULL),(1222340236,2,5.34,'2024-10-09',1,'Order Confirmed','20:21:11',NULL,NULL,NULL,NULL),(1333569889,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1359951025,2,3.05,'2024-10-09',1,'Canceled','15:53:15',NULL,'15:53:17',NULL,NULL),(1409039216,2,20.14,'2024-10-09',1,'Order Received','03:39:18',NULL,NULL,NULL,NULL),(1412491600,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1412995597,2,14.88,'2024-10-09',1,'Canceled','15:47:10',NULL,'15:47:41',NULL,NULL),(1462493827,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1470661655,2,8.39,'2024-10-09',1,'Canceled','20:06:38',NULL,'20:06:42',NULL,NULL),(1504776802,2,4.96,'2024-10-09',1,'Order Confirmed','20:56:04',NULL,NULL,NULL,NULL),(1604073781,2,3.05,'2024-10-09',1,'Canceled','14:01:02',NULL,'14:01:04',NULL,NULL),(1646826634,2,18.73,'2024-10-09',1,'Canceled','13:17:10',NULL,'13:17:34',NULL,NULL),(1670415672,2,18.73,'2024-10-09',4,'Order Confirmed','18:37:53',NULL,NULL,NULL,NULL),(1747324693,2,3.05,'2024-10-09',NULL,'Order Confirmed','18:37:06',NULL,NULL,NULL,NULL),(1843493275,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1878632611,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(1908231481,2,6.10,'2024-10-09',1,'Canceled','17:26:27',NULL,'17:26:40',NULL,NULL),(1991678682,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2084400247,2,15.68,'2024-10-09',1,'Canceled','23:32:46',NULL,'23:33:23',NULL,NULL),(2110459250,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-10  9:13:33
