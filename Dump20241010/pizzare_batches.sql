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
-- Table structure for table `batches`
--

DROP TABLE IF EXISTS `batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batches` (
  `Batch_ID` int NOT NULL,
  `Created_At` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Batch_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batches`
--

LOCK TABLES `batches` WRITE;
/*!40000 ALTER TABLE `batches` DISABLE KEYS */;
INSERT INTO `batches` VALUES (33376728,'2024-10-09 18:56:04'),(121267526,'2024-10-09 16:41:21'),(136669105,'2024-10-09 19:15:44'),(141156366,'2024-10-09 20:37:36'),(160513755,'2024-10-09 19:15:34'),(161934042,'2024-10-09 16:37:53'),(198566432,'2024-10-09 14:08:22'),(239170523,'2024-10-09 21:36:22'),(250509804,'2024-10-09 19:14:48'),(282033050,'2024-10-09 19:08:05'),(489014623,'2024-10-09 18:06:38'),(514977534,'2024-10-09 14:35:27'),(554269697,'2024-10-09 21:29:29'),(608002650,'2024-10-09 21:30:21'),(612948759,'2024-10-09 15:39:15'),(634869716,'2024-10-09 20:38:44'),(679531962,'2024-10-09 15:00:02'),(697175657,'2024-10-09 21:17:29'),(869480739,'2024-10-09 18:16:10'),(885633620,'2024-10-09 14:58:31'),(1099774444,'2024-10-09 18:21:11'),(1231980388,'2024-10-09 14:28:27'),(1271154248,'2024-10-09 20:30:57'),(1279111960,'2024-10-09 21:32:46'),(1302528276,'2024-10-09 16:37:05'),(1549741009,'2024-10-09 17:50:21'),(1595598139,'2024-10-09 14:28:27'),(1606685029,'2024-10-09 20:42:35'),(1699914112,'2024-10-09 16:38:23'),(1767839164,'2024-10-09 19:14:46'),(1829077909,'2024-10-09 14:30:34'),(1864629690,'2024-10-09 16:55:19'),(1870982862,'2024-10-09 16:26:25'),(1881483316,'2024-10-10 07:02:36'),(1928353871,'2024-10-09 17:07:28'),(2016853148,'2024-10-09 18:03:26'),(2070019575,'2024-10-09 14:58:31'),(2093038878,'2024-10-09 15:26:27');
/*!40000 ALTER TABLE `batches` ENABLE KEYS */;
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