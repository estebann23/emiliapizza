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
-- Table structure for table `toppings`
--

DROP TABLE IF EXISTS `toppings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `toppings` (
  `topping_id` int NOT NULL,
  `topping_name` varchar(100) DEFAULT NULL,
  `topping_price` decimal(5,2) DEFAULT NULL,
  `topping_isvegan` tinyint(1) DEFAULT NULL,
  `toppping_isvegetarian` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`topping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `toppings`
--

LOCK TABLES `toppings` WRITE;
/*!40000 ALTER TABLE `toppings` DISABLE KEYS */;
INSERT INTO `toppings` VALUES (1,'Mozzarella',1.50,0,1),(2,'Cherry Tomato',0.50,1,1),(3,'BBQ',0.75,1,1),(4,'Chicken',2.00,0,0),(5,'Kebab Strings',2.50,0,0),(6,'Onion',0.50,1,1),(7,'Red Paprika',0.75,1,1),(8,'Pepperoni',2.00,0,0),(9,'Curry Ketchup',0.50,1,1),(10,'Parmesan',1.75,0,1),(11,'Pecorino',2.00,0,1),(12,'Ricotta',1.75,0,1),(13,'Garlic Oil',0.50,1,1),(14,'Truffles',3.00,1,1),(15,'Bacon',2.00,0,0),(16,'Black Pepper',0.25,1,1),(17,'Rugula',1.00,1,1),(18,'Dried Tomato',1.50,1,1),(19,'Vegan Chicken',2.25,1,0),(20,'Black Olives',0.75,1,1),(21,'Salami',2.00,0,0),(22,'Avocado',1.50,1,1),(23,'Corn',0.75,1,1),(24,'Pulled Pork',2.50,0,0),(25,'Garlic Sauce',0.50,1,1),(26,'Carpaccio',3.00,0,0),(27,'Pear',1.50,1,1),(28,'Olive Oil',0.50,1,1),(29,'Stracchino',2.00,0,1),(30,'Truffle Ham',2.50,0,0),(31,'Mushrooms',1.00,1,1),(32,'Parsley',0.50,1,1);
/*!40000 ALTER TABLE `toppings` ENABLE KEYS */;
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
