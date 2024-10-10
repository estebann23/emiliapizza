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
-- Table structure for table `Customers`
--

DROP TABLE IF EXISTS `Customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customers` (
  `Customer_ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Birthdate` date DEFAULT NULL,
  `Postcode_ID` int DEFAULT NULL,
  `Street_Number` int DEFAULT NULL,
  `Email_Address` varchar(100) DEFAULT NULL,
  `Phone_Number` varchar(9) DEFAULT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `canBirthDay` tinyint(1) DEFAULT '1',
  `canDiscount` tinyint(1) DEFAULT '0',
  `PizzasOrdered` int DEFAULT '0',
  `Street_Name` varchar(255) DEFAULT NULL,
  `Postcode` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`Customer_ID`),
  UNIQUE KEY `Customer_ID_UNIQUE` (`Customer_ID`),
  KEY `Postcode_ID` (`Postcode_ID`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`Postcode_ID`) REFERENCES `postcode` (`Postcode_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customers`
--

LOCK TABLES `Customers` WRITE;
/*!40000 ALTER TABLE `Customers` DISABLE KEYS */;
INSERT INTO `Customers` VALUES (1,'Ramez','Bassous','2005-02-07',NULL,NULL,'Ramezb276@gmail.com','98273972','Bassous','1234',1,0,0,NULL,NULL),(2,'ran','m','2005-09-04',NULL,50,'fwerfw','3241','bass','$2a$10$AgWT2Ag4K1XpdXTjKChmEupMCEPvVQVoGPCkYDlQfZ4GW1aRudkRi',1,0,0,'wefs','6229'),(3,'ran','m','2005-09-04',NULL,NULL,'fwerfw','3241','bass','$2a$10$eIGC9ukimMfGv0dp3EdHkOeRtrVAV/HO8vhJkOzE8yFboXGB8KJQ2',1,0,0,NULL,NULL),(4,'ran','m','2005-09-04',NULL,NULL,'fwerfw','3241','bass','$2a$10$Q1jJQy.nX5PirhAmjKO/C.SPpnSWKTKj4g5aJctP3OlwIDGeq4dSC',1,0,0,NULL,NULL);
/*!40000 ALTER TABLE `Customers` ENABLE KEYS */;
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
