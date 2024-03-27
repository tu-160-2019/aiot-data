/*
 Navicat Premium Data Transfer

 Source Server         : wslmysql
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 172.22.131.7:3306
 Source Schema         : iotkit

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 26/03/2024 21:09:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alert_config
-- ----------------------------
DROP TABLE IF EXISTS `alert_config`;
CREATE TABLE `alert_config`  (
  `id` bigint(0) NOT NULL COMMENT '告警配置id',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `enable` bit(1) NULL DEFAULT NULL COMMENT '是否启用',
  `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '告警严重度',
  `message_template_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联消息转发模板ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '告警名称',
  `rule_info_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联规则引擎ID',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置所属用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alert_config
-- ----------------------------

-- ----------------------------
-- Table structure for alert_record
-- ----------------------------
DROP TABLE IF EXISTS `alert_record`;
CREATE TABLE `alert_record`  (
  `id` bigint(0) NOT NULL COMMENT '告警记录id',
  `alert_time` bigint(0) NULL DEFAULT NULL COMMENT '告警时间',
  `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '告警详情',
  `level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '告警严重度（1-5）',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '告警名称',
  `read_flg` bit(1) NULL DEFAULT NULL COMMENT '是否已读',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置所属用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of alert_record
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类id',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '分类描述',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES ('door', 1650173898298, '门磁');
INSERT INTO `category` VALUES ('fan', 1646630215889, '风扇');
INSERT INTO `category` VALUES ('FreshAir', 1681444312184, '新风');
INSERT INTO `category` VALUES ('gateway', 1646637047902, '网关');
INSERT INTO `category` VALUES ('light', 1650174762755, '灯');
INSERT INTO `category` VALUES ('meter', 1654237582120, '表计');
INSERT INTO `category` VALUES ('OpenIitaGateway', 1688969826383, '铱塔智联智能网关');
INSERT INTO `category` VALUES ('OpenIitaPump', 1688969826383, '铱塔智联水泵');
INSERT INTO `category` VALUES ('sensor', 1649743382683, '传感器');
INSERT INTO `category` VALUES ('SmartMeter', 1681444312184, '智能电表');
INSERT INTO `category` VALUES ('SmartPlug', 1645409421118, '智能插座');
INSERT INTO `category` VALUES ('switch', 1647599367057, '开关');

-- ----------------------------
-- Table structure for channel
-- ----------------------------
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel`  (
  `id` bigint(0) NOT NULL COMMENT '通道id',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道名称',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of channel
-- ----------------------------
INSERT INTO `channel` VALUES (1, 'DingTalk', 1683816661690, 'http://www.baidu.com', '钉钉');
INSERT INTO `channel` VALUES (2, 'QyWechat', 1683816661690, 'http://www.baidu.com', '企业微信');
INSERT INTO `channel` VALUES (3, 'Email', 1683816661690, 'http://www.baidu.com', '邮箱');

-- ----------------------------
-- Table structure for channel_config
-- ----------------------------
DROP TABLE IF EXISTS `channel_config`;
CREATE TABLE `channel_config`  (
  `id` bigint(0) NOT NULL COMMENT '通道配置id',
  `channel_id` bigint(0) NULL DEFAULT NULL COMMENT '通道id',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '通道配置参数',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道配置名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of channel_config
-- ----------------------------
INSERT INTO `channel_config` VALUES (1312312, 3, 1683816661690, '{\"userName\":\"xxx@163.com\",\"passWord\":\"xxx\",\"host\":\"smtp.163.com\",\"port\":465,\"mailSmtpAuth\":true,\"from\":\"xxxx@163.com\",\"to\":\"xxxx@163.com\"}	', '告警邮件配置');
INSERT INTO `channel_config` VALUES (1313123, 1, 1683816661690, '{\"dingTalkWebhook\":\"xxxxxxxxxxxxxxxx\",\"dingTalkSecret\":\"xxxx\"}', '告警钉钉配置');
INSERT INTO `channel_config` VALUES (32141342, 2, 1683816661690, '{\"qyWechatWebhook\":\"xxxxxxxxxxxxxxxx\"}', '告警企业微信配置');

-- ----------------------------
-- Table structure for channel_template
-- ----------------------------
DROP TABLE IF EXISTS `channel_template`;
CREATE TABLE `channel_template`  (
  `id` bigint(0) NOT NULL COMMENT '通道模板id',
  `channel_config_id` bigint(0) NULL DEFAULT NULL COMMENT '通道配置id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道模板内容',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通道模板名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of channel_template
-- ----------------------------
INSERT INTO `channel_template` VALUES (12312312, 1312312, '您的设备【${title}】<font color=\"warning\">温度过高</font>', 1683816661690, '告警邮件模板');
INSERT INTO `channel_template` VALUES (342353425, 1313123, '您的设备【${title}】<font color=\"warning\">温度过高</font>', 1683816661690, '告警钉钉模板');
INSERT INTO `channel_template` VALUES (786778567, 32141342, '您的设备【${title}】<font color=\"warning\">温度过高</font>', 1683816661690, '告警企业微信模板');

-- ----------------------------
-- Table structure for device_config
-- ----------------------------
DROP TABLE IF EXISTS `device_config`;
CREATE TABLE `device_config`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备配置id',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '设备配置json内容',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品key',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_config
-- ----------------------------

-- ----------------------------
-- Table structure for device_group
-- ----------------------------
DROP TABLE IF EXISTS `device_group`;
CREATE TABLE `device_group`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备组id',
  `create_at` bigint(0) NOT NULL COMMENT '创建时间',
  `device_qty` int(0) NOT NULL COMMENT '设备数量',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备组名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组说明',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_group
-- ----------------------------
INSERT INTO `device_group` VALUES ('g1', 0, 10, '分组1', '1111', '1');
INSERT INTO `device_group` VALUES ('g2', 0, 12, '组2', '222', '1');
INSERT INTO `device_group` VALUES ('g3', 0, 7, '组3', '2223333', '1');

-- ----------------------------
-- Table structure for device_group_mapping
-- ----------------------------
DROP TABLE IF EXISTS `device_group_mapping`;
CREATE TABLE `device_group_mapping`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备组映射id',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `group_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备组id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_group_mapping
-- ----------------------------
INSERT INTO `device_group_mapping` VALUES ('0697e9c2-5d0c-4dc6-94d0-c3dafc696765', '16552594475270pulg0a0010110000125', 'g2');
INSERT INTO `device_group_mapping` VALUES ('075e1545-9eec-4e0c-8bcb-fc5c222767fb', '16552594542310pulg0a0010130000123', 'g2');
INSERT INTO `device_group_mapping` VALUES ('1109c168-31e8-4c98-8f2a-c5ff30b9ab7e', '16538390097670switch0300100500143', 'g3');
INSERT INTO `device_group_mapping` VALUES ('21c7c3ed-a16e-49a2-91ef-65a037e5ea8e', '16552594511210pulg0a001012000012c', 'g2');
INSERT INTO `device_group_mapping` VALUES ('2264466a-44ba-4683-82b8-07eb23dac9b1', '16552594604220pulg0a0010150000127', 'g2');
INSERT INTO `device_group_mapping` VALUES ('261b4e4c-3698-4bcb-acf7-a81973a7234a', '16552594646210pulg0a0010160000122', 'g1');
INSERT INTO `device_group_mapping` VALUES ('2b13756b-2bf5-48d7-acfc-387f9fe78942', '16538389971670switch0300100200140', 'g3');
INSERT INTO `device_group_mapping` VALUES ('326a687b-7191-46bf-a4a1-68a9c1dc2eeb', '16552595685220menci00010070000127', 'g1');
INSERT INTO `device_group_mapping` VALUES ('40ceaa68-b6b7-481e-85f3-cf70f198c4cd', '16538390853670pulg0a0010040000121', 'g3');
INSERT INTO `device_group_mapping` VALUES ('4a6f3852-41af-4d14-8ec7-d60e11ce90c3', '16538390048670switch0300100400141', 'g3');
INSERT INTO `device_group_mapping` VALUES ('610c1742-1107-4439-9a6d-af7b22e7a14c', '16538390787670pulg0a0010020000124', 'g3');
INSERT INTO `device_group_mapping` VALUES ('6e75d541-e370-403e-b37c-79d6521c1444', '16552594933210linght001007000012f', 'g2');
INSERT INTO `device_group_mapping` VALUES ('74990e21-3703-4fe0-b2c6-3c404da5c731', '16552594572370pulg0a001014000012e', 'g2');
INSERT INTO `device_group_mapping` VALUES ('77c87d0d-7fc1-4142-8423-5ef073d2313f', '16552594320310pulg0a0010070000126', 'g2');
INSERT INTO `device_group_mapping` VALUES ('803ee7b5-7e21-46fb-8065-650db7c20fd7', '16552594898210linght0010060000129', 'g1');
INSERT INTO `device_group_mapping` VALUES ('86102667-c607-4bc4-bc82-2eb39ea85ebf', '16552594863210linght001005000012c', 'g1');
INSERT INTO `device_group_mapping` VALUES ('89653bc8-421c-40ce-a65b-388df5e39272', '16538390008670switch0300100300145', 'g3');
INSERT INTO `device_group_mapping` VALUES ('8c5931a3-03ba-441d-b66e-3ecc36b1267d', '16552594368340pulg0a0010080000126', 'g2');
INSERT INTO `device_group_mapping` VALUES ('9a468cdd-ee06-488a-8ef1-71bed396a015', '16552594812210linght001004000012d', 'g1');
INSERT INTO `device_group_mapping` VALUES ('9c759298-f7cc-4bf8-9c37-97cfcc23d827', '16552594863210linght001005000012c', 'g2');
INSERT INTO `device_group_mapping` VALUES ('af242e63-c60e-45a0-a5e5-f72ac78f5c64', '16552595656210menci0001006000012d', 'g1');
INSERT INTO `device_group_mapping` VALUES ('bb50bc80-4d96-4f68-89c6-1764ef5b61ca', '16552594444210pulg0a0010100000128', 'g2');
INSERT INTO `device_group_mapping` VALUES ('e75f16aa-3083-445b-a2db-3ce70460e17b', '16552595723210menci0001008000012f', 'g1');
INSERT INTO `device_group_mapping` VALUES ('eb2f13cd-ea24-4654-a566-acedfc6259ae', '16552594933210linght001007000012f', 'g1');
INSERT INTO `device_group_mapping` VALUES ('f2f39afa-baf3-4b43-9762-401cddf78870', '16538390738670pulg0a0010010000125', 'g3');
INSERT INTO `device_group_mapping` VALUES ('ff077fc3-0a6a-4aed-b502-6db094452ed3', '16552594405220pulg0a0010090000124', 'g2');

-- ----------------------------
-- Table structure for device_info
-- ----------------------------
DROP TABLE IF EXISTS `device_info`;
CREATE TABLE `device_info`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备名称',
  `latitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '纬度',
  `longitude` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '经度',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备类型',
  `offline_time` bigint(0) NULL DEFAULT NULL COMMENT '设备离线时间',
  `online_time` bigint(0) NULL DEFAULT NULL COMMENT '设备在线时间',
  `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级id',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品key',
  `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备密钥',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备状态',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_info
-- ----------------------------
INSERT INTO `device_info` VALUES ('16465226744430aabbccdd22000000143', 1646522674443, '16465226744430aabbccdd22000000143', 'AA:BB:CC:DD:22', '30.604218', '104.090377', NULL, 1653380471302, 1653380299997, NULL, 'hbtgIA0SuVw9lxjB', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16465723448670abc1230000200000115', 1646572344867, '16465723448670abc1230000200000115', 'ABC12300002', '30.604218', '104.090377', 'S01', 1653380471930, 1653729780174, '16465226744430aabbccdd22000000143', 'Rf4QSjbm65X45753', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16465723451670abc123000030000011a', 1646572345167, '16465723451670abc123000030000011a', 'ABC12300003', '30.604218', '104.090377', 'S01', 1653380472221, 1653729780071, '16465226744430aabbccdd22000000143', 'Rf4QSjbm65X45753', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16465723454670abd1230000100000117', 1646572345467, '16465723454670abd1230000100000117', 'ABD12300001', '30.604218', '104.090377', 'F01', 1653380472498, 1653380308883, '16465226744430aabbccdd22000000143', 'hdX3PCMcFrCYpesJ', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16465723457670abd123000020000011c', 1646572345767, '16465723457670abd123000020000011c', 'ABD12300002', '30.604218', '104.090377', 'F01', 1653380472842, 1653380311848, '16465226744430aabbccdd22000000143', 'hdX3PCMcFrCYpesJ', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('1647690908735019dd9f03008d1500163', 1647690908735, '1647690908735019dd9f03008d1500163', '19DD9F03008D1500', '30.604218', '104.090377', 'device', 1655581808009, 1655581925247, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('164776322117404acb9f03008d1500168', 1647763221174, '164776322117404acb9f03008d1500168', '4ACB9F03008D1500', '30.604218', '104.090377', 'device', 1655581808049, 1655581925255, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('16477632215720c11b1602008d1500160', 1647763221572, '16477632215720c11b1602008d1500160', 'C11B1602008D1500', '30.604218', '104.090377', 'device', 1655581808052, 1655581925252, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('1647763221972019a89f03008d1500163', 1647763221972, '1647763221972019a89f03008d1500163', '19A89F03008D1500', '30.604218', '104.090377', 'device', 1655581808053, 1655581925157, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('164776322227201472a803008d150016e', 1647763222272, '164776322227201472a803008d150016e', '1472A803008D1500', '30.604218', '104.090377', 'device', 1655581808148, 1655581925152, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('16477632226720c2cc9f03008d1500166', 1647763222672, '16477632226720c2cc9f03008d1500166', 'C2CC9F03008D1500', '30.604218', '104.090377', 'device', 1655581808150, 1655581925148, '164785263238900cefafcfeeab0000125', 'cGCrkK7Ex4FESAwe', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('164785263238900cefafcfeeab0000125', 1647852632389, '164785263238900cefafcfeeab0000125', '0CEFAFCFEEAB', '30.604218', '104.090377', NULL, 1655581807987, 1655581924548, NULL, 'N523nWsCiG3CAn6X', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('165017126122400cefafcfee61000012b', 1650171261224, '165017126122400cefafcfee61000012b', '0CEFAFCFEE61', '30.604218', '104.090377', NULL, 1652669743471, 1652602115884, NULL, 'N523nWsCiG3CAn6X', NULL, 'offline', NULL);
INSERT INTO `device_info` VALUES ('16501806313260000833feffac33bc16c', 1650180631326, '16501806313260000833feffac33bc16c', '000833FEFFAC33BC', '30.604218', '104.090377', 'device', 1652669743479, 1652602130873, '165017126122400cefafcfee61000012b', 'cGCrkK7Ex4FESAwe', NULL, 'offline', '15620886-b30d-439d-9e5f-13a094c1f1f9');
INSERT INTO `device_info` VALUES ('16501898583770f4cce4feffbd1bec164', 1650189858377, '16501898583770f4cce4feffbd1bec164', 'F4CCE4FEFFBD1BEC', '30.604218', '104.090377', 'device', 1652669743574, 1655415536433, '165017126122400cefafcfee61000012b', 'PN3EDmkBZDD8whDd', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16514626212240aabbccee01000000143', 1651462621224, '16514626212240aabbccee01000000143', 'AA:BB:CC:EE:01', '30.604218', '104.090377', NULL, 1652593079381, 1653380312358, NULL, 'N523nWsCiG3CAn6X', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16514626214280abe1230000100000116', 1651462621428, '16514626214280abe1230000100000116', 'ABE12300001', '30.604218', '104.090377', 'S1', 1652593079473, 1653380317469, '16514626212240aabbccee01000000143', 'cGCrkK7Ex4FESAwe', NULL, 'offline', '15620886-b30d-439d-9e5f-13a094c1f1f9');
INSERT INTO `device_info` VALUES ('16514626216250abe1230000200000114', 1651462621625, '16514626216250abe1230000200000114', 'ABE12300002', '30.604218', '104.090377', 'S1', 1652593079484, 1653380319984, '16514626212240aabbccee01000000143', 'cGCrkK7Ex4FESAwe', NULL, 'offline', '15620886-b30d-439d-9e5f-13a094c1f1f9');
INSERT INTO `device_info` VALUES ('16514626218250abe124000010000011a', 1651462621825, '16514626218250abe124000010000011a', 'ABE12400001', '30.604218', '104.090377', 'S1', 1652593079579, 1653380322090, '16514626212240aabbccee01000000143', '6kYp6jszrDns2yh4', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16514627991620aabbccdd1000000014f', 1651462799162, '16514627991620aabbccdd1000000014f', 'AA:BB:CC:DD:10', '30.604218', '104.090377', 'GW01', 1653187754035, 1653185932725, NULL, 'hbtgIA0SuVw9lxjB', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16514657683280abe125000010000011f', 1651465768328, '16514657683280abe125000010000011f', 'ABE12500001', '30.604218', '104.090377', 'M1', NULL, 1653380321483, '16514626212240aabbccee01000000143', 'AWcJnf7ymGSkaz5M', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16534030209640test001230100000118', 1653403020971, '16534030209640test001230100000118', 'TEST0012301', '30.604218', '104.090377', NULL, NULL, 1653730979070, NULL, 'cGCrkK7Ex4FESAwe', 'C8YxCycFFeQDPKX4', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537590780810menci0001001000012b', 1653759078092, '16537590780810menci0001001000012b', 'MENCI0001001', '30.604218', '104.090377', NULL, NULL, 1653785232184, NULL, 'PN3EDmkBZDD8whDd', 'PwYEKSARBJjD4y6B', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537590838150menci00010020000128', 1653759083815, '16537590838150menci00010020000128', 'MENCI0001002', '30.604218', '104.090377', NULL, NULL, 1653785231786, NULL, 'PN3EDmkBZDD8whDd', 'AQHQJTQ2iXkncb3C', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537591055800menci0001003000012e', 1653759105580, '16537591055800menci0001003000012e', 'MENCI0001003', '30.604218', '104.090377', NULL, NULL, 1653785231385, NULL, 'PN3EDmkBZDD8whDd', '2D2k8mmHbi2AMh8G', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537594707840wenshidu0100100013c', 1653759470784, '16537594707840wenshidu0100100013c', 'WENSHIDU01001', '30.604218', '104.090377', NULL, NULL, 1653839927067, NULL, '6kYp6jszrDns2yh4', 'neSfPnhsjCsQiCQx', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537594752710wenshidu0100200013d', 1653759475271, '16537594752710wenshidu0100200013d', 'WENSHIDU01002', '30.604218', '104.090377', NULL, NULL, 1653839926777, NULL, '6kYp6jszrDns2yh4', '5bm8pNYcdj7YPMXn', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537594784840wenshidu0100300013a', 1653759478484, '16537594784840wenshidu0100300013a', 'WENSHIDU01003', '30.604218', '104.090377', NULL, NULL, 1653839926671, NULL, '6kYp6jszrDns2yh4', 'DSBbhJW7cGXRWeZA', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595195720fan0001001000000108', 1653759519572, '16537595195720fan0001001000000108', 'FAN0001001', '30.604218', '104.090377', NULL, NULL, NULL, NULL, 'hdX3PCMcFrCYpesJ', 'KSpeRjXRP8H7tcAn', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595248720fan0001002000000101', 1653759524872, '16537595248720fan0001002000000101', 'FAN0001002', '30.604218', '104.090377', NULL, NULL, NULL, NULL, 'hdX3PCMcFrCYpesJ', 't3NYS5p7ExYaWKx4', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595308820fan000100300000010d', 1653759530882, '16537595308820fan000100300000010d', 'FAN0001003', '30.604218', '104.090377', NULL, NULL, NULL, NULL, 'hdX3PCMcFrCYpesJ', 'fR3R3i8BYDKWAiDj', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595591780linght0010010000121', 1653759559178, '16537595591780linght0010010000121', 'LINGHT001001', '30.604218', '104.090377', NULL, NULL, 1653839919673, NULL, 'xpsYHExTKPFaQMS7', 'diRBkEREDt47MzWF', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595624750linght001002000012c', 1653759562475, '16537595624750linght001002000012c', 'LINGHT001002', '30.604218', '104.090377', NULL, NULL, 1653839919576, NULL, 'xpsYHExTKPFaQMS7', 'xrX2mrkQwf3YYaWc', 'offline', '1');
INSERT INTO `device_info` VALUES ('16537595658790linght0010030000128', 1653759565879, '16537595658790linght0010030000128', 'LINGHT001003', '30.604218', '104.090377', NULL, NULL, 1653839919567, NULL, 'xpsYHExTKPFaQMS7', 'JwKxnDWGrRcP8xAJ', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538383810690wenshidu01004000132', 1653838381074, '16538383810690wenshidu01004000132', 'WENSHIDU01004', '30.604218', '104.090377', NULL, NULL, 1655416485631, NULL, '6kYp6jszrDns2yh4', 'FkCQGREXYCmjzxaZ', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538383850710wenshidu01005000134', 1653838385071, '16538383850710wenshidu01005000134', 'WENSHIDU01005', '30.604218', '104.090377', NULL, NULL, 1655416485531, NULL, '6kYp6jszrDns2yh4', 'Crm28CTD6iw7hYw5', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538383880670wenshidu0100600013b', 1653838388067, '16538383880670wenshidu0100600013b', 'WENSHIDU01006', '30.604218', '104.090377', NULL, NULL, 1655416485433, NULL, '6kYp6jszrDns2yh4', 'fEbdXEayedpBx6wk', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538383910670wenshidu01007000136', 1653838391067, '16538383910670wenshidu01007000136', 'WENSHIDU01007', '30.604218', '104.090377', NULL, NULL, 1655416485335, NULL, '6kYp6jszrDns2yh4', 'ZxDQeQ8jhMrfx8eE', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538383943670wenshidu01008000139', 1653838394367, '16538383943670wenshidu01008000139', 'WENSHIDU01008', '30.604218', '104.090377', NULL, NULL, 1655416485331, NULL, '6kYp6jszrDns2yh4', 'pGxKYrNJC7rDhsr8', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538389915670switch030010010014c', 1653838991567, '16538389915670switch030010010014c', 'SWITCH03001001', '30.604218', '104.090377', NULL, NULL, 1653839918967, NULL, 'eDhXKwEzwFybM5R7', 'Js66kBbXRjXmcpMB', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538389971670switch0300100200140', 1653838997167, '16538389971670switch0300100200140', 'SWITCH03001002', '30.604218', '104.090377', NULL, NULL, 1653839918871, NULL, 'eDhXKwEzwFybM5R7', '4nrNrjnFMSQZB562', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390008670switch0300100300145', 1653839000867, '16538390008670switch0300100300145', 'SWITCH03001003', '30.604218', '104.090377', NULL, NULL, 1653839918771, NULL, 'eDhXKwEzwFybM5R7', 'mbs4PsY4atEtzcA3', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390048670switch0300100400141', 1653839004867, '16538390048670switch0300100400141', 'SWITCH03001004', '30.604218', '104.090377', NULL, NULL, 1653839918676, NULL, 'eDhXKwEzwFybM5R7', 'BQj5SZetsC3eGtfM', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390097670switch0300100500143', 1653839009767, '16538390097670switch0300100500143', 'SWITCH03001005', '30.604218', '104.090377', NULL, NULL, 1653839918667, NULL, 'eDhXKwEzwFybM5R7', 'PXHwHXH4a8YJzZ3S', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390738670pulg0a0010010000125', 1653839073867, '16538390738670pulg0a0010010000125', 'PULG0A001001', '30.604218', '104.090377', NULL, NULL, 1653839933474, NULL, 'cGCrkK7Ex4FESAwe', 'rtsZbST3tiJHccbe', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390787670pulg0a0010020000124', 1653839078767, '16538390787670pulg0a0010020000124', 'PULG0A001002', '30.604218', '104.090377', NULL, NULL, 1653839933376, NULL, 'cGCrkK7Ex4FESAwe', 'Jm7ecfyXawiend8K', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390820760pulg0a0010030000127', 1653839082076, '16538390820760pulg0a0010030000127', 'PULG0A001003', '30.604218', '104.090377', NULL, NULL, 1653839933367, NULL, 'cGCrkK7Ex4FESAwe', '7N4S6eYzMCjA7YfK', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390853670pulg0a0010040000121', 1653839085367, '16538390853670pulg0a0010040000121', 'PULG0A001004', '30.604218', '104.090377', NULL, NULL, 1653839933267, NULL, 'cGCrkK7Ex4FESAwe', 'S4SDRKscRXAn43bc', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390885690pulg0a0010050000126', 1653839088569, '16538390885690pulg0a0010050000126', 'PULG0A001005', '30.604218', '104.090377', NULL, NULL, 1653839933167, NULL, 'cGCrkK7Ex4FESAwe', 'CN8ZGpcAheAbsDn2', 'offline', '1');
INSERT INTO `device_info` VALUES ('16538390924670pulg0a001006000012a', 1653839092467, '16538390924670pulg0a001006000012a', 'PULG0A001006', '30.604218', '104.090377', NULL, NULL, 1653839933067, NULL, 'cGCrkK7Ex4FESAwe', 'FFSdNXDRJbnE8fm2', 'offline', '1');
INSERT INTO `device_info` VALUES ('16542484163750d60ee9025d8430e327', 1654248416391, '16542484163750d60ee9025d8430e327', '2475b491e72541f7ad60ee9025d8430e', '30.604218', '104.090377', NULL, NULL, NULL, NULL, 'Eit3kmGJtxSHfCKT', 'bWkiEmY6wKM8WQP5', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594320310pulg0a0010070000126', 1655259432067, '16552594320310pulg0a0010070000126', 'PULG0A001007', '30.604218', '104.090377', NULL, NULL, 1655652739372, NULL, 'cGCrkK7Ex4FESAwe', 'NnnHaibdHJXMxNbC', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594368340pulg0a0010080000126', 1655259436834, '16552594368340pulg0a0010080000126', 'PULG0A001008', '30.604218', '104.090377', NULL, NULL, 1655652739272, NULL, 'cGCrkK7Ex4FESAwe', 'Ek3ZjwPayCymDxhN', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594405220pulg0a0010090000124', 1655259440522, '16552594405220pulg0a0010090000124', 'PULG0A001009', '30.604218', '104.090377', NULL, NULL, 1655652739174, NULL, 'cGCrkK7Ex4FESAwe', 'Z6yMp6D6mHKMjFi7', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594444210pulg0a0010100000128', 1655259444422, '16552594444210pulg0a0010100000128', 'PULG0A001010', '30.604218', '104.090377', NULL, NULL, 1655652739076, NULL, 'cGCrkK7Ex4FESAwe', 'HMxT2rQ55bzeRNJx', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594475270pulg0a0010110000125', 1655259447528, '16552594475270pulg0a0010110000125', 'PULG0A001011', '30.604218', '104.090377', NULL, NULL, 1655652739072, NULL, 'cGCrkK7Ex4FESAwe', 'pGC4z4TrbjZhii2m', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594511210pulg0a001012000012c', 1655259451121, '16552594511210pulg0a001012000012c', 'PULG0A001012', '30.604218', '104.090377', NULL, NULL, 1655652738972, NULL, 'cGCrkK7Ex4FESAwe', '5yHFHseBHDbCK3eZ', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594542310pulg0a0010130000123', 1655259454231, '16552594542310pulg0a0010130000123', 'PULG0A001013', '30.604218', '104.090377', NULL, NULL, 1655652738872, NULL, 'cGCrkK7Ex4FESAwe', 'FtxYcTM5p6B5GyPk', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594572370pulg0a001014000012e', 1655259457237, '16552594572370pulg0a001014000012e', 'PULG0A001014', '30.604218', '104.090377', NULL, NULL, 1655652738773, NULL, 'cGCrkK7Ex4FESAwe', '86pTYbQxxX3wd2FR', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594604220pulg0a0010150000127', 1655259460422, '16552594604220pulg0a0010150000127', 'PULG0A001015', '30.604218', '104.090377', NULL, NULL, 1655652738676, NULL, 'cGCrkK7Ex4FESAwe', 'TBwbHJdJBQCpGdHi', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594646210pulg0a0010160000122', 1655259464621, '16552594646210pulg0a0010160000122', 'PULG0A001016', '30.604218', '104.090377', NULL, NULL, 1655652738672, NULL, 'cGCrkK7Ex4FESAwe', '3KdPNBKZEyDGaNYK', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594812210linght001004000012d', 1655259481221, '16552594812210linght001004000012d', 'LINGHT001004', '30.604218', '104.090377', NULL, NULL, 1655416465231, NULL, 'xpsYHExTKPFaQMS7', 'PDkTxHPfw2H4WZjQ', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594863210linght001005000012c', 1655259486321, '16552594863210linght001005000012c', 'LINGHT001005', '30.604218', '104.090377', NULL, NULL, 1655416465133, NULL, 'xpsYHExTKPFaQMS7', 'xnKZZyPKj2rH6ee3', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594898210linght0010060000129', 1655259489821, '16552594898210linght0010060000129', 'LINGHT001006', '30.604218', '104.090377', NULL, NULL, 1655416465035, NULL, 'xpsYHExTKPFaQMS7', 'p72KAZyTA42zi8cJ', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552594933210linght001007000012f', 1655259493321, '16552594933210linght001007000012f', 'LINGHT001007', '30.604218', '104.090377', NULL, NULL, 1655416465031, NULL, 'xpsYHExTKPFaQMS7', 'mkQiSDk4rF3EWKFM', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552595656210menci0001006000012d', 1655259565621, '16552595656210menci0001006000012d', 'MENCI0001006', '30.604218', '104.090377', NULL, NULL, 1655415536135, NULL, 'PN3EDmkBZDD8whDd', 'ePSkE7bzGMsR6rTs', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552595685220menci00010070000127', 1655259568522, '16552595685220menci00010070000127', 'MENCI0001007', '30.604218', '104.090377', NULL, NULL, 1655415536130, NULL, 'PN3EDmkBZDD8whDd', 'eN4PjDhH4yx4mrmi', 'offline', '1');
INSERT INTO `device_info` VALUES ('16552595723210menci0001008000012f', 1655259572321, '16552595723210menci0001008000012f', 'MENCI0001008', '30.604218', '104.090377', NULL, NULL, 1655415536032, NULL, 'PN3EDmkBZDD8whDd', 'rMAweEJrE7cxbQWa', 'offline', '1');
INSERT INTO `device_info` VALUES ('168187356997901234567891230000120', 1646522674443, '168187356997901234567891230000120', '123456789123', '30.604218', '104.090377', NULL, 1653380471302, 1653380299997, NULL, 'BRD3x4fkKxkaxXFt', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('168191541600402017121609130000126', 1646522674443, '168191541600402017121609130000126', '201712160913', '30.604218', '104.090377', NULL, 1653380471302, 1653380299997, '168187356997901234567891230000120', 'PwMfpXmp4ZWkGahn', NULL, 'offline', '1');
INSERT INTO `device_info` VALUES ('16891443313530testgateway0100013d', 1689144331356, '16891443313530testgateway0100013d', 'testgateway01', '22.583686', '113.865453', NULL, 0, 0, NULL, 'openiitagateway01', 'bnw2Z6zNxxdBtm6N', 'offline', '1');
INSERT INTO `device_info` VALUES ('16891445583920testpump0100000010f', 1689144558392, '16891445583920testpump0100000010f', 'testpump01', NULL, NULL, NULL, 0, 0, '16891443313530testgateway0100013d', 'openiitapump01', '8cB3W8NzjP3rFY6W', 'offline', '1');
INSERT INTO `device_info` VALUES ('16895701241450testnbpump010000125', 1689570124148, '16895701241450testnbpump010000125', 'testnbpump01', '22.583686', '113.865453', NULL, 0, 0, NULL, 'openiitanbpump01', 'eY45WnZEWRryR2xt', 'offline', '1');

-- ----------------------------
-- Table structure for device_ota_detail
-- ----------------------------
DROP TABLE IF EXISTS `device_ota_detail`;
CREATE TABLE `device_ota_detail`  (
  `id` bigint(0) NOT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ota_info_id` bigint(0) NULL DEFAULT NULL,
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `step` int(0) NULL DEFAULT NULL,
  `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_ota_detail
-- ----------------------------

-- ----------------------------
-- Table structure for device_ota_info
-- ----------------------------
DROP TABLE IF EXISTS `device_ota_info`;
CREATE TABLE `device_ota_info`  (
  `id` bigint(0) NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `fail` int(0) NULL DEFAULT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `package_id` bigint(0) NULL DEFAULT NULL,
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `success` int(0) NULL DEFAULT NULL,
  `total` int(0) NULL DEFAULT NULL,
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_ota_info
-- ----------------------------

-- ----------------------------
-- Table structure for device_sub_user
-- ----------------------------
DROP TABLE IF EXISTS `device_sub_user`;
CREATE TABLE `device_sub_user`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_sub_user
-- ----------------------------

-- ----------------------------
-- Table structure for device_tag
-- ----------------------------
DROP TABLE IF EXISTS `device_tag`;
CREATE TABLE `device_tag`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签码',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_tag
-- ----------------------------

-- ----------------------------
-- Table structure for home
-- ----------------------------
DROP TABLE IF EXISTS `home`;
CREATE TABLE `home`  (
  `id` bigint(0) NOT NULL COMMENT '家庭id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭地址',
  `current` bit(1) NULL DEFAULT NULL COMMENT '是否为用户当前使用的家庭',
  `device_num` int(0) NULL DEFAULT NULL COMMENT '设备数量',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭名称',
  `space_num` int(0) NULL DEFAULT NULL COMMENT '空间数量',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '关联用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of home
-- ----------------------------
INSERT INTO `home` VALUES (1, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', '', b'1', 0, '我的家庭', 0, NULL, 1);
INSERT INTO `home` VALUES (2, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, b'1', NULL, '我的家', NULL, NULL, 3);
INSERT INTO `home` VALUES (3, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', '广东省深圳市南山区西丽镇', b'1', NULL, '我深圳的家', NULL, NULL, 1);
INSERT INTO `home` VALUES (4, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', '广东省深圳市南山区西丽镇', NULL, NULL, '我深圳的家', NULL, NULL, 1);
INSERT INTO `home` VALUES (5, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', '广东省深圳市南山区西丽镇', NULL, NULL, '我深圳的家', NULL, NULL, 1);
INSERT INTO `home` VALUES (6, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', '广东省深圳市南山区西丽镇', NULL, NULL, '我深圳的家', NULL, NULL, 1);
INSERT INTO `home` VALUES (7, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', 'shengzhen nanshan', NULL, NULL, 'myhome', NULL, NULL, 1);
INSERT INTO `home` VALUES (8, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', 'shengzhen nanshan', NULL, NULL, 'myhome', NULL, NULL, 1);

-- ----------------------------
-- Table structure for notify_message
-- ----------------------------
DROP TABLE IF EXISTS `notify_message`;
CREATE TABLE `notify_message`  (
  `id` bigint(0) NOT NULL COMMENT '通知消息id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `message_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` bit(1) NULL DEFAULT NULL,
  `update_at` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notify_message
-- ----------------------------
INSERT INTO `notify_message` VALUES (31, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'0', 1683816661690);
INSERT INTO `notify_message` VALUES (5756, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (9789, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'0', 1683816661690);
INSERT INTO `notify_message` VALUES (56856, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (67567, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (90890, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (151515, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'0', 1683816661690);
INSERT INTO `notify_message` VALUES (1231312, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (4324234, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (4534346, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'0', 1683816661690);
INSERT INTO `notify_message` VALUES (4534636, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);
INSERT INTO `notify_message` VALUES (64432342, '你的设备【热水器】温度过高', 1683816661690, 'alert', b'1', 1683816661690);

-- ----------------------------
-- Table structure for oauth_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client`;
CREATE TABLE `oauth_client`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'id',
  `allow_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '允许访问的url',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端id',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端密钥',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_client
-- ----------------------------
INSERT INTO `oauth_client` VALUES ('013cfba8-538b-4b51-a90e-10ae4a6f7816', '*', 'iotkit', 'b86cb53d-c005-48a3-bb02-3c262151b68c', 1711303622818, '奇特物联');
INSERT INTO `oauth_client` VALUES ('2febb8cd-4c16-4c99-9c88-21b967794406', '*', 'dueros', '750c67c2-29cb-40c3-bf4d-c0b9bf3eed88', 1711303622827, '小度音箱');

-- ----------------------------
-- Table structure for oper_log
-- ----------------------------
DROP TABLE IF EXISTS `oper_log`;
CREATE TABLE `oper_log`  (
  `id` bigint(0) NOT NULL COMMENT '日志主键',
  `business_type` int(0) NULL DEFAULT NULL COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `cost_time` bigint(0) NULL DEFAULT NULL COMMENT '消耗时间',
  `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误消息',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回参数',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `oper_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作地点',
  `oper_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人员',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求url',
  `operator_type` int(0) NULL DEFAULT NULL COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `request_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方式',
  `status` int(0) NULL DEFAULT NULL COMMENT '操作状态（0正常 1异常）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作模块',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oper_log
-- ----------------------------
INSERT INTO `oper_log` VALUES (516580177526853, 2, 32, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"8ab12839-a039-4fad-8afe-bc5c0c98354f\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.443+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127357415493,\"parentId\":2210,\"menuName\":\"插件添加\",\"orderNum\":1,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot: plugin:add\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:10:09', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580271538245, 2, 6, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"f3bfc9df-cacc-4560-8c6f-8984d231675a\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.444+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127532781637,\"parentId\":2210,\"menuName\":\"插件修改\",\"orderNum\":1,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:edit\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:10:32', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580303495237, 2, 7, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"e5e6b549-ae57-4265-9655-1f72768289bc\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.443+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127357415493,\"parentId\":2210,\"menuName\":\"插件添加\",\"orderNum\":1,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:add\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:10:40', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580369678405, 2, 5, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"413f5711-9b51-4b53-9c07-4a18a5d369f3\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.446+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127596064837,\"parentId\":2210,\"menuName\":\"插件查询\",\"orderNum\":1,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:query\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:10:56', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580424007749, 2, 6, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"564af98c-3af4-4b8b-8637-6a008d34fc6d\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.447+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127705182277,\"parentId\":2210,\"menuName\":\"插件删除\",\"orderNum\":1,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:remove\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:11:10', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580484644933, 2, 5, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"38ff89ff-6243-4c54-aa0b-735e83377836\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.444+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127532781637,\"parentId\":2210,\"menuName\":\"插件修改\",\"orderNum\":2,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:edit\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:11:24', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580523356229, 2, 6, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"19366ec6-55f5-49cd-8f82-e7b592cc0dd1\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.447+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127705182277,\"parentId\":2210,\"menuName\":\"插件删除\",\"orderNum\":3,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:remove\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:11:34', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580549386309, 2, 5, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysMenuController.edit()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"4beb5d69-7e4e-4826-abe8-cc7d860fe814\",\"data\":{\"createDept\":null,\"createBy\":null,\"createTime\":\"2024-02-18T11:05:22.446+00:00\",\"updateBy\":null,\"updateTime\":null,\"id\":442127596064837,\"parentId\":2210,\"menuName\":\"插件查询\",\"orderNum\":4,\"path\":\"\",\"component\":null,\"queryParam\":null,\"isFrame\":\"1\",\"isCache\":\"0\",\"menuType\":\"F\",\"visible\":\"0\",\"status\":\"0\",\"perms\":\"iot:plugin:query\",\"icon\":\"\",\"remark\":null}}', '2024-02-18 11:11:40', '/system/menu/edit', 1, 'POST', 0, '000000', '菜单管理');
INSERT INTO `oper_log` VALUES (516580700786757, 3, 29, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysUserController.remove()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"32db1317-b4ee-42c8-8b44-ad077b13468d\",\"data\":[3]}', '2024-02-18 11:12:17', '/system/user/delete', 1, 'POST', 0, '000000', '用户管理');
INSERT INTO `oper_log` VALUES (516580742733893, 3, 8, NULL, NULL, NULL, 'cc.iotkit.system.controller.SysUserController.remove()', '127.0.0.1', '内网IP', 'admin', '{\"requestId\":\"84a35cb9-b99f-4bdc-9977-a379fc964699\",\"data\":[2]}', '2024-02-18 11:12:27', '/system/user/delete', 1, 'POST', 0, '000000', '用户管理');
INSERT INTO `oper_log` VALUES (1665641342438514690, 6, 199, '', NULL, NULL, 'org.dromara.generator.controller.GenController.importTableSave()', '127.0.0.1', '内网IP', 'admin', '\"sys_role_dept,sys_role_menu,sysConfig,sysDictData,sysDictType,sysLogininfor,sysNotice,sys_operLog,sys_oss,sys_ossConfig\"', NULL, '/tool/gen/importTable', 1, NULL, 0, '000000', '代码生成');

-- ----------------------------
-- Table structure for ota_device
-- ----------------------------
DROP TABLE IF EXISTS `ota_device`;
CREATE TABLE `ota_device`  (
  `id` bigint(0) NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `device_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` int(0) NULL DEFAULT NULL,
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ota_device
-- ----------------------------

-- ----------------------------
-- Table structure for ota_package
-- ----------------------------
DROP TABLE IF EXISTS `ota_package`;
CREATE TABLE `ota_package`  (
  `id` bigint(0) NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ext_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_diff` bit(1) NULL DEFAULT NULL,
  `md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sign` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sign_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `size` bigint(0) NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ota_package
-- ----------------------------

-- ----------------------------
-- Table structure for plugin_info
-- ----------------------------
DROP TABLE IF EXISTS `plugin_info`;
CREATE TABLE `plugin_info`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '插件配置信息',
  `config_schema` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '插件配置项描述信息',
  `deploy_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部署方式',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件包地址',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件名称',
  `plugin_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件包id',
  `protocol` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备插件协议类型',
  `script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '插件脚本',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件类型',
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plugin_info
-- ----------------------------
INSERT INTO `plugin_info` VALUES (516835985375301, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, '[\n  {\n    \"id\": \"port\",\n    \"name\": \"端口\",\n    \"type\": \"number\",\n    \"value\": 9081,\n    \"desc\": \"http端口，默认为9081\"\n  },\n  {\n    \"id\": \"a\",\n    \"name\": \"测试参数1\",\n    \"type\": \"radio\",\n    \"value\": 0,\n    \"desc\": \"单选参数a\",\n    \"options\": [\n      {\n        \"name\": \"值0\",\n        \"value\": 0\n      },\n      {\n        \"name\": \"值1\",\n        \"value\": 11\n      }\n    ]\n  }\n]', 'upload', 'http示例插件，配置参数：端口(port)默认9081', 'http-plugin-1.0.1-repackage.jar', 'http插件', 'http-plugin', NULL, '', 'running', NULL, NULL, '1.0.1');
INSERT INTO `plugin_info` VALUES (516836007116869, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, '[\n  {\n    \"id\": \"port\",\n    \"name\": \"端口\",\n    \"type\": \"number\",\n    \"value\": 1883,\n    \"desc\": \"mqtt端口，默认为1883\"\n  }\n]', 'upload', 'mqtt示例插件', 'mqtt-plugin-1.0.1-repackage.jar', 'mqtt插件', 'mqtt-plugin', NULL, '', 'running', NULL, NULL, '1.0.1');
INSERT INTO `plugin_info` VALUES (516836027760709, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, '[\n  {\n    \"id\": \"host\",\n    \"name\": \"绑定ip\",\n    \"type\": \"text\",\n    \"value\": \"127.0.0.1\",\n    \"desc\": \"tcp绑定ip，默认为127.0.0.1\"\n  },\n  {\n    \"id\": \"port\",\n    \"name\": \"端口\",\n    \"type\": \"number\",\n    \"value\": 6883,\n    \"desc\": \"tcp端口，默认为6883\"\n  }\n]', 'upload', 'tcp示例插件', 'tcp-plugin-1.0.4-repackage.jar', 'tcp插件', 'tcp-plugin', NULL, 'function hexToByte(hexString) {\n    if (hexString.length % 2 !== 0) {\n        throw new Error(\'Invalid hex string. String must have an even number of characters.\');\n    }\n\n    let byteArray = [];\n    for (let i = 0; i < hexString.length; i += 4) {\n        byteArray.push(parseInt(hexString.substr(i, 4), 16));\n    }\n\n    return byteArray;\n}\nfunction byteToHex(bytes) {\n    for (var hex = [], i = 0; i < bytes.length; i++) {\n        hex.push((bytes[i] >>> 4).toString(16));\n        hex.push((bytes[i] & 0xF).toString(16));\n    }\n    return hex.join(\"\");\n}\n\nthis.decode=function(data){\n    hex=data.payload;\n    const bytes=hexToByte(hex);\n    return {\n        \"rssi\":bytes[0],\n        \"powerstate\":bytes[1]\n    };\n}\n\nthis.encode=function(params){\n    const hex=byteToHex([params.powerstate]);\n    return hex;\n}', 'running', NULL, NULL, '1.0.4');

-- ----------------------------
-- Table structure for plugin_instance
-- ----------------------------
DROP TABLE IF EXISTS `plugin_instance`;
CREATE TABLE `plugin_instance`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `heartbeat_at` bigint(0) NULL DEFAULT NULL COMMENT '心跳时间',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件主程序所在ip',
  `main_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '插件主程序id',
  `plugin_id` bigint(0) NULL DEFAULT NULL COMMENT '插件id',
  `port` int(0) NOT NULL COMMENT '插件主程序端口',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plugin_instance
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(0) NOT NULL COMMENT '产品id',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品类',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `is_open_locate` bit(1) NULL DEFAULT NULL COMMENT '是否开启设备定位,true/false',
  `keep_alive_time` bigint(0) NULL DEFAULT NULL COMMENT '保活时长（秒）',
  `locate_update_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '定位更新方式',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品名称',
  `node_type` int(0) NULL DEFAULT NULL COMMENT '节点类型',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品key',
  `product_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品密钥',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `transparent` bit(1) NULL DEFAULT NULL COMMENT '是否透传,true/false',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 'OpenIitaGateway', 1649653149339, NULL, b'1', NULL, 'manual', '铱塔智联智能网关01', 0, 'openiitagateway01', 'openiitasecret01', NULL, b'0', '1');
INSERT INTO `product` VALUES (2, 'OpenIitaPump', 1649653149339, NULL, b'0', NULL, 'manual', '铱塔智联-单泵01', 1, 'openiitapump01', 'openiitapump01', NULL, b'0', '1');
INSERT INTO `product` VALUES (3, 'OpenIitaPump', 1649653149339, NULL, b'1', NULL, 'device', 'NB透传水泵', 2, 'openiitanbpump01', 'openiitanbpump01', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994839621, 'light', 1650174777304, NULL, b'1', NULL, 'manual', '调光灯', 1, 'xpsYHExTKPFaQMS7', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994847813, 'fan', 1646571291131, NULL, b'0', NULL, NULL, '智能风扇', 1, 'hdX3PCMcFrCYpesJ', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994860101, 'gateway', 1652238155938, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/hbtgIA0SuVw9lxjB/cover.jpg?Expires=1967598154&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=2gh2jad14mVHGvWThwOd%2FykiB5g%3D', b'0', NULL, NULL, 'GW01网关', 0, 'hbtgIA0SuVw9lxjB', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994864197, 'switch', 1652238173536, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/eDhXKwEzwFybM5R7/cover.jpeg?Expires=1967598172&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=ZrFgANkomVEDQRV5JdmONL0S2sY%3D', b'0', NULL, NULL, '三路开关', 1, 'eDhXKwEzwFybM5R7', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994876485, 'SmartPlug', 1652238138626, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/cGCrkK7Ex4FESAwe/cover.jpeg?Expires=1967598137&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=vOjqav0pRZqQFgx8xBo99WhgWXk%3D', b'0', NULL, NULL, '插座', 1, 'cGCrkK7Ex4FESAwe', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994880581, 'switch', 1652238147123, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/Rf4QSjbm65X45753/cover.jpeg?Expires=1967598145&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=ksQhmEm5Rn7C7FFqY09o9l%2BZ%2BIQ%3D', b'0', NULL, NULL, '一路开关', 1, 'Rf4QSjbm65X45753', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994884677, 'door', 1650190400357, 'null', b'0', NULL, NULL, '门磁', 1, 'PN3EDmkBZDD8whDd', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994896965, 'gateway', 1652237643216, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/N523nWsCiG3CAn6X/cover.jpg?Expires=1967597641&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=%2BaGcHBT%2FHA3s%2BrZ687U50b4YE0A%3D', b'0', NULL, NULL, 'ZGW01', 0, 'N523nWsCiG3CAn6X', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994901061, 'meter', 1654237604221, NULL, b'0', NULL, NULL, '燃气表', 2, 'Eit3kmGJtxSHfCKT', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994905157, 'switch', 1652238202310, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/DSGxxKk6E8mmDk6C/cover.jpeg?Expires=1967598201&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=k2PqHc%2BI14DfCwD8kQIflwoBAog%3D', b'0', NULL, NULL, '通断器', 1, 'DSGxxKk6E8mmDk6C', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994909253, 'switch', 1650187781637, NULL, b'0', NULL, NULL, '四路场景面板', 1, 'D8c5pXFmt2KJDxNm', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994913349, 'SmartPlug', 1652279098100, 'http://iotkit-img.oss-cn-shenzhen.aliyuncs.com/product/AWcJnf7ymGSkaz5M/cover.jpeg?Expires=1967598035&OSSAccessKeyId=LTAI5tGEHNoVu5tWHUWnosrs&Signature=tXzWH5%2B4JNcnuTFrJbvGwsbx97c%3D', b'0', NULL, NULL, 'smart pulg', 1, 'AWcJnf7ymGSkaz5M', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'1', '1');
INSERT INTO `product` VALUES (516578994925637, 'sensor', 1649653149339, NULL, b'0', NULL, NULL, '温湿度传感器', 1, '6kYp6jszrDns2yh4', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994933829, 'FreshAir', 1649653149339, NULL, b'0', NULL, NULL, '新风', 1, 'bGdZt8ffBETtsirm', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994942021, 'gateway', 1649653149339, NULL, b'0', NULL, NULL, '智能电表采集器', 0, 'BRD3x4fkKxkaxXFt', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');
INSERT INTO `product` VALUES (516578994958405, 'SmartMeter', 1649653149339, NULL, b'0', NULL, NULL, '智能电表', 1, 'PwMfpXmp4ZWkGahn', 'xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU', NULL, b'0', '1');

-- ----------------------------
-- Table structure for product_model
-- ----------------------------
DROP TABLE IF EXISTS `product_model`;
CREATE TABLE `product_model`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '型号id',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '型号',
  `modify_at` bigint(0) NULL DEFAULT NULL COMMENT '修改时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品Key',
  `script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '脚本内容',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '脚本状态',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '脚本类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_model
-- ----------------------------
INSERT INTO `product_model` VALUES ('M1', NULL, 1711303622927, '型号1', 'AWcJnf7ymGSkaz5M', '\nfunction decode(msg)\n   return {\n        [\'identifier\'] = \'report\',\n        [\'mid\'] = \'1\',\n        [\'type\'] = \'property\',\n        [\'data\'] ={\n            [\'power\']=string.sub(msg.data,3,3)\n        }\n    }\nend\n\nfunction encode(service)\n    return {\n	[\'mid\'] = 1,\n	[\'model\'] = \'M1\',\n	[\'mac\'] = service.deviceName,\n	[\'data\'] = \'BB2\'\n  }\nend\n', 'publish', 'LuaScript');

-- ----------------------------
-- Table structure for rule_info
-- ----------------------------
DROP TABLE IF EXISTS `rule_info`;
CREATE TABLE `rule_info`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则id',
  `actions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '动作',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `filters` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '过滤器',
  `listeners` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '监听器',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则名称',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则类型',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rule_info
-- ----------------------------
INSERT INTO `rule_info` VALUES ('2820c218-660e-48ff-a234-c7b6793a5bb8', '[]', 1649167998895, 'test', '[]', '[]', '测试场景1', 'stopped', 'scene', '1');
INSERT INTO `rule_info` VALUES ('2c10229b-dcb2-439e-b411-5425b49657a1', '[]', 1652515471242, '开关插座开关状态推送', '[]', '[]', '小度设备属性更新推送', 'running', 'flow', '1');

-- ----------------------------
-- Table structure for screen
-- ----------------------------
DROP TABLE IF EXISTS `screen`;
CREATE TABLE `screen`  (
  `id` bigint(0) NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `is_default` bit(1) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `port` int(0) NULL DEFAULT NULL,
  `resource_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of screen
-- ----------------------------

-- ----------------------------
-- Table structure for screen_api
-- ----------------------------
DROP TABLE IF EXISTS `screen_api`;
CREATE TABLE `screen_api`  (
  `id` bigint(0) NOT NULL,
  `api_params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `api_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL,
  `data_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `http_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `screen_id` bigint(0) NULL DEFAULT NULL,
  `script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of screen_api
-- ----------------------------

-- ----------------------------
-- Table structure for space
-- ----------------------------
DROP TABLE IF EXISTS `space`;
CREATE TABLE `space`  (
  `id` bigint(0) NOT NULL COMMENT '空间id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `device_num` int(0) NULL DEFAULT NULL COMMENT '设备数量',
  `home_id` bigint(0) NULL DEFAULT NULL COMMENT '关联家庭id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间名称',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space
-- ----------------------------
INSERT INTO `space` VALUES (1, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '123', NULL);
INSERT INTO `space` VALUES (2, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '123', NULL);
INSERT INTO `space` VALUES (3, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '客厅', NULL);
INSERT INTO `space` VALUES (4, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '卧室', NULL);
INSERT INTO `space` VALUES (5, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '客厅', NULL);
INSERT INTO `space` VALUES (6, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '阳台', NULL);
INSERT INTO `space` VALUES (7, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '厨房', NULL);
INSERT INTO `space` VALUES (8, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '卧室', NULL);
INSERT INTO `space` VALUES (9, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, 1, '客厅', NULL);

-- ----------------------------
-- Table structure for space_device
-- ----------------------------
DROP TABLE IF EXISTS `space_device`;
CREATE TABLE `space_device`  (
  `id` bigint(0) NOT NULL,
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `collect` bit(1) NULL DEFAULT NULL COMMENT '是否收藏',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间中的设备id',
  `home_id` bigint(0) NULL DEFAULT NULL COMMENT '所属家庭Id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '空间中的设备名称',
  `space_id` bigint(0) NULL DEFAULT NULL COMMENT '空间id',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of space_device
-- ----------------------------
INSERT INTO `space_device` VALUES (1, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'1', '164785263238900cefafcfeeab0000125', 1, '卧室的ZGW01', 1, NULL);
INSERT INTO `space_device` VALUES (2, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'1', '164776322117404acb9f03008d1500168', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (3, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '1647763221972019a89f03008d1500163', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (4, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '164776322227201472a803008d150016e', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (5, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '16477632226720c2cc9f03008d1500166', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (6, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '16477632215720c11b1602008d1500160', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (7, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '1647690908735019dd9f03008d1500163', 1, '卧室的插座', 1, NULL);
INSERT INTO `space_device` VALUES (8, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '16538390924670pulg0a001006000012a', 1, '阳台的插座', 1, NULL);
INSERT INTO `space_device` VALUES (9, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '16501806313260000833feffac33bc16c', 1, '灯的插座', 1, NULL);
INSERT INTO `space_device` VALUES (10, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', b'0', '165017126122400cefafcfee61000012b', 1, '客厅网关', 1, NULL);

-- ----------------------------
-- Table structure for sys_app
-- ----------------------------
DROP TABLE IF EXISTS `sys_app`;
CREATE TABLE `sys_app`  (
  `id` bigint(0) NOT NULL COMMENT '主键id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appId',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用名称',
  `app_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'appSecret',
  `app_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用类型',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_app
-- ----------------------------
INSERT INTO `sys_app` VALUES (453554819821637, 1, NULL, '2024-03-24 18:07:06', 1, '2024-03-24 18:07:06', 'xxx', '微信小程序', 'xxx', '1', '微信小程序', '000000');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(0) NOT NULL COMMENT '参数主键',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数键名',
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数名称',
  `config_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统内置（Y是 N否）',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数键值',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.index.skinName', '主框架页-默认皮肤样式名称', 'Y', 'skin-blue', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', '000000');
INSERT INTO `sys_config` VALUES (2, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.user.initPassword', '用户管理-账号初始密码', 'Y', '123456', '初始化密码 123456', '000000');
INSERT INTO `sys_config` VALUES (3, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.index.sideTheme', '主框架页-侧边栏主题', 'Y', 'theme-dark', '深色主题theme-dark，浅色主题theme-light', '000000');
INSERT INTO `sys_config` VALUES (5, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.account.registerUser', '账号自助-是否开启用户注册功能', 'Y', 'false', '是否开启注册用户功能（true开启，false关闭）', '000000');
INSERT INTO `sys_config` VALUES (11, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.oss.previewListResource', 'OSS预览列表资源开关', 'Y', 'true', 'true:开启, false:关闭', '000000');
INSERT INTO `sys_config` VALUES (452767972696133, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.index.skinName', '主框架页-默认皮肤样式名称', 'Y', 'skin-blue', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', '452748015218757');
INSERT INTO `sys_config` VALUES (452767972700229, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.user.initPassword', '用户管理-账号初始密码', 'Y', '123456', '初始化密码 123456', '452748015218757');
INSERT INTO `sys_config` VALUES (452767972704325, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.index.sideTheme', '主框架页-侧边栏主题', 'Y', 'theme-dark', '深色主题theme-dark，浅色主题theme-light', '452748015218757');
INSERT INTO `sys_config` VALUES (452767972708421, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'sys.account.registerUser', '账号自助-是否开启用户注册功能', 'Y', 'false', '是否开启注册用户功能（true开启，false关闭）', '452748015218757');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint(0) NOT NULL COMMENT '部门ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `ancestors` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '祖级列表',
  `del_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `leader` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `order_num` int(0) NULL DEFAULT NULL COMMENT '显示顺序',
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父部门ID',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门状态:0正常,1停用',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0', '0', '铱塔智联', 'xxx@qq.com', 'openiita', 0, 0, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (101, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100', '0', '深圳总公司', 'xxx@qq.com', 'openiita', 1, 100, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (102, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100', '0', '长沙分公司', 'xxx@qq.com', 'openiita', 2, 100, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (103, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,101', '0', '研发部门', 'xxx@qq.com', 'openiita', 1, 101, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (104, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,101', '0', '市场部门', 'xxx@qq.com', 'openiita', 2, 101, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (105, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,101', '0', '测试部门', 'xxx@qq.com', 'openiita', 3, 101, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (106, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,101', '0', '财务部门', 'xxx@qq.com', 'openiita', 4, 101, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (107, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,101', '0', '运维部门', 'xxx@qq.com', 'openiita', 5, 101, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (108, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,102', '0', '市场部门', 'xxx@qq.com', 'openiita', 1, 102, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (109, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0,100,102', '0', '财务部门', 'xxx@qq.com', 'openiita', 2, 102, '15888888888', '0', '000000');
INSERT INTO `sys_dept` VALUES (452767971254341, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '0', '0', '测试租户有限公司', NULL, 'test', NULL, 0, '18888888888', '0', '452748015218757');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` bigint(0) NOT NULL COMMENT '字典编码',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `css_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `dict_label` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典标签',
  `dict_sort` int(0) NULL DEFAULT NULL COMMENT '字典排序',
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `dict_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典键值',
  `is_default` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否默认（Y是 N否）',
  `list_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格字典样式',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '男', 1, 'sys_user_sex', '0', 'Y', '', '性别男', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (2, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '女', 2, 'sys_user_sex', '1', 'N', '', '性别女', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (3, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '未知', 3, 'sys_user_sex', '2', 'N', '', '性别未知', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (4, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '显示', 1, 'sys_show_hide', '0', 'Y', 'primary', '显示菜单', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (5, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '隐藏', 2, 'sys_show_hide', '1', 'N', 'danger', '隐藏菜单', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (6, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '正常', 1, 'sys_normal_disable', '0', 'Y', 'primary', '正常状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (7, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '停用', 2, 'sys_normal_disable', '1', 'N', 'danger', '停用状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (12, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '是', 1, 'sys_yes_no', 'Y', 'Y', 'primary', '系统默认是', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (13, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '否', 2, 'sys_yes_no', 'N', 'N', 'danger', '系统默认否', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (14, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '通知', 1, 'sys_notice_type', '1', 'Y', 'warning', '通知', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (15, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '公告', 2, 'sys_notice_type', '2', 'N', 'success', '公告', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (16, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '正常', 1, 'sys_notice_status', '0', 'Y', 'primary', '正常状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (17, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '关闭', NULL, 'sys_notice_status', '1', 'N', 'danger', '关闭状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (18, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '新增', NULL, 'sys_oper_type', '1', 'N', 'info', '新增操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (19, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '修改', NULL, 'sys_oper_type', '2', 'N', 'info', '修改操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (20, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '删除', NULL, 'sys_oper_type', '3', 'N', 'danger', '删除操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (21, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '授权', NULL, 'sys_oper_type', '4', 'N', 'primary', '授权操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (22, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '导出', NULL, 'sys_oper_type', '5', 'N', 'warning', '导出操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (23, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '导入', NULL, 'sys_oper_type', '6', 'N', 'warning', '导入操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (24, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '强退', NULL, 'sys_oper_type', '7', 'N', 'danger', '强退操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (25, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '生成代码', NULL, 'sys_oper_type', '8', 'N', 'warning', '生成操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (26, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '清空数据', NULL, 'sys_oper_type', '9', 'N', 'danger', '清空操作', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (27, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '成功', NULL, 'sys_common_status', '0', 'N', 'primary', '正常状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (28, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '失败', NULL, 'sys_common_status', '1', 'N', 'danger', '停用状态', '0', '000000');
INSERT INTO `sys_dict_data` VALUES (29, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '其他', NULL, 'sys_oper_type', '0', 'N', 'info', '其他操作', '0', '000000');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint(0) NOT NULL COMMENT '字典主键',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `dict_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '用户性别', 'sys_user_sex', '用户性别列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (2, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '菜单状态', 'sys_show_hide', '菜单状态列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (3, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '系统开关', 'sys_normal_disable', '系统开关列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (6, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '系统是否', 'sys_yes_no', '系统是否列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (7, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '通知类型', 'sys_notice_type', '通知类型列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (8, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '通知状态', 'sys_notice_status', '通知状态列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (9, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '操作类型', 'sys_oper_type', '操作类型列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (10, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '系统状态', 'sys_common_status', '登录状态列表', '0', '000000');
INSERT INTO `sys_dict_type` VALUES (452767972294725, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '用户性别', 'sys_user_sex', '用户性别列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972298821, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '菜单状态', 'sys_show_hide', '菜单状态列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972298822, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '系统开关', 'sys_normal_disable', '系统开关列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972298823, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '系统是否', 'sys_yes_no', '系统是否列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972298824, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '通知类型', 'sys_notice_type', '通知类型列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972298825, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '通知状态', 'sys_notice_status', '通知状态列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972302917, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '操作类型', 'sys_oper_type', '操作类型列表', '0', '452748015218757');
INSERT INTO `sys_dict_type` VALUES (452767972302918, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '登录状态', 'sys_common_status', '登录状态列表', '0', '452748015218757');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `id` bigint(0) NOT NULL COMMENT 'ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器类型',
  `ipaddr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地点',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提示消息',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录状态 0成功 1失败',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (516579924885573, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'Chrome', '127.0.0.1', '内网IP', '2024-02-18 11:09:08', 'user.login.success', 'OSX', '0', '000000', 'admin');
INSERT INTO `sys_logininfor` VALUES (529070732259397, 1, NULL, '2024-03-24 18:14:21', 1, '2024-03-24 18:14:21', 'Chrome', '127.0.0.1', '内网IP', '2024-03-24 18:14:21', 'user.login.success', 'Windows 10 or Windows Server 2016', '0', '000000', 'admin');
INSERT INTO `sys_logininfor` VALUES (529208541483077, 1, NULL, '2024-03-25 03:35:06', 1, '2024-03-25 03:35:06', 'Chrome', '127.0.0.1', '内网IP', '2024-03-25 03:35:06', 'user.login.success', 'Windows 10 or Windows Server 2016', '0', '000000', 'admin');
INSERT INTO `sys_logininfor` VALUES (529648296656965, 1, NULL, '2024-03-26 09:24:28', 1, '2024-03-26 09:24:28', 'Chrome', '127.0.0.1', '内网IP', '2024-03-26 09:24:28', 'user.login.success', 'Linux', '0', '000000', 'admin');
INSERT INTO `sys_logininfor` VALUES (1665628533390614529, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'Chrome', '127.0.0.1', '内网IP', NULL, '登录成功', 'Windows 10 or Windows Server 2016', '0', '000000', 'admin');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(0) NOT NULL COMMENT '菜单ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `is_cache` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否缓存（0缓存 1不缓存）',
  `is_frame` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为外链（0是 1否）',
  `menu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `menu_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型（M目录 C菜单 F按钮）',
  `order_num` int(0) NULL DEFAULT NULL COMMENT '显示顺序',
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父菜单ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址',
  `perms` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限字符串',
  `query_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单状态（0正常 1停用）',
  `visible` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示状态（0显示 1隐藏）',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'system', '0', '1', '系统管理', 'M', 1, 0, 'system', '', '', '系统管理目录', '0', '0');
INSERT INTO `sys_menu` VALUES (2, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'monitor', '0', '1', '系统监控', 'M', 23, 0, 'monitor', '', '', '系统监控目录', '0', '0');
INSERT INTO `sys_menu` VALUES (4, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'guide', '0', '0', '铱塔官网', 'M', 25, 0, 'http://iotkit-open-source.gitee.io/document', '', '', '铱塔官网', '0', '0');
INSERT INTO `sys_menu` VALUES (6, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'chart', '0', '1', '租户管理', 'M', 22, 0, 'tenant', '', '', '租户管理目录', '0', '0');
INSERT INTO `sys_menu` VALUES (100, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/user/index', 'user', '0', '1', '用户管理', 'C', 1, 1, 'user', 'system:user:list', '', '用户管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (101, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/role/index', 'peoples', '0', '1', '角色管理', 'C', 2, 1, 'role', 'system:role:list', '', '角色管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (102, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/menu/index', 'tree-table', '0', '1', '菜单管理', 'C', 3, 1, 'menu', 'system:menu:list', '', '菜单管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (103, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/dept/index', 'tree', '0', '1', '部门管理', 'C', 4, 1, 'dept', 'system:dept:list', '', '部门管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (104, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/post/index', 'post', '0', '1', '岗位管理', 'C', 5, 1, 'post', 'system:post:list', '', '岗位管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (105, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/dict/index', 'dict', '0', '1', '字典管理', 'C', 6, 1, 'dict', 'system:dict:list', '', '字典管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (106, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/config/index', 'edit', '0', '1', '参数设置', 'C', 7, 1, 'sysconfig', 'system:config:list', '', '参数设置菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (107, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/notice/index', 'message', '0', '1', '通知公告', 'C', 8, 1, 'notice', 'system:notice:list', '', '通知公告菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (108, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', 'log', '0', '1', '日志管理', 'M', 9, 1, 'log', '', '', '日志管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (109, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'monitor/online/index', 'online', '0', '1', '在线用户', 'C', 1, 2, 'online', 'monitor:online:list', '', '在线用户菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (118, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/oss/index', 'upload', '0', '1', '文件管理', 'C', 10, 1, 'oss', 'system:oss:list', '', '文件管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (121, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/tenant/index', 'list', '0', '1', '租户管理', 'C', 1, 6, 'tenant', 'system:tenant:list', '', '租户管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (122, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/tenantPackage/index', 'form', '0', '1', '租户套餐管理', 'C', 2, 6, 'tenantPackage', 'system:tenantPackage:list', '', '租户套餐管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (423, 1, NULL, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, '', '0', '1', '应用修改', 'F', 4, 502, '', 'system:app:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (500, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'monitor/operlog/index', 'form', '0', '1', '操作日志', 'C', 1, 108, 'operlog', 'monitor:operlog:list', '', '操作日志菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (501, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'monitor/logininfor/index', 'logininfor', '0', '1', '登录日志', 'C', 2, 108, 'logininfor', 'monitor:logininfor:list', '', '登录日志菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (502, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'system/app/index', 'phone', '0', '1', '应用管理', 'C', 11, 1, 'app', 'system:app:list', '', '应用管理菜单', '0', '0');
INSERT INTO `sys_menu` VALUES (1001, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户查询', 'F', 1, 100, '', 'system:user:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1002, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户新增', 'F', 2, 100, '', 'system:user:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1003, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户修改', 'F', 3, 100, '', 'system:user:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1004, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户删除', 'F', 4, 100, '', 'system:user:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1005, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户导出', 'F', 5, 100, '', 'system:user:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1006, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '用户导入', 'F', 6, 100, '', 'system:user:import', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1007, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '重置密码', 'F', 7, 100, '', 'system:user:resetPwd', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1008, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '角色查询', 'F', 1, 101, '', 'system:role:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1009, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '角色新增', 'F', 2, 101, '', 'system:role:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1010, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '角色修改', 'F', 3, 101, '', 'system:role:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1011, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '角色删除', 'F', 4, 101, '', 'system:role:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1012, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '角色导出', 'F', 5, 101, '', 'system:role:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1013, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '菜单查询', 'F', 1, 102, '', 'system:menu:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1014, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '菜单新增', 'F', 2, 102, '', 'system:menu:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1015, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '菜单修改', 'F', 3, 102, '', 'system:menu:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1016, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '菜单删除', 'F', 4, 102, '', 'system:menu:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1017, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '部门查询', 'F', 1, 103, '', 'system:dept:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1018, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '部门新增', 'F', 2, 103, '', 'system:dept:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1019, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '部门修改', 'F', 3, 103, '', 'system:dept:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1020, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '部门删除', 'F', 4, 103, '', 'system:dept:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1021, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '岗位查询', 'F', 1, 104, '', 'system:post:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1022, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '岗位新增', 'F', 2, 104, '', 'system:post:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1023, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '岗位修改', 'F', 3, 104, '', 'system:post:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1024, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '岗位删除', 'F', 4, 104, '', 'system:post:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1025, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '岗位导出', 'F', 5, 104, '', 'system:post:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1026, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '字典查询', 'F', 1, 105, '#', 'system:dict:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1027, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '字典新增', 'F', 2, 105, '#', 'system:dict:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1028, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '字典修改', 'F', 3, 105, '#', 'system:dict:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1029, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '字典删除', 'F', 4, 105, '#', 'system:dict:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1030, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '字典导出', 'F', 5, 105, '#', 'system:dict:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1031, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '参数查询', 'F', 1, 106, '#', 'system:config:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1032, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '参数新增', 'F', 2, 106, '#', 'system:config:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1033, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '参数修改', 'F', 3, 106, '#', 'system:config:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1034, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '参数删除', 'F', 4, 106, '#', 'system:config:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1035, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '参数导出', 'F', 5, 106, '#', 'system:config:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1036, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '公告查询', 'F', 1, 107, '#', 'system:notice:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1037, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '公告新增', 'F', 2, 107, '#', 'system:notice:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1038, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '公告修改', 'F', 3, 107, '#', 'system:notice:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1039, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '公告删除', 'F', 4, 107, '#', 'system:notice:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1040, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '操作查询', 'F', 1, 500, '#', 'monitor:operlog:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1041, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '操作删除', 'F', 2, 500, '#', 'monitor:operlog:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1042, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '日志导出', 'F', 4, 500, '#', 'monitor:operlog:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1043, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '登录查询', 'F', 1, 501, '#', 'monitor:logininfor:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1044, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '登录删除', 'F', 2, 501, '#', 'monitor:logininfor:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1045, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '日志导出', 'F', 3, 501, '#', 'monitor:logininfor:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1046, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '在线查询', 'F', 1, 109, '#', 'monitor:online:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1047, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '批量强退', 'F', 2, 109, '#', 'monitor:online:batchLogout', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1048, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '单条强退', 'F', 3, 109, '#', 'monitor:online:forceLogout', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1050, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '账户解锁', 'F', 4, 501, '#', 'monitor:logininfor:unlock', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1600, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '文件查询', 'F', 1, 118, '#', 'system:oss:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1601, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '文件上传', 'F', 2, 118, '#', 'system:oss:upload', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1602, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '文件下载', 'F', 3, 118, '#', 'system:oss:download', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1603, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '文件删除', 'F', 4, 118, '#', 'system:oss:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1604, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '配置添加', 'F', 5, 118, '#', 'system:oss:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1605, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '配置编辑', 'F', 6, 118, '#', 'system:oss:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1606, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户查询', 'F', 1, 121, '#', 'system:tenant:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1607, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户新增', 'F', 2, 121, '#', 'system:tenant:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1608, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户修改', 'F', 3, 121, '#', 'system:tenant:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1609, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户删除', 'F', 4, 121, '#', 'system:tenant:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1610, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户导出', 'F', 5, 121, '#', 'system:tenant:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1611, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户套餐查询', 'F', 1, 122, '#', 'system:tenantPackage:query', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1612, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户套餐新增', 'F', 2, 122, '#', 'system:tenantPackage:add', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1613, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户套餐修改', 'F', 3, 122, '#', 'system:tenantPackage:edit', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1614, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户套餐删除', 'F', 4, 122, '#', 'system:tenantPackage:remove', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (1615, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', '', '#', '0', '1', '租户套餐导出', 'F', 5, 122, '#', 'system:tenantPackage:export', '', '', '0', '0');
INSERT INTO `sys_menu` VALUES (2200, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'device', '0', '1', '设备接入', 'M', 1, 0, '/equipment', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2201, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/categories/index', 'education', '1', '1', '品类管理', 'C', 1, 2200, 'categories', 'iot:category:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2205, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/products/index', 'product-management', '1', '1', '产品管理', 'C', 2, 2200, 'products', 'iot:product:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2206, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/devices/list', 'device', '1', '1', '设备列表', 'C', 3, 2200, 'devices', 'iot:device:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2207, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/devices/virtualDevices', 'monitor', '1', '1', '虚拟设备', 'C', 4, 2200, 'virtualDevices', 'iot:virtualDevice:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2210, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/plugins/index', 'component', '1', '1', '插件管理', 'C', 4, 2200, 'plugins', 'iot:plugin:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2211, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'dashboard', '0', '1', '规则引擎', 'M', 3, 0, 'ruleEngine', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2212, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/ruleEngine/ruleSys/index', 'component', '0', '1', '规则管理', 'C', 1, 2211, 'ruleSys', 'iot:rule:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2213, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/ruleEngine/scheduledTask/index', 'component', '0', '1', '定时任务', 'C', 1, 2211, 'scheduledTask', 'iot:task:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2214, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/devices/detail', '', '0', '1', '设备详情', 'C', 1, 2200, 'devicesDetail/:id', NULL, NULL, '', '0', '1');
INSERT INTO `sys_menu` VALUES (2215, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/equipment/devices/virtualDeviceConfig', '', '1', '1', '虚拟设备详情', 'C', 16, 2200, 'virtualDeviceConfig/:id', NULL, NULL, '', '0', '1');
INSERT INTO `sys_menu` VALUES (2216, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', NULL, 'message', '0', '1', '消息中心', 'M', 4, 0, 'messageCenter', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2217, 1, 103, '2024-03-24 18:07:04', 1, '2024-03-24 18:07:04', 'iot/messageCenter/list', 'message', '0', '1', '消息列表', 'C', 1, 2216, 'messageCenterList', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2218, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, 'cascader', '0', '1', '通道管理', 'M', 5, 0, 'channel', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2219, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/channel/config', 'system', '0', '1', '通道配置', 'C', 1, 2218, 'channelConfig', 'iot:channel:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2220, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/channel/template', 'system', '0', '1', '模板配置', 'C', 2, 2218, 'template', 'iot:channel:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2221, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, 'alart', '0', '1', '告警中心', 'M', 6, 0, 'alarm', NULL, NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2222, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/alarm/list', 'message', '0', '1', '告警列表', 'C', 1, 2221, 'list', 'iot:alert:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2223, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/alarm/config', 'system', '0', '1', '告警配置', 'C', 2, 2221, 'config', 'iot:alertConfig:list', NULL, '', '0', '0');
INSERT INTO `sys_menu` VALUES (2225, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/ota/upgradePack/index', 'upload', '0', '1', 'OTA', 'C', 1, 0, 'upgradePack', 'iot:ota:list', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442129, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '应用查询', 'F', 1, 502, '', 'system:app:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (44212332, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '应用添加', 'F', 2, 502, '', 'system:app:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (4234553537, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '应用导出', 'F', 5, 502, '', 'system:app:export', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442123323123, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '应用删除', 'F', 3, 502, '', 'system:app:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441851175424069, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '品类查询', 'F', 1, 2201, '', 'iot:category:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441851310268485, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '品类修改', 'F', 1, 2201, '', 'iot:category:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441851485323333, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '品类删除', 'F', 1, 2201, '', 'iot:category:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441853056651333, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '产品查询', 'F', 1, 2205, '', 'iot:product:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441853220675653, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '产品添加', 'F', 1, 2205, '', 'iot:product:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441853285179461, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '产品修改', 'F', 1, 2205, '', 'iot:product:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441862726639685, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '产品删除', 'F', 1, 2205, '', 'iot:product:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441862877970501, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '物模型查询', 'F', 1, 2205, '', 'iot:thingModel:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441862978281541, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '物模型修改', 'F', 1, 2205, '', 'iot:thingModel:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441863058096197, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '物模型删除', 'F', 1, 2205, '', 'iot:thingModel:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869217525829, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备查询', 'F', 1, 2206, '', 'iot:device:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869327724613, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备添加', 'F', 1, 2206, '', 'iot:device:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869398310981, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备修改', 'F', 1, 2206, '', 'iot:device:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869530075205, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备删除', 'F', 1, 2206, '', 'iot:device:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869649666117, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备控制', 'F', 1, 2206, '', 'iot:device:ctrl', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441869802405957, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备日志查询', 'F', 1, 2206, '', 'iot:deviceLog:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870007808069, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备组查询', 'F', 1, 2206, '', 'iot:deviceGroup:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870091001925, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备组添加', 'F', 1, 2206, '', 'iot:deviceGroup:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870227968069, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备组修改', 'F', 1, 2206, '', 'iot:deviceGroup:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870309814341, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '设备组删除', 'F', 1, 2206, '', 'iot:deviceGroup:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870607315013, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '虚拟设备查询', 'F', 1, 2207, '', 'iot:virtualDevice:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870717771845, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '虚拟设备添加', 'F', 1, 2207, '', 'iot:virtualDevice:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870780928069, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '虚拟设备修改', 'F', 1, 2207, '', 'iot:virtualDevice:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (441870881378373, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '虚拟设备删除', 'F', 1, 2207, '', 'iot:virtualDevice:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442127357415493, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '插件添加', 'F', 1, 2210, '', 'iot:plugin:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442127532781637, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '插件修改', 'F', 2, 2210, '', 'iot:plugin:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442127596064837, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '插件查询', 'F', 4, 2210, '', 'iot:plugin:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442127705182277, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '插件删除', 'F', 3, 2210, '', 'iot:plugin:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442128593006661, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '规则查询', 'F', 1, 2212, '', 'iot:rule:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442128733950021, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '规则修改', 'F', 1, 2212, '', 'iot:rule:eidt', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442128795189317, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '规则删除', 'F', 1, 2212, '', 'iot:rule:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442129175347269, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '定时任务查询', 'F', 1, 2213, '', 'iot:task:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442129320091717, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '定时任务修改', 'F', 1, 2213, '', 'iot:task:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442129388187717, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '定时任务删除', 'F', 1, 2213, '', 'iot:task:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442130419388485, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '通道添加', 'F', 1, 2219, '', 'iot:channel:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442130475098181, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '通道修改', 'F', 1, 2219, '', 'iot:channel:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442130534805573, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '通道删除', 'F', 1, 2219, '', 'iot:channel:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442130860195909, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '告警配置查询', 'F', 1, 2223, '', 'iot:alertConfig:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442130932109381, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '告警配置添加', 'F', 1, 2223, '', 'iot:alertConfig:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442131029889093, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '告警配置修改', 'F', 1, 2223, '', 'iot:alertConfig:edit', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442131115253829, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '告警配置删除', 'F', 1, 2223, '', 'iot:alertConfig:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442131294584901, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '告警查询', 'F', 1, 2222, '', 'iot:alert:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442143693541445, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '通道查询', 'F', 1, 2219, '', 'iot:channel:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442149490409541, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', 'OTA查询', 'F', 1, 2225, '', 'iot:ota:query', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442149580529733, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', 'OTA添加', 'F', 1, 2225, '', 'iot:ota:add', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442149650423877, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', 'OTA删除', 'F', 1, 2225, '', 'iot:ota:remove', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (442149811572805, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '', '0', '1', '执行升级', 'F', 1, 2225, '', 'iot:ota:upgrade', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (461626374598725, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/plugins/detail', '', '0', '1', '插件详情', 'C', 3, 2200, 'detail/:id', NULL, NULL, NULL, '0', '1');
INSERT INTO `sys_menu` VALUES (521086150434885, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/equipment/devices/deviceGroup', 'device-grouping', '0', '1', '设备分组', 'C', 2, 2200, 'deviceGroup', 'iot:deviceGroup:list', NULL, NULL, '0', '0');
INSERT INTO `sys_menu` VALUES (521131319332933, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'iot/equipment/devices/deviceGroupDetail', '', '1', '1', '设备分组详情', 'C', 17, 2200, 'deviceGroupDetail/:id', 'iot:device:query', NULL, NULL, '0', '1');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `id` bigint(0) NOT NULL COMMENT '公告ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告内容',
  `notice_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告标题',
  `notice_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告类型（1通知 2公告）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公告状态（0正常 1关闭）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', '5paw54mI5pys5YaF5a65', '温馨提醒：2018-07-01 新版本发布啦', '2', '管理员', '0', '000000');
INSERT INTO `sys_notice` VALUES (2, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', '57u05oqk5YaF5a65', '维护通知：2018-07-01 系统凌晨维护', '1', '管理员', '0', '000000');

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
  `id` bigint(0) NOT NULL COMMENT '对象存储主键',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件后缀名',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '原名',
  `service` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务商',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'URL地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`  (
  `id` bigint(0) NOT NULL COMMENT '主建',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `access_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'accessKey',
  `access_policy` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶权限类型(0private 1public 2custom)',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '桶名称',
  `config_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置key',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义域名',
  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问站点',
  `ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扩展字段',
  `is_https` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否https（0否 1是）',
  `prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前缀',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `secret_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '秘钥',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否默认（0=是,1=否）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss_config
-- ----------------------------
INSERT INTO `sys_oss_config` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'admin', '1', 'iot', 'oss-embed', '', 'localhost:8086/iot-oss', '', NULL, '', 'local', NULL, '123', '0', '000000');
INSERT INTO `sys_oss_config` VALUES (2, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'XXXXXXXXXXXXXXX', '1', 'ruoyi', 'qiniu', '', 's3-cn-north-1.qiniucs.com', '', NULL, '', '', NULL, 'XXXXXXXXXXXXXXX', '1', '000000');
INSERT INTO `sys_oss_config` VALUES (3, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'XXXXXXXXXXXXXXX', '1', 'ruoyi', 'aliyun', '', 'oss-cn-beijing.aliyuncs.com', '', NULL, '', '', NULL, 'XXXXXXXXXXXXXXX', '1', '000000');
INSERT INTO `sys_oss_config` VALUES (4, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'XXXXXXXXXXXXXXX', '1', 'ruoyi-1250000000', 'qcloud', '', 'cos.ap-beijing.myqcloud.com', '', NULL, '', 'ap-beijing', NULL, 'XXXXXXXXXXXXXXX', '1', '000000');

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `id` bigint(0) NOT NULL COMMENT '岗位序号',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `post_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位编码',
  `post_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `post_sort` int(0) NULL DEFAULT NULL COMMENT '岗位排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'ceo', '董事长', 1, '', '0', '000000');
INSERT INTO `sys_post` VALUES (2, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'se', '项目经理', 2, '', '0', '000000');
INSERT INTO `sys_post` VALUES (3, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'hr', '人力资源', 3, '', '0', '000000');
INSERT INTO `sys_post` VALUES (4, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', 'user', '普通员工', 4, '', '0', '000000');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(0) NOT NULL COMMENT '角色ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `data_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）',
  `del_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `dept_check_strictly` bit(1) NULL DEFAULT NULL COMMENT '部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）',
  `menu_check_strictly` bit(1) NULL DEFAULT NULL COMMENT '菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `role_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色权限',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_sort` int(0) NULL DEFAULT NULL COMMENT '角色排序',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '0', NULL, NULL, '超级管理员', 'superadmin', '超级管理员', 1, '0', '000000');
INSERT INTO `sys_role` VALUES (2, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', NULL, '0', NULL, NULL, '普通角色', 'common', '普通角色', 2, '0', '000000');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门ID',
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (516579003666501, 100, 2);
INSERT INTO `sys_role_dept` VALUES (516579003695173, 101, 2);
INSERT INTO `sys_role_dept` VALUES (516579003699269, 105, 2);
INSERT INTO `sys_role_dept` VALUES (516579003707461, 452767971254341, 452767970971717);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `menu_id` bigint(0) NULL DEFAULT NULL COMMENT '菜单ID',
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (447227041742917, 1, 2);
INSERT INTO `sys_role_menu` VALUES (447227041742918, 100, 2);
INSERT INTO `sys_role_menu` VALUES (447227041742919, 1001, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747013, 1002, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747014, 1003, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747015, 1005, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747016, 101, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747017, 1008, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747018, 1009, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747019, 1010, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747020, 1012, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747021, 102, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747022, 1013, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747023, 1014, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747024, 1015, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747025, 103, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747026, 1017, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747027, 1018, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747028, 1019, 2);
INSERT INTO `sys_role_menu` VALUES (447227041747029, 104, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751109, 1021, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751110, 1022, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751111, 1023, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751112, 1025, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751113, 105, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751114, 1026, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751115, 1027, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751116, 1028, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751117, 1030, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751118, 106, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751119, 1031, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751120, 1032, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751121, 1033, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751122, 1035, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751123, 107, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751124, 1036, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751125, 1037, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751126, 1038, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751127, 1039, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751128, 108, 2);
INSERT INTO `sys_role_menu` VALUES (447227041751129, 500, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755205, 1040, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755206, 1041, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755207, 1042, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755208, 501, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755209, 1043, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755210, 1044, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755211, 1045, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755212, 118, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755213, 1600, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755214, 1601, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755215, 1602, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755216, 1603, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755217, 1604, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755218, 1605, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755219, 2200, 2);
INSERT INTO `sys_role_menu` VALUES (447227041755220, 2201, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759301, 441851175424069, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759302, 441851310268485, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759303, 2214, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759304, 2205, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759305, 441853056651333, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759306, 441853220675653, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759307, 441853285179461, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759308, 441862877970501, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759309, 441862978281541, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759310, 2206, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759311, 441869217525829, 2);
INSERT INTO `sys_role_menu` VALUES (447227041759312, 441869327724613, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763397, 441869398310981, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763398, 441869649666117, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763399, 441869802405957, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763400, 441870007808069, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763401, 441870091001925, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763402, 441870227968069, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763403, 2207, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763404, 441870607315013, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763405, 441870717771845, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763406, 441870780928069, 2);
INSERT INTO `sys_role_menu` VALUES (447227041763407, 2215, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767493, 2225, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767494, 442149490409541, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767495, 442149580529733, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767496, 442149811572805, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767497, 2208, 2);
INSERT INTO `sys_role_menu` VALUES (447227041767498, 2209, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771591, 2210, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771592, 442127357415493, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771593, 442127532781637, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771594, 442127596064837, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771595, 2211, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771596, 2212, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771597, 442128593006661, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771598, 442128733950021, 2);
INSERT INTO `sys_role_menu` VALUES (447227041771599, 2213, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775685, 442129175347269, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775686, 442129320091717, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775687, 2216, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775688, 2217, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775689, 2218, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775690, 2219, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775691, 442130419388485, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775692, 442130475098181, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775693, 442143693541445, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775694, 2220, 2);
INSERT INTO `sys_role_menu` VALUES (447227041775695, 2221, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779781, 2222, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779782, 442131294584901, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779783, 2223, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779784, 442130860195909, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779785, 442130932109381, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779786, 442131029889093, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779787, 6, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779788, 121, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779789, 1606, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779790, 1607, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779791, 1608, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779792, 1610, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779793, 1611, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779794, 1612, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779795, 1613, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779796, 1615, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779797, 2, 2);
INSERT INTO `sys_role_menu` VALUES (447227041779798, 109, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783877, 1046, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783879, 115, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783880, 1055, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783881, 1056, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783882, 1058, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783883, 1057, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783884, 1059, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783885, 1060, 2);
INSERT INTO `sys_role_menu` VALUES (447227041783886, 4, 2);

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `account_count` bigint(0) NULL DEFAULT NULL COMMENT '用户数量（-1不限制）',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `contact_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `del_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业简介',
  `license_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统一社会信用代码',
  `package_id` bigint(0) NULL DEFAULT NULL COMMENT '租户套餐编号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', -1, NULL, 'XXX有限公司', '15888888888', '管理组', '0', NULL, NULL, '多租户通用后台管理管理系统', NULL, NULL, NULL, '0', '000000');
INSERT INTO `sys_tenant` VALUES (452748015235141, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', -1, '13123123', '测试租户有限公司', '18888888888', '测试人员', '0', NULL, NULL, '测试租户有限公司管理系统', '12312312312', 450389924483141, '租户管理员账密:test/test123', '0', '452748015218757');

-- ----------------------------
-- Table structure for sys_tenant_package
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant_package`;
CREATE TABLE `sys_tenant_package`  (
  `id` bigint(0) NOT NULL COMMENT '租户套餐id',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `menu_check_strictly` bit(1) NULL DEFAULT NULL COMMENT '菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）',
  `menu_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '关联菜单id',
  `package_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '套餐名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态（0正常 1停用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant_package
-- ----------------------------
INSERT INTO `sys_tenant_package` VALUES (450389924483141, 1, NULL, '2024-03-24 18:07:05', 1, '2024-03-24 18:07:05', '0', b'1', '1,100,1001,1002,1003,1004,1005,1006,1007,101,1008,1009,1010,1011,1012,102,1013,1014,1015,1016,103,1017,1018,1019,1020,104,1021,1022,1023,1024,1025,105,1026,1027,1028,1029,1030,106,1031,1032,1033,1034,1035,107,1036,1037,1038,1039,108,500,1040,1041,1042,501,1043,1044,1045,1050,118,1600,1601,1602,1603,1604,1605,502,442129,44212332,442123323123,423,4234553537,2200,2201,441851175424069,441851310268485,441851485323333,2214,2205,441853056651333,441853220675653,441853285179461,441862726639685,441862877970501,441862978281541,441863058096197,2206,441869217525829,441869327724613,441869398310981,441869530075205,441869649666117,441869802405957,441870007808069,441870091001925,441870227968069,441870309814341,2207,441870607315013,441870717771845,441870780928069,441870881378373,2215,2225,442149490409541,442149580529733,442149650423877,442149811572805,2208,2209,2210,442127357415493,442127532781637,442127596064837,442127705182277,2211,2212,442128593006661,442128733950021,442128795189317,2213,442129175347269,442129320091717,442129388187717,2216,2217,2218,2219,442130419388485,442130475098181,442130534805573,442143693541445,2220,2221,2222,442131294584901,2223,442130860195909,442130932109381,442131029889093,442131115253829', '测试套餐', '测试套餐', '0');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL COMMENT '用户ID',
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `avatar` bigint(0) NULL DEFAULT NULL COMMENT '用户头像',
  `del_flag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除标志（0代表存在 2代表删除）',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门ID',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `phonenumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户性别',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帐号状态（0正常 1停用）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户类型（sys_user系统用户）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 1, 103, '2024-03-24 18:07:05', 1, '2024-03-26 09:24:28', NULL, '0', 103, 'xw2sy@163.com', '2024-03-26 09:24:28', '127.0.0.1', 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '15888888888', '管理员', '1', '0', '000000', 'admin', 'sys_user');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `id` bigint(0) NOT NULL,
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `post_id` bigint(0) NULL DEFAULT NULL COMMENT '岗位ID',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (516579006476357, 1, NULL, '2024-03-24 18:07:06', 1, '2024-03-24 18:07:06', 1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(0) NOT NULL,
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (516579006554181, 1, NULL, '2024-03-24 18:07:06', 1, '2024-03-24 18:07:06', 1, 1);

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `actions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务输出',
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表达式',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作备注',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务状态',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务类型',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_info
-- ----------------------------
INSERT INTO `task_info` VALUES ('667bbfa1-a7ed-4ce8-9ce0-cfa8cac90e6c', NULL, 1645871966035, '2222', '22', '2222', 'stop by 6286886077b91b031115e6a6', 'stopped', 'delay', NULL);
INSERT INTO `task_info` VALUES ('97f79dde-bf3c-4d5b-bfd8-8102539002ad', NULL, 1645928016031, 'sss入网', '*/25 * * * * ? *', '测试111', 'stop by 1', 'stopped', 'timer', '1');

-- ----------------------------
-- Table structure for thing_model
-- ----------------------------
DROP TABLE IF EXISTS `thing_model`;
CREATE TABLE `thing_model`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `model` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模型内容',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品key',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of thing_model
-- ----------------------------
INSERT INTO `thing_model` VALUES (1, '{\"properties\":[{\"identifier\":\"powerstate\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"brightness\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"100\"}},\"name\":\"亮度\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'xpsYHExTKPFaQMS7');
INSERT INTO `thing_model` VALUES (2, '{\"properties\":[{\"identifier\":\"windSpeed\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"10\"}},\"name\":\"风速\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"powerSwitch\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"电源开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"workMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"1\":\"正常风\",\"2\":\"自然风\",\"3\":\"睡眠风\",\"4\":\"静音风\"}},\"name\":\"工作模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'hdX3PCMcFrCYpesJ');
INSERT INTO `thing_model` VALUES (3, '{\"properties\":[],\"services\":[{\"identifier\":\"allowJoin\",\"inputData\":[],\"outputData\":[],\"name\":\"开启入网\"},{\"identifier\":\"rawSend\",\"inputData\":[{\"identifier\":\"data\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"数据\",\"required\":false},{\"identifier\":\"deviceName\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备唯一码\",\"required\":false},{\"identifier\":\"model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"required\":false}],\"outputData\":[],\"name\":\"透传下发\"}],\"events\":[{\"identifier\":\"rawReport\",\"outputData\":[{\"identifier\":\"data\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"数据\",\"required\":false},{\"identifier\":\"deviceName\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备唯一码\",\"required\":false},{\"identifier\":\"model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"required\":false}],\"name\":\"透传上报\"}]}', 'hbtgIA0SuVw9lxjB');
INSERT INTO `thing_model` VALUES (4, '{\"properties\":[{\"identifier\":\"powerstate\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"全开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_1\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关1\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_2\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关2\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_3\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关3\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"DeviceType\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"rssi\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-127\",\"max\":\"127\"}},\"name\":\"信号强度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[{\"identifier\":\"faultReportEvent\",\"outputData\":[{\"identifier\":\"code\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"错误代码\",\"required\":false}],\"name\":\"故障上报\"}]}', 'eDhXKwEzwFybM5R7');
INSERT INTO `thing_model` VALUES (5, '{\"properties\":[{\"identifier\":\"rssi\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-127\",\"max\":\"127\"}},\"name\":\"信号强度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"DeviceType\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[{\"identifier\":\"Toggle\",\"inputData\":[],\"outputData\":[],\"name\":\"开关切换\"}],\"events\":[{\"identifier\":\"faultReportEvent\",\"outputData\":[{\"identifier\":\"code\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"255\"}},\"name\":\"错误代码\",\"required\":false}],\"name\":\"故障上报\"}]}', 'cGCrkK7Ex4FESAwe');
INSERT INTO `thing_model` VALUES (6, '{\"properties\":[{\"identifier\":\"powerstate\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关状态\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"volt\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电压\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[{\"identifier\":\"service1\",\"inputData\":[{\"identifier\":\"p1\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"100\"}},\"name\":\"参数1\",\"required\":false},{\"identifier\":\"p2\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"90\"}},\"name\":\"参数2\",\"required\":false}],\"outputData\":[],\"name\":\"服务1\"}],\"events\":[{\"identifier\":\"event1\",\"outputData\":[{\"identifier\":\"p1\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"参数p1\",\"required\":false},{\"identifier\":\"p2\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"100\"}},\"name\":\"参数p2\",\"required\":false}],\"name\":\"测试event1\"}]}', 'Rf4QSjbm65X45753');
INSERT INTO `thing_model` VALUES (7, '{\"properties\":[{\"identifier\":\"power\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"电量\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"DeviceType\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"rssi\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-127\",\"max\":\"127\"}},\"name\":\"信号强度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"doorStatus\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"门状态\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[{\"identifier\":\"prylockEvent\",\"outputData\":[],\"name\":\"防撬报警事件\"}]}', 'PN3EDmkBZDD8whDd');
INSERT INTO `thing_model` VALUES (8, '{\"properties\":[{\"identifier\":\"DeviceType\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"UnbindAndDelete\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"否\",\"1\":\"是\"}},\"name\":\"解绑并删除设备\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[{\"identifier\":\"Reboot\",\"inputData\":[],\"outputData\":[],\"name\":\"重启\"},{\"identifier\":\"AllowJoin\",\"inputData\":[],\"outputData\":[],\"name\":\"开启组网\"},{\"identifier\":\"OpenTrace\",\"inputData\":[{\"identifier\":\"enable\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"Close\",\"1\":\"Open\"}},\"name\":\"值\",\"required\":false}],\"outputData\":[],\"name\":\"打开调试\"},{\"identifier\":\"ShowDesc\",\"inputData\":[{\"identifier\":\"ieee\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"32\"}},\"name\":\"设备地址\",\"required\":false}],\"outputData\":[],\"name\":\"显示设备信息\"},{\"identifier\":\"rawSend\",\"inputData\":[{\"identifier\":\"model\",\"dataType\":{\"type\":\"text\",\"specs\":{}},\"name\":\"设备型号\",\"required\":false},{\"identifier\":\"mac\",\"dataType\":{\"type\":\"text\",\"specs\":{}},\"name\":\"设备mac\",\"required\":false},{\"identifier\":\"data\",\"dataType\":{\"type\":\"text\",\"specs\":{}},\"name\":\"数据\",\"required\":false}],\"outputData\":[],\"name\":\"透传下发\"}],\"events\":[{\"identifier\":\"faultReport\",\"outputData\":[{\"identifier\":\"code\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"255\"}},\"name\":\"错误码\",\"required\":false}],\"name\":\"故障上报\"},{\"identifier\":\"rawReport\",\"outputData\":[{\"identifier\":\"model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"设备型号\",\"required\":false},{\"identifier\":\"mac\",\"dataType\":{\"type\":\"text\",\"specs\":{}},\"name\":\"设备mac\",\"required\":false},{\"identifier\":\"data\",\"dataType\":{\"type\":\"text\",\"specs\":{}},\"name\":\"数据\",\"required\":false}],\"name\":\"透传上报\"}]}', 'N523nWsCiG3CAn6X');
INSERT INTO `thing_model` VALUES (9, '{\"properties\":[],\"services\":[],\"events\":[{\"identifier\":\"userDevicesChange\",\"outputData\":[{\"identifier\":\"uid\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"100\"}},\"name\":\"用户Id\",\"required\":false}],\"name\":\"用户设备列表变更\"}]}', 'KdJYpTp5ywNhmrmC');
INSERT INTO `thing_model` VALUES (10, '{\"properties\":[{\"identifier\":\"flow\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100000\"}},\"name\":\"用量\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"fee\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100000\"}},\"name\":\"费用\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'Eit3kmGJtxSHfCKT');
INSERT INTO `thing_model` VALUES (11, '{\"properties\":[{\"identifier\":\"powerstate_1\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关1\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_2\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关2\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_3\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关3\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"powerstate_4\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关4\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"DeviceType\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"128\"}},\"name\":\"型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"rssi\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-127\",\"max\":\"127\"}},\"name\":\"信号强度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[{\"identifier\":\"faultReportEvent\",\"outputData\":[{\"identifier\":\"code\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"错误代码\",\"required\":false}],\"name\":\"故障上报\"}]}', 'D8c5pXFmt2KJDxNm');
INSERT INTO `thing_model` VALUES (12, '{\"properties\":[{\"identifier\":\"rssi\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-128\",\"max\":\"128\"}},\"name\":\"信号强度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"switch\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"voltage\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1000\"}},\"name\":\"电压\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"current\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1000\"}},\"name\":\"电流\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"power\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1000000\"}},\"name\":\"功率\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"electricty\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1000000000\"}},\"name\":\"电量\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"back_light\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"背光灯\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"start_onoff\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"off\",\"1\":\"onoff with swithc\",\"2\":\"on\"}},\"name\":\"上电启动配置\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"cycle_timer\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"64\"}},\"name\":\"循环定时\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"countdown\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1000000\"}},\"name\":\"倒计时\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'AWcJnf7ymGSkaz5M');
INSERT INTO `thing_model` VALUES (13, '{\"properties\":[{\"identifier\":\"humidity\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"湿度\\t\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"temperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"-38\",\"max\":\"656\"}},\"name\":\"温度\\t\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[{\"identifier\":\"temperatureTooLowEvent\",\"outputData\":[],\"name\":\"温度过低事件\"}]}', '6kYp6jszrDns2yh4');
INSERT INTO `thing_model` VALUES (14, '{\"properties\":[{\"identifier\":\"temp\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"17\",\"max\":\"32\"}},\"name\":\"温度\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"swing_modes\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"扫风开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"modes\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"制热\",\"1\":\"关闭\",\"2\":\"制冷\",\"3\":\"送风\"}},\"name\":\"模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"fan_modes\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"高\",\"1\":\"中\",\"2\":\"低\"}},\"name\":\"风模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"presets_modes\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"节能\",\"1\":\"睡眠\",\"2\":\"活动\"}},\"name\":\"预设模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'bGdZt8ffBETtsirm');
INSERT INTO `thing_model` VALUES (15, '{\"properties\":[],\"services\":[{\"identifier\":\"readData\",\"inputData\":[{\"identifier\":\"deviceAddr\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"12\"}},\"name\":\"设备地址\",\"required\":false},{\"identifier\":\"dataIdentifier\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"4\"}},\"name\":\"数据标识\",\"required\":false}],\"outputData\":[],\"name\":\"读数据\"},{\"identifier\":\"writeData\",\"inputData\":[{\"identifier\":\"deviceAddr\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"12\"}},\"name\":\"设备地址\",\"required\":false},{\"identifier\":\"dataIdentifier\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"4\"}},\"name\":\"数据标识\",\"required\":false}],\"outputData\":[],\"name\":\"写数据\"}],\"events\":[]}', 'BRD3x4fkKxkaxXFt');
INSERT INTO `thing_model` VALUES (16, '{\"properties\":[{\"identifier\":\"p9010\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"20\"}},\"name\":\"(当前)正向有功总电能\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"p9410\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"20\"}},\"name\":\"(上月)正向有功总电能\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'PwMfpXmp4ZWkGahn');
INSERT INTO `thing_model` VALUES (17, '{\"properties\":[{\"identifier\":\"status\",\"dataType\":{\"type\":\"bool\",\"specs\":{\"0\":\"关\",\"1\":\"开\"}},\"name\":\"状态\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"pressure\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"压力\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"model\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动模式\",\"1\":\"手动模式\",\"2\":\"定时模式\",\"3\":\"防锈模式\",\"4\":\"防冻模式\"}},\"name\":\"模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"slave_id\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"255\"}},\"name\":\"连接配置-从机地址\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"baud\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"连接配置-波特率\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"verify\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"1\"}},\"name\":\"连接配置-奇偶校验\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"retain\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"连接配置-保留位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"data_len\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"连接配置-数据位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"alarm\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"故障信息\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"vol\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"电压\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"elect\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"电流\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"power\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"功率\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"spd\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"转速\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"realPre\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"实时压力1\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"runStat\",\"dataType\":{\"type\":\"int32\",\"specs\":{}},\"name\":\"实时状态\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"mcuVersion\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"MCU版本\",\"accessMode\":\"r\",\"description\":null,\"unit\":null}],\"services\":[],\"events\":[]}', 'openiitapump01');
INSERT INTO `thing_model` VALUES (18, '{\"properties\":[{\"identifier\":\"WorkMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\",\"2\":\"自检1\",\"3\":\"防锈\",\"4\":\"防冻\",\"5\":\"自检2\",\"6\":\"温控\"}},\"name\":\"模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"Pressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"设置压力\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"Switch\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"85\":\"关闭\",\"170\":\"开启\"}},\"name\":\"开关\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"EmtyRunPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"缺水压力\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"StartPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"开启压力\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"WaterT\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温保护\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"WaterTReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温复位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"ClearIceEn\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关闭\",\"1\":\"开启\"}},\"name\":\"防冻\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"ErrorMsg\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无\",\"1\":\"过压\",\"2\":\"欠压\",\"3\":\"过流\",\"4\":\"电机高温\",\"5\":\"IPM高温\",\"6\":\"堵转\",\"7\":\"温升保护\",\"8\":\"启动失败\",\"9\":\"缺相\",\"10\":\"无-\",\"11\":\"软件过流\",\"12\":\"缺水\",\"13\":\"未激活\",\"14\":\"传感器故障\",\"15\":\"通信故障\"}},\"name\":\"故障信息\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Voltage\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"300\"}},\"name\":\"电压\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Electric\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"100\",\"precision\":\"\"}},\"name\":\"电流\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Power\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"5000\"}},\"name\":\"功率\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Speed\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"10000\"}},\"name\":\"转速\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"CurrentPressure1\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力1\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"CurrentPressure2\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力2\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"IpmTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"IPM温度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"MotorTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机温度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"WaterTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"水温度\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"McuStatus\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"APP\",\"8\":\"BootLoader\"}},\"name\":\"MCU状态\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Scene\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"增压泵\",\"1\":\"回水器\",\"2\":\"循环泵\"}},\"name\":\"设备场景\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"WarnInfo\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"警告信息\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"ActiveTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"激活时间\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"SensorMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\"}},\"name\":\"传感器组合模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"SensorGroup\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无传感器\",\"1\":\"水流开关-压力\",\"2\":\"单水流开关\",\"3\":\"单压力\",\"4\":\"双压力\"}},\"name\":\"传感器组合\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"ElectronicTMax\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机保护温度\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"ElectronicTMaxReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机复位温度\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"History\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"历史上报\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"Model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"设备型号\",\"accessMode\":\"r\",\"description\":null,\"unit\":null},{\"identifier\":\"HandMode\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"手动模式挡位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"TempGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"温控挡位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"RatioGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"比例挡位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"SpeedGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"速度挡位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"PressureGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"压力挡位\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"SceneMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"节能模式-自动挡\",\"1\":\"温控模式-温控模式\",\"2\":\"一键热水-恒速模式\",\"3\":\"定时模式-恒压模式\",\"4\":\"比例模式\"}},\"name\":\"回水器/循环泵-设置模式\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"EnergyModeTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"节能模式运行时间\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"WaterTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"水流开关设置\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"HotWaterTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"一键热水运行时间\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"TempSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"温控上下限设置\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"CountDown\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"回水器倒计时\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"Enabled\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"停止\",\"1\":\"开启\"}},\"name\":\"实时运行状态\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"TimeModeSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"定时模式时间设置\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null},{\"identifier\":\"McuVersion\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"Mcu版本号\",\"accessMode\":\"rw\",\"description\":null,\"unit\":null}],\"services\":[{\"identifier\":\"set\",\"inputData\":[{\"identifier\":\"WorkMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\",\"2\":\"自检1\",\"3\":\"防锈\",\"4\":\"防冻\",\"5\":\"自检2\",\"6\":\"温控\"}},\"name\":\"模式\",\"required\":false},{\"identifier\":\"Pressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"设置压力\",\"required\":false},{\"identifier\":\"Switch\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"85\":\"关闭\",\"170\":\"开启\"}},\"name\":\"开关\",\"required\":false},{\"identifier\":\"EmtyRunPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"缺水压力\",\"required\":false},{\"identifier\":\"StartPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"开启压力\",\"required\":false},{\"identifier\":\"WaterT\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温保护\",\"required\":false},{\"identifier\":\"WaterTReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温复位\",\"required\":false},{\"identifier\":\"ClearIceEn\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关闭\",\"1\":\"开启\"}},\"name\":\"防冻\",\"required\":false},{\"identifier\":\"ActiveTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"激活时间\",\"required\":false},{\"identifier\":\"SensorMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\"}},\"name\":\"传感器组合模式\",\"required\":false},{\"identifier\":\"SensorGroup\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无传感器\",\"1\":\"水流开关-压力\",\"2\":\"单水流开关\",\"3\":\"单压力\",\"4\":\"双压力\"}},\"name\":\"传感器组合\",\"required\":false},{\"identifier\":\"ElectronicTMax\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机保护温度\",\"required\":false},{\"identifier\":\"ElectronicTMaxReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机复位温度\",\"required\":false},{\"identifier\":\"HandMode\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"手动模式挡位\",\"required\":false},{\"identifier\":\"TempGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"温控挡位\",\"required\":false},{\"identifier\":\"RatioGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"比例挡位\",\"required\":false},{\"identifier\":\"SpeedGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"速度挡位\",\"required\":false},{\"identifier\":\"PressureGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"压力挡位\",\"required\":false},{\"identifier\":\"SceneMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"节能模式-自动挡\",\"1\":\"温控模式-温控模式\",\"2\":\"一键热水-恒速模式\",\"3\":\"定时模式-恒压模式\",\"4\":\"比例模式\"}},\"name\":\"回水器/循环泵-设置模式\",\"required\":false},{\"identifier\":\"EnergyModeTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"节能模式运行时间\",\"required\":false},{\"identifier\":\"WaterTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"水流开关设置\",\"required\":false},{\"identifier\":\"HotWaterTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"一键热水运行时间\",\"required\":false},{\"identifier\":\"TempSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"温控上下限设置\",\"required\":false},{\"identifier\":\"CountDown\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"回水器倒计时\",\"required\":false},{\"identifier\":\"Enabled\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"停止\",\"1\":\"开启\"}},\"name\":\"实时运行状态\",\"required\":false},{\"identifier\":\"TimeModeSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"定时模式时间设置\",\"required\":false},{\"identifier\":\"McuVersion\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"Mcu版本号\",\"required\":false}],\"outputData\":[],\"name\":\"属性设置\"},{\"identifier\":\"get\",\"inputData\":[{\"identifier\":\"propertyName\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"500\"}},\"name\":\"属性名\",\"required\":false}],\"outputData\":[{\"identifier\":\"WorkMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\",\"2\":\"自检1\",\"3\":\"防锈\",\"4\":\"防冻\",\"5\":\"自检2\",\"6\":\"温控\"}},\"name\":\"模式\",\"required\":false},{\"identifier\":\"Pressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"设置压力\",\"required\":false},{\"identifier\":\"Switch\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"85\":\"关闭\",\"170\":\"开启\"}},\"name\":\"开关\",\"required\":false},{\"identifier\":\"EmtyRunPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"缺水压力\",\"required\":false},{\"identifier\":\"StartPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"开启压力\",\"required\":false},{\"identifier\":\"WaterT\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温保护\",\"required\":false},{\"identifier\":\"WaterTReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温复位\",\"required\":false},{\"identifier\":\"ClearIceEn\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关闭\",\"1\":\"开启\"}},\"name\":\"防冻\",\"required\":false},{\"identifier\":\"ErrorMsg\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无\",\"1\":\"过压\",\"2\":\"欠压\",\"3\":\"过流\",\"4\":\"电机高温\",\"5\":\"IPM高温\",\"6\":\"堵转\",\"7\":\"温升保护\",\"8\":\"启动失败\",\"9\":\"缺相\",\"10\":\"无-\",\"11\":\"软件过流\",\"12\":\"缺水\",\"13\":\"未激活\",\"14\":\"传感器故障\",\"15\":\"通信故障\"}},\"name\":\"故障信息\",\"required\":false},{\"identifier\":\"Voltage\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"300\"}},\"name\":\"电压\",\"required\":false},{\"identifier\":\"Electric\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电流\",\"required\":false},{\"identifier\":\"Power\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"5000\"}},\"name\":\"功率\",\"required\":false},{\"identifier\":\"Speed\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"10000\"}},\"name\":\"转速\",\"required\":false},{\"identifier\":\"CurrentPressure1\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力1\",\"required\":false},{\"identifier\":\"CurrentPressure2\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力2\",\"required\":false},{\"identifier\":\"IpmTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"IPM温度\",\"required\":false},{\"identifier\":\"MotorTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机温度\",\"required\":false},{\"identifier\":\"WaterTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"水温度\",\"required\":false},{\"identifier\":\"McuStatus\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"APP\",\"8\":\"BootLoader\"}},\"name\":\"MCU状态\",\"required\":false},{\"identifier\":\"Scene\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"增压泵\",\"1\":\"回水器\",\"2\":\"循环泵\"}},\"name\":\"设备场景\",\"required\":false},{\"identifier\":\"WarnInfo\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"警告信息\",\"required\":false},{\"identifier\":\"ActiveTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"激活时间\",\"required\":false},{\"identifier\":\"SensorMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\"}},\"name\":\"传感器组合模式\",\"required\":false},{\"identifier\":\"SensorGroup\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无传感器\",\"1\":\"水流开关-压力\",\"2\":\"单水流开关\",\"3\":\"单压力\",\"4\":\"双压力\"}},\"name\":\"传感器组合\",\"required\":false},{\"identifier\":\"ElectronicTMax\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机保护温度\",\"required\":false},{\"identifier\":\"ElectronicTMaxReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机复位温度\",\"required\":false},{\"identifier\":\"History\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"历史上报\",\"required\":false},{\"identifier\":\"Model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"设备型号\",\"required\":false},{\"identifier\":\"HandMode\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"手动模式挡位\",\"required\":false},{\"identifier\":\"TempGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"温控挡位\",\"required\":false},{\"identifier\":\"RatioGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"比例挡位\",\"required\":false},{\"identifier\":\"SpeedGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"速度挡位\",\"required\":false},{\"identifier\":\"PressureGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"压力挡位\",\"required\":false},{\"identifier\":\"SceneMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"节能模式-自动挡\",\"1\":\"温控模式-温控模式\",\"2\":\"一键热水-恒速模式\",\"3\":\"定时模式-恒压模式\",\"4\":\"比例模式\"}},\"name\":\"回水器/循环泵-设置模式\",\"required\":false},{\"identifier\":\"EnergyModeTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"节能模式运行时间\",\"required\":false},{\"identifier\":\"WaterTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"水流开关设置\",\"required\":false},{\"identifier\":\"HotWaterTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"一键热水运行时间\",\"required\":false},{\"identifier\":\"TempSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"温控上下限设置\",\"required\":false},{\"identifier\":\"CountDown\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"回水器倒计时\",\"required\":false},{\"identifier\":\"Enabled\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"停止\",\"1\":\"开启\"}},\"name\":\"实时运行状态\",\"required\":false},{\"identifier\":\"TimeModeSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"定时模式时间设置\",\"required\":false},{\"identifier\":\"McuVersion\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"Mcu版本号\",\"required\":false}],\"name\":\"属性获取\"}],\"events\":[{\"identifier\":\"post\",\"outputData\":[{\"identifier\":\"WorkMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\",\"2\":\"自检1\",\"3\":\"防锈\",\"4\":\"防冻\",\"5\":\"自检2\",\"6\":\"温控\"}},\"name\":\"模式\",\"required\":false},{\"identifier\":\"Pressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"设置压力\",\"required\":false},{\"identifier\":\"Switch\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"85\":\"关闭\",\"170\":\"开启\"}},\"name\":\"开关\",\"required\":false},{\"identifier\":\"EmtyRunPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\",\"precision\":\"1\"}},\"name\":\"缺水压力\",\"required\":false},{\"identifier\":\"StartPressure\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"开启压力\",\"required\":false},{\"identifier\":\"WaterT\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温保护\",\"required\":false},{\"identifier\":\"WaterTReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"35\",\"max\":\"100\"}},\"name\":\"水温复位\",\"required\":false},{\"identifier\":\"ClearIceEn\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"关闭\",\"1\":\"开启\"}},\"name\":\"防冻\",\"required\":false},{\"identifier\":\"ErrorMsg\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无\",\"1\":\"过压\",\"2\":\"欠压\",\"3\":\"过流\",\"4\":\"电机高温\",\"5\":\"IPM高温\",\"6\":\"堵转\",\"7\":\"温升保护\",\"8\":\"启动失败\",\"9\":\"缺相\",\"10\":\"无-\",\"11\":\"软件过流\",\"12\":\"缺水\",\"13\":\"未激活\",\"14\":\"传感器故障\",\"15\":\"通信故障\"}},\"name\":\"故障信息\",\"required\":false},{\"identifier\":\"Voltage\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"300\"}},\"name\":\"电压\",\"required\":false},{\"identifier\":\"Electric\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电流\",\"required\":false},{\"identifier\":\"Power\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"5000\"}},\"name\":\"功率\",\"required\":false},{\"identifier\":\"Speed\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"10000\"}},\"name\":\"转速\",\"required\":false},{\"identifier\":\"CurrentPressure1\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力1\",\"required\":false},{\"identifier\":\"CurrentPressure2\",\"dataType\":{\"type\":\"float\",\"specs\":{\"min\":\"0\",\"max\":\"10\"}},\"name\":\"实时压力2\",\"required\":false},{\"identifier\":\"IpmTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"IPM温度\",\"required\":false},{\"identifier\":\"MotorTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机温度\",\"required\":false},{\"identifier\":\"WaterTemperature\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"水温度\",\"required\":false},{\"identifier\":\"McuStatus\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"APP\",\"8\":\"BootLoader\"}},\"name\":\"MCU状态\",\"required\":false},{\"identifier\":\"Scene\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"增压泵\",\"1\":\"回水器\",\"2\":\"循环泵\"}},\"name\":\"设备场景\",\"required\":false},{\"identifier\":\"WarnInfo\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"警告信息\",\"required\":false},{\"identifier\":\"ActiveTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"激活时间\",\"required\":false},{\"identifier\":\"SensorMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"自动\",\"1\":\"手动\"}},\"name\":\"传感器组合模式\",\"required\":false},{\"identifier\":\"SensorGroup\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"无传感器\",\"1\":\"水流开关-压力\",\"2\":\"单水流开关\",\"3\":\"单压力\",\"4\":\"双压力\"}},\"name\":\"传感器组合\",\"required\":false},{\"identifier\":\"ElectronicTMax\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机保护温度\",\"required\":false},{\"identifier\":\"ElectronicTMaxReset\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"100\"}},\"name\":\"电机复位温度\",\"required\":false},{\"identifier\":\"History\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"历史上报\",\"required\":false},{\"identifier\":\"Model\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"设备型号\",\"required\":false},{\"identifier\":\"HandMode\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"手动模式挡位\",\"required\":false},{\"identifier\":\"TempGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"温控挡位\",\"required\":false},{\"identifier\":\"RatioGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"比例挡位\",\"required\":false},{\"identifier\":\"SpeedGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"速度挡位\",\"required\":false},{\"identifier\":\"PressureGear\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"1\",\"max\":\"5\"}},\"name\":\"压力挡位\",\"required\":false},{\"identifier\":\"SceneMode\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"节能模式-自动挡\",\"1\":\"温控模式-温控模式\",\"2\":\"一键热水-恒速模式\",\"3\":\"定时模式-恒压模式\",\"4\":\"比例模式\"}},\"name\":\"回水器/循环泵-设置模式\",\"required\":false},{\"identifier\":\"EnergyModeTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"节能模式运行时间\",\"required\":false},{\"identifier\":\"WaterTime\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"水流开关设置\",\"required\":false},{\"identifier\":\"HotWaterTime\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"一键热水运行时间\",\"required\":false},{\"identifier\":\"TempSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"温控上下限设置\",\"required\":false},{\"identifier\":\"CountDown\",\"dataType\":{\"type\":\"int32\",\"specs\":{\"min\":\"0\",\"max\":\"65532\"}},\"name\":\"回水器倒计时\",\"required\":false},{\"identifier\":\"Enabled\",\"dataType\":{\"type\":\"enum\",\"specs\":{\"0\":\"停止\",\"1\":\"开启\"}},\"name\":\"实时运行状态\",\"required\":false},{\"identifier\":\"TimeModeSet\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"定时模式时间设置\",\"required\":false},{\"identifier\":\"McuVersion\",\"dataType\":{\"type\":\"text\",\"specs\":{\"length\":\"255\"}},\"name\":\"Mcu版本号\",\"required\":false}],\"name\":\"属性上报\"}]}', 'openiitanbpump01');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint(0) NOT NULL,
  `create_by` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_dept` bigint(0) NULL DEFAULT NULL COMMENT '创建部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `curr_home_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当前家庭Id',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'email',
  `gender` int(0) NULL DEFAULT NULL COMMENT '性别 0-未知 1-male,2-female',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `permissions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限',
  `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色',
  `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密钥（密码加密后的内容）',
  `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户编号',
  `type` int(0) NULL DEFAULT NULL COMMENT '用户类型 0:平台用户 1:终端用户',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户账号',
  `use_platforms` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户使用的平台',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (432323, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, 'song', NULL, NULL, '483752384B473759556E685768796B6F644E48634F76486C51646A41576B545A2B396C34474244414F334F517674507547437A72627A7154302B724441734C5A', '000000', 1, '18126045687', NULL);
INSERT INTO `user_info` VALUES (3454543, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '天猫精灵1', NULL, NULL, NULL, '000000', 1, 'tm1', NULL);
INSERT INTO `user_info` VALUES (13123123, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '管理员', NULL, NULL, '6E49354D37437030564370666E48486150524B3134743258735059354D75324F6532594478654C47767535614C6C6E767139625170774E576477785A34513369', '000000', 0, 'iotkit', NULL);
INSERT INTO `user_info` VALUES (23423423, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '天猫精灵test3', NULL, NULL, NULL, '000000', 1, 'tm3', NULL);
INSERT INTO `user_info` VALUES (45234345, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '小度接入1', NULL, NULL, '483752384B473759556E685768796B6F644E48634F76486C51646A41576B545A2B396C34474244414F334F517674507547437A72627A7154302B724441734C5A', '000000', 1, 'du1', NULL);
INSERT INTO `user_info` VALUES (12312312312, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '小度接入2', NULL, NULL, NULL, '000000', 1, 'du2', NULL);
INSERT INTO `user_info` VALUES (141231312312, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, '演示账户', NULL, NULL, '48774861346645676F51324A4D6E6639306E6474437741634C4333746C4575666C316F76503455542B5836763065315A2F676244695056557356704D49513569', '000000', 0, 'guest1', NULL);
INSERT INTO `user_info` VALUES (13123123123213, 1, NULL, '2024-03-24 18:07:03', 1, '2024-03-24 18:07:03', NULL, NULL, NULL, NULL, NULL, 'song2', NULL, NULL, '483752384B473759556E685768796B6F644E48634F76486C51646A41576B545A2B396C34474244414F334F78574F4778613764564338594E666770595A376177', '000000', 1, '13480802157', NULL);

-- ----------------------------
-- Table structure for virtual_device
-- ----------------------------
DROP TABLE IF EXISTS `virtual_device`;
CREATE TABLE `virtual_device`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟设备名称',
  `product_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '产品key',
  `script` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '设备行为脚本',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '运行状态',
  `trigger` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '触发方式执行方式',
  `trigger_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '触发表达式',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟类型',
  `uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属用户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of virtual_device
-- ----------------------------
INSERT INTO `virtual_device` VALUES ('628fa6bc1b735b73cb260042', 1653581500076, '虚拟插座12', 'cGCrkK7Ex4FESAwe', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  var identifier=service.identifier;\n  var result= [{\n    \"productKey\":service.productKey,\n    \"deviceName\":service.deviceName,\n    \"mid\":service.mid,\n    \"type\":\"service\",\n    \"identifier\":identifier+\"_reply\",\n    \"data\":{},\n    \"code\":0\n  }]\n  \n  if(service.type==\"property\" && (identifier==\"get\" || identifier==\"set\")){\n	result.push({\n	  \"mid\":getRequestId(),\n	  \"productKey\":device.productKey,  \n	  \"deviceName\":device.deviceName,\n	  \"type\":\"property\",\n	  \"identifier\":\"report\",\n	  \"occurred\":new Date().getTime(),\n	  \"time\":new Date().getTime(),\n	  \"data\":service.params\n	});\n  }\n  \n  return result;\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n      \"rssi\":127-parseInt(Math.random()*127),\n	  \"powerstate\":Math.random()>0.5?1:0\n    }\n  }\n}', 'running', 'random', 'second', 'thingModel', '1');
INSERT INTO `virtual_device` VALUES ('628fd800fba69e633a972e12', 1653594112764, '开关1', 'Rf4QSjbm65X45753', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  return [{\n    \"productKey\":service.productKey,\n    \"deviceName\":service.deviceName,\n    \"mid\":service.mid,\n    \"type\":\"service\",\n    \"identifier\":\"reboot_reply\",\n    \"data\":{},\n    \"code\":0\n  },{\n    \"mid\":getRequestId(),\n    \"productKey\":service.productKey,  \n    \"deviceName\":service.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),\n    \"time\":new Date().getTime(),\n    \"data\":{\n      \"volt\":parseInt(Math.random()*100),\n	  \"powerstate\":Math.random()>0.5?1:0\n    }\n  }]\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n      \"volt\":parseInt(Math.random()*100)\n    }\n  }\n}', 'running', 'cron', '0 * * * * ? ', 'thingModel', '1');
INSERT INTO `virtual_device` VALUES ('62925cb72002b44c15caeb1c', 1653759159567, '虚拟门磁1', 'PN3EDmkBZDD8whDd', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  return [];\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n      \"rssi\":127-parseInt(Math.random()*127),\n      \"power\":parseInt(Math.random()*100),\n	  \"doorStatus\":Math.random()>0.5?1:0\n    }\n  }\n}', 'running', 'random', 'minute', 'thingModel', '1');
INSERT INTO `virtual_device` VALUES ('629390f492084e2df303ba3c', 1653838068860, '调光灯', 'xpsYHExTKPFaQMS7', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  return [];\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n      \"brightness\":parseInt(Math.random()*100),\n	  \"powerstate\":Math.random()>0.5?1:0\n    }\n  }\n}', 'running', 'random', 'second', 'thingModel', '1');
INSERT INTO `virtual_device` VALUES ('629391ae92084e2df303ba3d', 1653838254989, '温湿度传感器', '6kYp6jszrDns2yh4', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  return [];\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n      \"humidity\":parseInt(Math.random()*100),\n	  \"temperature\":parseInt(Math.random()*500)-38\n    }\n  }\n}', 'running', 'random', 'second', 'thingModel', '1');
INSERT INTO `virtual_device` VALUES ('6293953092084e2df303ba3e', 1653839152090, '三路开关', 'eDhXKwEzwFybM5R7', '\nvar mid=1000;\n\nfunction getMid(){\n  mid++;\n  if(mid>9999){\n	mid=1;\n  }\n  return mid+\"\";\n}\n\nfunction getRequestId(){\n  return \"RID\"+new Date().getTime()+getMid();\n}\n\n\nthis.receive=function(service,device){\n  return [];\n}\n\nthis.report=function(device){\n  return {\n    \"mid\":getRequestId(),\n    \"productKey\":device.productKey,  \n    \"deviceName\":device.deviceName,\n    \"type\":\"property\",\n    \"identifier\":\"report\",\n    \"occurred\":new Date().getTime(),	//时间戳，设备上的事件或数据产生的本地时间\n    \"time\":new Date().getTime(),		//时间戳，消息上报时间\n    \"data\":{\n	  \"rssi\":127-parseInt(Math.random()*127),\n	  \"powerstate_1\":Math.random()>0.5?1:0,\n	  \"powerstate_2\":Math.random()>0.5?1:0,\n	  \"powerstate_3\":Math.random()>0.5?1:0\n    }\n  }\n}', 'running', 'random', 'second', 'thingModel', '1');

-- ----------------------------
-- Table structure for virtual_device_mapping
-- ----------------------------
DROP TABLE IF EXISTS `virtual_device_mapping`;
CREATE TABLE `virtual_device_mapping`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备ID',
  `virtual_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟设备ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of virtual_device_mapping
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
