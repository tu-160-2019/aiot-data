/*
 Navicat Premium Data Transfer

 Source Server         : wslmysql
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 172.22.131.7:3306
 Source Schema         : srt_cloud

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 12/04/2024 10:39:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_layer
-- ----------------------------
DROP TABLE IF EXISTS `data_layer`;
CREATE TABLE `data_layer`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分层英文名称',
  `cn_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分层中文名称',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分层描述',
  `table_prefix` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名前缀',
  `version` int(0) NULL DEFAULT NULL COMMENT '版本号',
  `deleted` tinyint(0) NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updater` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30007 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数仓分层' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_layer
-- ----------------------------
INSERT INTO `data_layer` VALUES (30002, 'ODS', '数据引入层', '用于接收并处理需要存储至数据仓库系统的原始数据，其数据表的结构与原始数据所在的数据系统中的表结构一致，是数据仓库的数据准备区', 'ods', 3, 0, 10000, '2022-10-08 17:16:35', 10000, '2022-10-08 17:16:41');
INSERT INTO `data_layer` VALUES (30003, 'DIM', '维度层', '使用维度构建数据模型', 'dim', 0, 0, 10000, '2022-10-08 17:17:40', 10000, '2022-10-08 17:17:42');
INSERT INTO `data_layer` VALUES (30004, 'DWD', '明细数据层', '通过企业的业务活动事件构建数据模型。基于具体业务事件的特点，构建最细粒度的明细数据事实表。', 'dwd', 0, 0, 10000, '2022-10-08 17:18:13', 10000, '2022-10-08 17:18:18');
INSERT INTO `data_layer` VALUES (30006, 'DWS', '汇总数据层', '通过分析的主题对象构建数据模型。基于上层的应用和产品的指标需求，构建公共粒度的汇总指标表。', 'dws', 0, 0, 10000, '2022-10-08 17:20:03', 10000, '2022-10-08 17:20:09');
INSERT INTO `data_layer` VALUES (30007, 'ADS', '应用数据层', '用于存放数据产品个性化的统计指标数据，输出各种报表', 'ads', 0, 0, 10000, '2022-10-08 17:20:52', 10000, '2022-10-08 17:20:57');

-- ----------------------------
-- Table structure for data_project
-- ----------------------------
DROP TABLE IF EXISTS `data_project`;
CREATE TABLE `data_project`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `eng_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '英文名称',
  `description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int(0) NOT NULL COMMENT '0-停用 1-启用',
  `duty_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `db_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓ip',
  `db_port` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓端口',
  `db_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓库名',
  `db_schema` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓schema',
  `db_url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓url',
  `db_username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓用户名',
  `db_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数仓密码',
  `db_type` int(0) NOT NULL DEFAULT 1 COMMENT '数仓类型 字典  data_house_type',
  `version` int(0) NULL DEFAULT NULL COMMENT '版本号',
  `deleted` tinyint(0) NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updater` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据项目' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_project
-- ----------------------------
INSERT INTO `data_project` VALUES (10002, '默认项目', 'test_project', '测试', 1, 'admin', '127.0.0.1', '3306', 'srt_data_warehouse_p_10002', 'srt_data_warehouse_p_10002', 'jdbc:mysql://127.0.0.1:3306/srt_data_warehouse_p_10002?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true', 'root', '123456', 1, 77, 0, 10000, '2022-09-27 20:59:19', 10000, '2023-10-11 12:30:50');
INSERT INTO `data_project` VALUES (10009, 'doris数仓测试', 'doris-test', '', 1, 'admin', '192.168.30.128', '9030', 'test_db', 'test_db', 'jdbc:mysql://192.168.30.128:9030/test_db?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true', 'root', '123456', 16, 4, 0, 10000, '2023-06-23 10:27:15', 10000, '2023-11-17 14:22:47');
INSERT INTO `data_project` VALUES (10010, 'pg数仓', 'postgresql', '', 1, '', '127.0.0.1', '5432', 'postgres', 'public', 'jdbc:postgresql://127.0.0.1:5432/postgres', 'postgres', '123456', 5, 9, 0, 10000, '2023-09-24 17:06:33', 10000, '2023-11-09 11:00:20');

SET FOREIGN_KEY_CHECKS = 1;
