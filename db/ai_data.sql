/*
 Navicat Premium Data Transfer

 Source Server         : wslmysql
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 172.22.131.7:3306
 Source Schema         : ai_data

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 07/05/2024 14:31:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_tts
-- ----------------------------
DROP TABLE IF EXISTS `data_tts`;
CREATE TABLE `data_tts`  (
  `id` bigint(0) NOT NULL DEFAULT 0 COMMENT '主键id',
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `tts_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成的语音文件路径',
  `file_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件访问url地址',
  `type` int(0) NULL DEFAULT NULL COMMENT '1语音转文本、2文本生成语音',
  `org_id` bigint(0) NULL DEFAULT NULL COMMENT '机构id',
  `size` bigint(0) NULL DEFAULT NULL COMMENT '文件大小',
  `version` int(0) NULL DEFAULT NULL,
  `deleted` tinyint(0) NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updater` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_tts
-- ----------------------------
INSERT INTO `data_tts` VALUES (0, '开源所有数据集来源', '/bigdata/deploy/aiot-data/upload/tts/output/1714312858662.wav', 'http://localhost:8083/sys/upload/tts/output/1714312858662.wav', 2, NULL, 106028, 0, 0, 10000, '2024-04-28 22:01:12', 10000, '2024-04-28 22:01:12');
INSERT INTO `data_tts` VALUES (0, '有很多大模型。在这里下载会方便些', '/bigdata/deploy/aiot-data/upload/tts/output/1714643635635.wav', 'http://localhost:8083/sys/upload/tts/output/1714643635635.wav', 2, NULL, 184876, 0, 0, 10000, '2024-05-02 17:54:18', 10000, '2024-05-02 17:54:18');
INSERT INTO `data_tts` VALUES (0, '下载大模型会快些', '/bigdata/deploy/aiot-data/upload/tts/output/1714643781212.wav', 'http://localhost:8083/sys/upload/tts/output/1714643781212.wav', 2, NULL, 109100, 0, 0, 10000, '2024-05-02 17:56:36', 10000, '2024-05-02 17:56:36');

SET FOREIGN_KEY_CHECKS = 1;
