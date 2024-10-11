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
-- Table structure for table `Batches`
--

DROP TABLE IF EXISTS `Batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Batches` (
  `Batch_ID` int NOT NULL,
  `Created_At` timestamp NOT NULL,
  `DeliveryDriver_Name` varchar(255) NOT NULL,
  `Postcode` varchar(10) NOT NULL,
  `IsDispatched` tinyint(1) NOT NULL,
  PRIMARY KEY (`Batch_ID`),
  UNIQUE KEY `Batch_ID_UNIQUE` (`Batch_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Batches`
--

LOCK TABLES `Batches` WRITE;
/*!40000 ALTER TABLE `Batches` DISABLE KEYS */;
INSERT INTO `Batches` VALUES (87408264,'2024-10-10 19:26:15','Christian McCaffrey','6211',0),(360447264,'2024-10-10 19:07:46','Robert Griffin III','6221',0),(497167814,'2024-10-10 23:46:00','Justin Jefferson','6226',1),(535431610,'2024-10-10 21:40:28','Justin Jefferson','6221',1),(943980677,'2024-10-10 19:05:11','Robert Griffin III','6226',0),(1247384157,'2024-10-10 20:08:30','Brian Robinson Jr','6226',0),(1408561096,'2024-10-10 18:49:09','Robert Griffin III','6211',0),(2102906456,'2024-10-10 20:50:37','Brian Robinson Jr','6226',1),(2113267075,'2024-10-11 00:07:42','Justin Jefferson','6226',1);
/*!40000 ALTER TABLE `Batches` ENABLE KEYS */;
UNLOCK TABLES;

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
  `Phone_Number` varchar(15) DEFAULT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `canBirthDay` tinyint(1) DEFAULT '1',
  `canDiscount` tinyint(1) DEFAULT '0',
  `PizzasOrdered` int DEFAULT '0',
  `Street_Name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Customer_ID`),
  UNIQUE KEY `Customer_ID_UNIQUE` (`Customer_ID`),
  KEY `Postcode_ID` (`Postcode_ID`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`Postcode_ID`) REFERENCES `postcode` (`Postcode_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customers`
--

LOCK TABLES `Customers` WRITE;
/*!40000 ALTER TABLE `Customers` DISABLE KEYS */;
INSERT INTO `Customers` VALUES (1,'Ramez','Male','2005-05-07',19,50,'Ramezb275@gmail.com','0627579368','admin','$2a$10$ROrOQ0kVnvL5/8XeUZ5y4eLg4DEiwW8tWPcKutCLTsHlq7FFm87nC',1,0,0,'ac'),(2,'Esteban ','Male','2004-08-04',19,50,'Esteban123@gmail.com','0627578790','esteban','$2a$10$GaiDs4E7exV5.k.dGjui8O3zJyUDUCa/N/oIx2Nl6T84rcawPTEC2',1,0,0,'ac'),(3,'Tina ','female','2003-05-23',19,50,'tina@tina.com','066662728','tina','$2a$10$VvZZpXqAwOvxZQoUXRTese.gLNv025.tdukX2oQU5IjvnSdCKxKpa',1,0,0,'ace'),(4,'Tom ','Male','1999-09-02',14,50,'tom@gmail.com','062756893','tom','$2a$10$BncU1ujO.7/wGT5AfC.U6ernvt5GgzAywIrF2HZdrMUuRE56PL0Zq',1,0,0,'ree'),(5,'ben','male','2005-07-05',19,55,'ben@gmail.com','062757','ben','$2a$10$6oSnf5fdcqB4PhgGvnLPBOtN8OHza2LBXBoQe1xSmgZw7DVqChw.6',1,0,0,'wef'),(6,'john','male','2024-10-10',19,60,'john@gmail.com','0682374','john','$2a$10$jhLiCyGYCoxzMH6k66QMteVSygvLNXaFUzmHvN9Q1LjSsWoVjrNTS',0,0,0,'wcds'),(7,'karol','male','2024-10-10',19,45,'karol@gmail.com','062343','karol','$2a$10$gJMBa6aiKdrY8bFiZb6KdOP1bR36f1/V7XdiZopE0eB29p9MzW5Gy',0,0,0,'sce'),(8,'james','male','2024-10-10',14,35,'james@gmail','0628453','james','$2a$10$NB3qLes9jfyyyYoGUF/Vp.ALb6/z5AXAk05PgjoMIVix5exfsXq4a',0,0,0,'cdw'),(9,'nasri','Male','2003-10-10',19,35,'nasri@gmai.com','62747822','nasri','$2a$10$3LR5QLlzF8F054tKUxNXxuI/FpyWviMiCIglyzI8RkCRtDgmok6lG',0,0,0,'trew'),(10,'hanna','male','2006-10-11',19,35,'hanna@gmail','06234322','hanna','$2a$10$O9Z9XtwFsNC5kqHDQ47B/uBSbBkFH/McRccvYyuzwLGghudYPjv7a',0,0,0,'sree'),(11,'sief','male','2005-10-11',19,23,'sief@gmail','237672334','sief','$2a$10$TkPB8.u.2MDnWGIKtAln/uLRaOr08EDyCW38NRgw12CoVGX/lm0qu',0,0,0,'street');
/*!40000 ALTER TABLE `Customers` ENABLE KEYS */;
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
INSERT INTO `discountcodes` VALUES (1,0.25,1,'2311'),(2,0.50,1,'1210'),(3,0.10,1,'0408'),(4,0.15,1,'2002'),(5,0.18,1,'2004'),(6,0.23,1,'6226'),(7,0.20,1,'6213');
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
  `Customer_ID` int NOT NULL,
  `Pizza_ID` int DEFAULT NULL,
  `Dessert_ID` int DEFAULT NULL,
  `Drink_ID` int DEFAULT NULL,
  `Pizza_Quantity` int DEFAULT '1',
  PRIMARY KEY (`OrderItem_ID`),
  KEY `Order_ID` (`Order_ID`),
  KEY `Customer_ID` (`Customer_ID`),
  KEY `Pizza_ID` (`Pizza_ID`),
  KEY `Dessert_ID` (`Dessert_ID`),
  KEY `Drink_ID` (`Drink_ID`),
  CONSTRAINT `Customer_ID` FOREIGN KEY (`Customer_ID`) REFERENCES `Customers` (`Customer_ID`),
  CONSTRAINT `orderitems_ibfk_1` FOREIGN KEY (`Order_ID`) REFERENCES `orders` (`Order_ID`),
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
INSERT INTO `orderitems` VALUES (6,346274031,8,NULL,2,NULL,NULL),(48,1123408966,6,1,NULL,NULL,1),(131,1502795856,4,NULL,NULL,2,NULL),(194,640336717,10,2,NULL,NULL,1),(220,85225176,7,NULL,1,NULL,NULL),(229,1004679476,5,NULL,1,NULL,NULL),(235,346274031,8,NULL,NULL,1,NULL),(261,640336717,10,NULL,NULL,1,NULL),(274,1004679476,5,2,NULL,NULL,1),(280,1982025315,11,NULL,1,NULL,NULL),(308,1004679476,5,NULL,NULL,1,NULL),(357,1982025315,11,4,NULL,NULL,1),(395,1502795856,4,3,NULL,NULL,1),(432,1123408966,6,NULL,NULL,1,NULL),(434,85225176,7,NULL,NULL,2,NULL),(495,85225176,7,3,NULL,NULL,1),(546,1123408966,6,NULL,2,NULL,NULL),(650,1805376502,4,1,NULL,NULL,10),(669,2141823730,4,5,NULL,NULL,1),(680,346274031,8,6,NULL,NULL,1),(810,85225176,7,NULL,NULL,4,NULL),(863,1805376502,4,NULL,NULL,3,NULL),(953,2141823730,4,4,NULL,NULL,1),(969,1123408966,6,5,NULL,NULL,1);
/*!40000 ALTER TABLE `orderitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `Order_ID` int NOT NULL,
  `Customer_ID` int NOT NULL,
  `Total_Amount` decimal(5,2) DEFAULT NULL,
  `Order_Date` date DEFAULT NULL,
  `DeliveryDriver_ID` int DEFAULT NULL,
  `Order_Status` varchar(50) DEFAULT NULL,
  `Order_StartTime` time DEFAULT NULL,
  `Order_EndTime` time DEFAULT NULL,
  `Batch_ID` int DEFAULT NULL,
  `Postcode` varchar(10) DEFAULT NULL,
  `DiscountCode_ID` int DEFAULT NULL,
  PRIMARY KEY (`Order_ID`),
  KEY `Customer_ID` (`Customer_ID`),
  KEY `DeliveryDriver_ID` (`DeliveryDriver_ID`),
  KEY `DiscountCode_ID` (`DiscountCode_ID`),
  KEY `Batch_ID` (`Batch_ID`),
  CONSTRAINT `Batch_ID` FOREIGN KEY (`Batch_ID`) REFERENCES `Batches` (`Batch_ID`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`Customer_ID`) REFERENCES `customers` (`Customer_ID`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`DeliveryDriver_ID`) REFERENCES `deliverydrivers` (`DeliveryDriver_ID`),
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`DiscountCode_ID`) REFERENCES `discountcodes` (`DiscountCode_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES (85225176,7,5.65,'2024-10-10',5,'Canceled','22:50:37','22:54:11',2102906456,'6226',1),(346274031,8,10.79,'2024-10-10',1,'Order Confirmed','23:40:28','12:10:28',535431610,'6221',NULL),(640336717,10,33.09,'2024-10-11',1,'Canceled','01:46:00','01:49:13',497167814,'6226',NULL),(1004679476,5,22.17,'2024-10-10',5,'Canceled','22:08:30','22:09:44',1247384157,'6226',4),(1123408966,6,22.40,'2024-10-10',5,'Order Confirmed','22:53:22','23:23:22',2102906456,'6226',4),(1502795856,4,11.45,'2024-10-10',1,'Canceled','23:42:28','23:46:20',535431610,'6221',1),(1805376502,4,39.52,'2024-10-11',1,'Order Confirmed','02:09:21','02:39:21',2113267075,'6226',NULL),(1982025315,11,6.11,'2024-10-11',1,'Canceled','02:07:42','02:11:01',2113267075,'6226',1),(2141823730,4,17.23,'2024-10-11',1,'Canceled','01:48:05','02:18:05',497167814,'6226',NULL);
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
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
INSERT INTO `pizzas` VALUES (1,'Margherita',3.05,0,1),(2,'BBQ Chicken',4.96,0,0),(3,'Kebab',6.10,0,0),(4,'Pepperoni',3.05,0,0),(5,'Quatro Formaggio',10.68,0,1),(6,'Tartufo Porcino',5.34,0,0),(7,'Vegan Riviera',6.87,1,1),(8,'Tropical Delight',4.20,0,0),(9,'Settebello',8.77,0,0),(10,'Bresca',5.34,0,0);
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
INSERT INTO `pizzatoppings` VALUES (1,1),(5,1),(1,2),(2,3),(2,4),(3,5),(2,6),(3,7),(4,8),(5,10),(9,10),(5,11),(5,12),(7,18),(7,19),(3,20),(7,20),(8,22),(8,23),(10,26),(9,27),(8,28),(10,28),(6,30),(9,30),(6,31);
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

-- Dump completed on 2024-10-11  4:10:29
