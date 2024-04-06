> 针对物联网的控制及数据整理开发一个云系统,集成对硬件的便捷控制处理及数据的可视化展示!
> [物联网 - chenjimo (chenjimo) - Gitee.com](https://gitee.com/chenjimo/collections/375599)
> JIMO-IOT的整体开发正在设计过程中，后续开发好整理好资料，会在[https://gitee.com/chenjimo](https://gitee.com/chenjimo)我的码云进行开源，后续更新请持续关注！

[JIMO-IOT首页](http://iot.jimo.fun/)
> 应用案例：[基于ESP32&JIMO-IOT搭建的看门狗设备](https://www.yuque.com/jimoworld/linux/smart_watchdog?view=doc_embed)、[基于JIMO-IOT的智能浇水开发](https://www.yuque.com/jimoworld/linux/smart_watering?view=doc_embed)、[基于JIMO-IOT的智能晾衣架开发](https://www.yuque.com/jimoworld/linux/smart_rack?view=doc_embed)、[JIMO-IOT通信控制测试Demo1](https://www.yuque.com/jimoworld/linux/tdc8gg73oiorz9b2?view=doc_embed)；
> 优化后的版本：[JIMO-IOT针对部分案例的优化](https://www.yuque.com/jimoworld/javabj/jimo-iot-v1.3?view=doc_embed)

<a name="BxzG4"></a>
#### 代码地址：
[chenjimo/JIMO_IOT](https://gitee.com/chenjimo/JIMO_IOT)
<a name="Rt3DS"></a>
## 整体架构设计
:::info
设计思路：基于HTTP协议的（类MQTT）+Server+Web的开发。<br />客户端向服务端进行订阅信息（定时心跳并汇报状态）、上传传感器数据（原数据）、并刷新服务端的指令要求！
:::
![](https://cdn.nlark.com/yuque/0/2023/jpeg/26820301/1700054841115-7f6a7b81-5fe4-4fff-8105-8823b20700e3.jpeg)
<a name="SrQ5E"></a>
## 1.0的简化版本
:::info
先实现传感器数据的上传储存和页面展示、及简单的控制指令。<br />实操接入Client硬件的案例：[JIMO-IOT通信控制测试Demo1](https://www.yuque.com/jimoworld/linux/tdc8gg73oiorz9b2?view=doc_embed)
:::
<a name="MlvkB"></a>
### Mysql的数据结构
```sql
-- MySQL dump 10.13  Distrib 8.0.24, for Linux (x86_64)
--
-- Host: localhost    Database: jimo_iot
-- ------------------------------------------------------
-- Server version	8.0.24

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
-- Table structure for table `device_log`
--

DROP TABLE IF EXISTS `device_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `device_id` int NOT NULL,
  `value` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0',
  `time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `bz` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_log`
--

LOCK TABLES `device_log` WRITE;
/*!40000 ALTER TABLE `device_log` DISABLE KEYS */;
INSERT INTO `device_log` VALUES (1,1,'30','2023-11-15 15:55:32','test'),(2,1,'10','2023-11-16 05:08:31','test'),(3,1,'40','2023-11-16 05:08:36','test'),(4,1,'28.6','2023-11-16 09:19:37','test'),(5,1,'14.3','2023-11-16 09:19:50','test');
/*!40000 ALTER TABLE `device_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device_message`
--

DROP TABLE IF EXISTS `device_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `module_id` int NOT NULL,
  `type` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `unit` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `max` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1024',
  `min` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '-1024',
  `bz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_message`
--

LOCK TABLES `device_message` WRITE;
/*!40000 ALTER TABLE `device_message` DISABLE KEYS */;
INSERT INTO `device_message` VALUES (1,'温度传感器',1,'温度','℃','40','10',NULL),(2,'湿度传感器',1,'湿度','%RH','95','20',NULL),(3,'空气质量',3,'烟雾等','无','500','10','MQ2'),(4,'土壤湿度',2,'水分','%RH','100','30',NULL),(5,'光感',1,'光照',NULL,'800','100','光敏电阻'),(6,'震动传感器',3,'晃动',NULL,'600','0','检测破门'),(7,'人体感应',3,'人靠近',NULL,'1024','-1024','人员检测'),(8,'水位传感器',2,'水位',NULL,'800','10','浇水限制');
/*!40000 ALTER TABLE `device_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_log`
--

DROP TABLE IF EXISTS `module_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `module_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `module_id` int NOT NULL,
  `time` timestamp NOT NULL,
  `bz` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_log`
--

LOCK TABLES `module_log` WRITE;
/*!40000 ALTER TABLE `module_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `module_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_message`
--

DROP TABLE IF EXISTS `module_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `module_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `status` int NOT NULL,
  `mode` int NOT NULL,
  `bz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_message`
--

LOCK TABLES `module_message` WRITE;
/*!40000 ALTER TABLE `module_message` DISABLE KEYS */;
INSERT INTO `module_message` VALUES (1,'智能晾衣架模块',1,0,NULL),(2,'智能浇水模块',1,0,NULL),(3,'智能门窗',1,1,NULL);
/*!40000 ALTER TABLE `module_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oder_log`
--

DROP TABLE IF EXISTS `oder_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oder_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `oder_id` int NOT NULL,
  `user_id` int NOT NULL,
  `module_id` int NOT NULL,
  `write_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `read_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `status` int DEFAULT '0',
  `bz` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oder_log`
--

LOCK TABLES `oder_log` WRITE;
/*!40000 ALTER TABLE `oder_log` DISABLE KEYS */;
INSERT INTO `oder_log` VALUES (1,1,1,1,'2023-11-16 16:38:02','2023-11-16 16:38:02',0,NULL);
/*!40000 ALTER TABLE `oder_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oder_message`
--

DROP TABLE IF EXISTS `oder_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oder_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `message` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `module_id` int NOT NULL,
  `status` int NOT NULL,
  `bz` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oder_message`
--

LOCK TABLES `oder_message` WRITE;
/*!40000 ALTER TABLE `oder_message` DISABLE KEYS */;
INSERT INTO `oder_message` VALUES (1,'打开门窗','OpenWindows',3,1,NULL),(2,'关闭门窗','OnWindows',3,1,NULL),(3,'开始浇水','Watering',2,1,NULL),(4,'收回衣服','UpClothes',1,1,NULL),(5,'晾衣服','CoolClothes',1,1,NULL),(6,'报警','Call',3,1,NULL);
/*!40000 ALTER TABLE `oder_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_message`
--

DROP TABLE IF EXISTS `user_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `pwd` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `login_time` timestamp NULL DEFAULT '2023-11-15 15:55:32' ON UPDATE CURRENT_TIMESTAMP,
  `power` int DEFAULT NULL,
  `phone` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `visit` int DEFAULT '0',
  `bz` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_message`
--

LOCK TABLES `user_message` WRITE;
/*!40000 ALTER TABLE `user_message` DISABLE KEYS */;
INSERT INTO `user_message` VALUES (1,'JIMO','123456','1517962688@qq.com','2023-11-15 15:55:32',7,'13384076768',NULL,0,NULL),(2,'test','test','test@jimo.fun','2023-11-15 15:55:32',1,'13843819438',NULL,0,NULL);
/*!40000 ALTER TABLE `user_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_module`
--

DROP TABLE IF EXISTS `user_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_module` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `module_id` int NOT NULL,
  `power` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_module`
--

LOCK TABLES `user_module` WRITE;
/*!40000 ALTER TABLE `user_module` DISABLE KEYS */;
INSERT INTO `user_module` VALUES (1,1,1,2),(2,1,2,6),(3,1,3,7),(4,2,3,6);
/*!40000 ALTER TABLE `user_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'jimo_iot'
--

--
-- Dumping routines for database 'jimo_iot'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-18  2:30:04

```
<a name="bnHNk"></a>
#### 传感器信号的采集存储【device_log】
> 一个信号一个设备（传感器）ID、存储的原始值为String、一个精准的时间戳、一个备用的备注

| **id** | **device_id** | **value** | **time** | **bz** |
| --- | --- | --- | --- | --- |
| **1** | **1** | **24.5** | **2023-11-15 23:55:32** | **test** |

<a name="JeFrX"></a>
#### 传感器的信息（定死）【device_message】
> 传感器的name、归属模块module_id、检测类型type、检测单位unit、备注bz、
> 阈值报警提示：最大值man、最小值min、

| **id** | **name** | **module_id** | **type** | **unit** | **man** | **min** | **bz** |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **1** | **温度传感器** | **1** | **温度** | **℃** | **40** | **10** | <br /> |

<a name="ounIJ"></a>
#### 模块的信息（用户最小控制范围为模块）（定死）【module_message】
> 模块name、模块的状态status（30s中内有心跳为1、没有心跳1min为0、删除为-1）、
> 模式设置mode（0为自动模式、1为智能模式、2为严格模式）、备注bz、

| id | name | status | mode | bz |
| --- | --- | --- | --- | --- |
| 1 | 智能晾衣架模块 | 0 | 1 |  |

<a name="g6Vuv"></a>
#### 模块的心跳记录（大约30S会汇报一次）【module_log】
> 模块module_id、心跳时间戳、备注bz、

| id | module_id | time | bz |
| --- | --- | --- | --- |
| 1 | 1 | 2023-11-15 23:55:32 | test |

<a name="iPuFz"></a>
#### 控制指令记录（用户选择规范指令）（定死）【oder_message】
> 指令名字、指令内容message、控制模块module_id、
> 指令状态status（1可用、0禁用、-1已删除）、备注bz、

| id | name | message | module_id | status | bz |
| --- | --- | --- | --- | --- | --- |
| 1 | 打开窗户 | OpenWindows | 1 | 1 | test |

<a name="LhlMj"></a>
#### 控制记录（用户刷新指令，模块按顺序执行）【oder_log】
> 操作者ID、指令ID、模块ID、
> 操作时间（用户输入时）、执行时间（模块刷新到时）、
> 成功失败未响应撤回status（操作完回馈信息：1成功、2失败、0暂未向应、-1已撤销）、备注ｂz、

| id | user_id | module_id | write_time | read_time | status（0） | bz |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 1 | 1 | 2023-11-15 23:55:32 | 2023-11-15 23:56:02 | 1 | test |

<a name="loaO5"></a>
#### 用户信息【user_message】
> 用户ID、用户name、email、pwd、loginTime、sex、phone、备注bz、操作次数visit、
> 权限等级（1可以看、2可操作、4可管理，同理3=1+2、5=1+4、7=1+2+4）

| id | name | pwd | email | power | phone | loginTime | sex | visit | bz |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | JIMO | 123456 | 1517962688@qq.com | 7 | 13843819438 | 2023-11-15 23:55:32 | 1 | 20 | Admin |

<a name="F36ws"></a>
#### 用户拥有的模块信息【user_module】
> 用户ID、模块ID、
> 模块权限表示模块的操作限制（1可以看、2可操作、4可管理，同理3=1+2、5=1+4、7=1+2+4）、

| id | user_id | module_id | power |
| --- | --- | --- | --- |
| 1 | 1 | 1 | 7 |

<a name="pIHi2"></a>
### API规范
> 心跳时间测试时间：30S，正常运行设置为300S比较合适
> 实时统计数据的刷新建议在：测试5S，正常运行20S
> 传感器的数值上传间隔测试（硬件端）：4S，正常运行设置为15S比较合适。
> 指令请求设置（硬件端）：10S左右比较合适，越小越准时但会浪费很多性能。

<a name="hMo6Z"></a>
#### 传感器的数据上传
:::info
传感器数据上传：POST，ID、value、time、
:::
<a name="WnZFF"></a>
### 问题逻辑分析
<a name="bzQ34"></a>
#### 对于模块的状态分析
:::info
我们在进行输入指令的时候，模块是智能状态和手动状态的激活方式，是在每几秒的命令获取中进行刷新自己的状态，是自动模式下和手动模式下都要进行智能检测进行判别，所以应该设置优先级！<br />严格模式（手动设置）>手动模式>智能模式>自动模式。<br />这里我们简化为，只有智能模式和全自动模式，意味着没有指令的情况下，模块自主决定行为。只要有指令就按计划执行指令。也就意味着在简化版本中mode为一个虚设的存在，不需要特定的去读取模式状态，来进行调整。后续可以在2.0版本中完善，对于不同模式下的用户操作限制以及模块的运行状态控制。在1.0版本中均做了扩展预留的预设。
:::
<a name="mJrOo"></a>
#### 对于模块和对应用户的权限分析
:::info
理论上讲多个用户对应多个家庭和多个成员。意味着模块是有家庭的属性的但又不仅限一个人，安全考虑只有模块有不同用户的属性对应不同模块，不同的家庭成员对此安全考虑儿童保护应又具有操作权限的限制，所以添加了模块对于不同人的权限（定义原则根据模块自身的情况而定），对于不同用户的权限不同应该由家庭管理员进行设置。所以同家庭的模块对应不同的用户的最高权限即为家庭管理员。模块权限由模块接入时所对应自身的功能和属性决定！<br />所以在展示指令的时候应该会用对应的所属权和权限进行匹配和限制操作。同时数据的展示和模块状态栏均应如此，但考虑到简化版中仅有一个用户和简单的测试用户的情况所以没有对家庭关系的权限和控制及展示做详细的逻辑处理，会在后续的2.0版本中完善这一项家庭的权限管理！在1.0版本中均做了扩展预留的预设。
:::
<a name="p6mcK"></a>
#### 对于天气的提前感知
:::info
通过外界API自动预判定，进行一次操作指令，和数据存储。（可以依靠心跳函数进行激活对天气的数据刷新，再外加一个状态判定）
:::
<a name="vLCri"></a>
#### 对于执行指令执行时的执行状态判定
:::info
1、每次心跳函数同步刷新，执行单位的状态值，例如窗户是开是关、衣架是伸是缩、水位是高是低（这部分数据均是不需要加载那么勤的）<br />2、单独在执行的时候进行一次逻辑判定（比较复杂且不准）<br />3、或者硬件层面就可以自行进行判定，无需额外添加（待分析电路）。
:::
<a name="me2By"></a>
## 2.0的完全版本
:::info
2024-3-24开始！<br />更多功能的完善和实现！经过最近四个月的使用和与硬件的对接中又遇到的缺陷进行了完善和优化！<br />2024-4-6：目前只做到最新版本：JIMO-IOTV.3[JIMO-IOT针对部分案例的优化](https://www.yuque.com/jimoworld/javabj/jimo-iot-v1.3?view=doc_embed)<br />后续更新开情况了！
:::
[JIMO-IOT针对部分案例的优化](https://www.yuque.com/jimoworld/javabj/jimo-iot-v1.3?view=doc_embed)
