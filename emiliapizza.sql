-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: emiliadb
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `Customer_ID` int NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Birthdate` date DEFAULT NULL,
  `Postcode_ID` int DEFAULT NULL,
  `Street_Number` int DEFAULT NULL,
  `Email_Address` varchar(100) DEFAULT NULL,
  `Phone_Number` varchar(9) DEFAULT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `canBirthDay` tinyint(1) DEFAULT '1',
  `canDiscount` tinyint(1) DEFAULT '0',
  `PizzasOrdered` int DEFAULT '0',
  PRIMARY KEY (`Customer_ID`),
  KEY `Postcode_ID` (`Postcode_ID`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`Postcode_ID`) REFERENCES `postcode` (`Postcode_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliverydrivers`
--

DROP TABLE IF EXISTS `deliverydrivers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliverydrivers` (
  `DeliveryDriver_ID` int NOT NULL,
  `DeliveryDriver_Name` varchar(100) DEFAULT NULL,
  `isAvailable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`DeliveryDriver_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliverydrivers`
--

LOCK TABLES `deliverydrivers` WRITE;
/*!40000 ALTER TABLE `deliverydrivers` DISABLE KEYS */;
INSERT INTO `deliverydrivers` VALUES (1,'Justin Jefferson',1),(2,'Lamar Jackson',1),(3,'Robert Griffin III',1),(4,'Christian McCaffrey',1),(5,'Brian Robinson Jr',1);
/*!40000 ALTER TABLE `deliverydrivers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `desserts`
--

DROP TABLE IF EXISTS `desserts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `desserts` (
  `dessert_id` int NOT NULL,
  `dessert_name` varchar(100) DEFAULT NULL,
  `dessert_price` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`dessert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `desserts`
--

LOCK TABLES `desserts` WRITE;
/*!40000 ALTER TABLE `desserts` DISABLE KEYS */;
INSERT INTO `desserts` VALUES (1,'Cheesecake',4.50),(2,'Pistachio Tiramisu',5.00);
/*!40000 ALTER TABLE `desserts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discountcodes`
--

DROP TABLE IF EXISTS `discountcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discountcodes` (
  `DiscountCode_ID` int NOT NULL,
  `Discount_Value` decimal(10,2) DEFAULT NULL,
  `DiscountCode_isAvailable` tinyint(1) DEFAULT NULL,
  `DiscountCode` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`DiscountCode_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discountcodes`
--

LOCK TABLES `discountcodes` WRITE;
/*!40000 ALTER TABLE `discountcodes` DISABLE KEYS */;
INSERT INTO `discountcodes` VALUES (1,0.90,1,'2311'),(2,0.50,1,'1210'),(3,0.10,1,'0408'),(4,0.15,1,'2002'),(5,0.18,1,'2004'),(6,0.23,1,'6226'),(7,0.20,1,'6213');
/*!40000 ALTER TABLE `discountcodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drinks`
--

DROP TABLE IF EXISTS `drinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drinks` (
  `drink_id` int NOT NULL,
  `drink_name` varchar(100) DEFAULT NULL,
  `drink_price` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`drink_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drinks`
--

LOCK TABLES `drinks` WRITE;
/*!40000 ALTER TABLE `drinks` DISABLE KEYS */;
INSERT INTO `drinks` VALUES (1,'Moretti Beer',3.50),(2,'Negroamaro Wine',4.50),(3,'Aperol Spritz',5.50),(4,'Water',0.80);
/*!40000 ALTER TABLE `drinks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitems`
--

DROP TABLE IF EXISTS `orderitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitems` (
  `OrderItem_ID` int NOT NULL,
  `Order_ID` int DEFAULT NULL,
  `Customer_ID` int DEFAULT NULL,
  `Pizza_ID` int DEFAULT NULL,
  `Dessert_ID` int DEFAULT NULL,
  `Drink_ID` int DEFAULT NULL,
  `OrderItem_Amount` int NOT NULL,
  PRIMARY KEY (`OrderItem_ID`),
  KEY `Order_ID` (`Order_ID`),
  KEY `Customer_ID` (`Customer_ID`),
  KEY `Pizza_ID` (`Pizza_ID`),
  KEY `Dessert_ID` (`Dessert_ID`),
  KEY `Drink_ID` (`Drink_ID`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`Order_ID`) REFERENCES `orders` (`Order_ID`),
  CONSTRAINT `orderitems_ibfk_2` FOREIGN KEY (`Customer_ID`) REFERENCES `customers` (`Customer_ID`),
  CONSTRAINT `orderitems_ibfk_3` FOREIGN KEY (`Pizza_ID`) REFERENCES `pizzas` (`pizza_id`),
  CONSTRAINT `orderitems_ibfk_4` FOREIGN KEY (`Dessert_ID`) REFERENCES `desserts` (`dessert_id`),
  CONSTRAINT `orderitems_ibfk_5` FOREIGN KEY (`Drink_ID`) REFERENCES `drinks` (`drink_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitems`
--

LOCK TABLES `orderitems` WRITE;
/*!40000 ALTER TABLE `orderitems` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `Order_ID` int NOT NULL,
  `Customer_ID` int DEFAULT NULL,
  `Total_Amount` decimal(5,2) NOT NULL,
  `Order_Date` date NOT NULL,
  `DeliveryDriver_ID` int DEFAULT NULL,
  `Order_Status` varchar(50) DEFAULT NULL,
  `Order_StartTime` time DEFAULT NULL,
  `Order_ETA` time DEFAULT NULL,
  `Order_EndTime` time DEFAULT NULL,
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
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pizzas`
--

DROP TABLE IF EXISTS `pizzas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pizzas` (
  `pizza_id` int NOT NULL,
  `pizza_name` varchar(100) DEFAULT NULL,
  `pizza_baseprice` decimal(5,2) DEFAULT NULL,
  `pizza_finalprice` decimal(5,2) DEFAULT NULL,
  `pizza_isvegan` tinyint(1) DEFAULT NULL,
  `pizza_isvegetarian` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`pizza_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pizzas`
--

LOCK TABLES `pizzas` WRITE;
/*!40000 ALTER TABLE `pizzas` DISABLE KEYS */;
INSERT INTO `pizzas` VALUES (1,'Margherita',NULL,NULL,0,1),(2,'BBQ Chicken',NULL,NULL,0,0),(3,'Kebab',NULL,NULL,0,0),(4,'Pepperoni',NULL,NULL,0,0),(5,'Quatro Formaggio',NULL,NULL,0,1),(6,'Tartufo Porcino',NULL,NULL,0,0),(7,'Vegan Riviera',NULL,NULL,1,1),(8,'Tropical Delight',NULL,NULL,0,0),(9,'Settebello',NULL,NULL,0,0),(10,'Bresca',NULL,NULL,0,0);
/*!40000 ALTER TABLE `pizzas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pizzatoppings`
--

DROP TABLE IF EXISTS `pizzatoppings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pizzatoppings` (
  `pizza_id` int NOT NULL,
  `topping_id` int NOT NULL,
  PRIMARY KEY (`pizza_id`,`topping_id`),
  KEY `topping_id` (`topping_id`),
  CONSTRAINT `pizzatoppings_ibfk_1` FOREIGN KEY (`pizza_id`) REFERENCES `pizzas` (`pizza_id`),
  CONSTRAINT `pizzatoppings_ibfk_2` FOREIGN KEY (`topping_id`) REFERENCES `toppings` (`topping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pizzatoppings`
--

LOCK TABLES `pizzatoppings` WRITE;
/*!40000 ALTER TABLE `pizzatoppings` DISABLE KEYS */;
/*!40000 ALTER TABLE `pizzatoppings` ENABLE KEYS */;
UNLOCK TABLES;

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

-- Dump completed on 2024-09-24 23:41:54
