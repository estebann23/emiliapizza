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
-- Table structure for table `postcode`
--

DROP TABLE IF EXISTS `postcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `postcode` (
  `Postcode_ID` int NOT NULL,
  `Postcode` varchar(20) DEFAULT NULL,
  `DeliveryDriver_ID` int DEFAULT NULL,
  PRIMARY KEY (`Postcode_ID`),
  KEY `DeliveryDriver_ID` (`DeliveryDriver_ID`),
  CONSTRAINT `postcode_ibfk_1` FOREIGN KEY (`DeliveryDriver_ID`) REFERENCES `deliverydrivers` (`DeliveryDriver_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `postcode`
--

LOCK TABLES `postcode` WRITE;
/*!40000 ALTER TABLE `postcode` DISABLE KEYS */;
INSERT INTO `postcode` VALUES (1,'6200',1),(2,'6201',1),(3,'6202',1),(4,'6203',1),(5,'6211',1),(6,'6212',2),(7,'6213',2),(8,'6214',2),(9,'6215',2),(10,'6216',3),(11,'6217',3),(12,'6218',3),(13,'6219',3),(14,'6221',4),(15,'6222',4),(16,'6223',4),(17,'6224',4),(18,'6225',4),(19,'6226',5),(20,'6227',5),(21,'6228',5),(22,'6229',5);
/*!40000 ALTER TABLE `postcode` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-10  9:13:32
