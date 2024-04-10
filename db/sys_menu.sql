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

 Date: 10/04/2024 22:12:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pid` bigint(0) NULL DEFAULT NULL COMMENT '上级ID，一级菜单为0',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单URL',
  `authority` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授权标识(多个用逗号分隔，如：sys:menu:list,sys:menu:save)',
  `type` tinyint(0) NULL DEFAULT NULL COMMENT '类型   0：菜单   1：按钮   2：接口',
  `open_style` tinyint(0) NULL DEFAULT NULL COMMENT '打开方式   0：内部   1：外部',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `version` int(0) NULL DEFAULT NULL COMMENT '版本号',
  `deleted` tinyint(0) NULL DEFAULT NULL COMMENT '删除标识  0：正常   1：已删除',
  `creator` bigint(0) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `updater` bigint(0) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pid`(`pid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 249 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', NULL, NULL, 0, 0, 'icon-setting', 21, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2023-01-12 10:28:59');
INSERT INTO `sys_menu` VALUES (2, 1, '菜单管理', 'sys/menu/index', NULL, 0, 0, 'icon-menu', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (3, 2, '查看', '', 'sys:menu:list', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (4, 2, '新增', '', 'sys:menu:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (5, 2, '修改', '', 'sys:menu:update,sys:menu:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (6, 2, '删除', '', 'sys:menu:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (7, 1, '数据字典', 'sys/dict/type', '', 0, 0, 'icon-insertrowabove', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (8, 7, '查询', '', 'sys:dict:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (9, 7, '新增', '', 'sys:dict:save', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (10, 7, '修改', '', 'sys:dict:update,sys:dict:info', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (11, 7, '删除', '', 'sys:dict:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (13, 1, '岗位管理', 'sys/post/index', '', 0, 0, 'icon-solution', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:06:32');
INSERT INTO `sys_menu` VALUES (14, 13, '查询', '', 'sys:post:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (15, 13, '新增', '', 'sys:post:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (16, 13, '修改', '', 'sys:post:update,sys:post:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (17, 13, '删除', '', 'sys:post:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (18, 1, '机构管理', 'sys/org/index', '', 0, 0, 'icon-cluster', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:06:25');
INSERT INTO `sys_menu` VALUES (19, 18, '查询', '', 'sys:org:list', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (20, 18, '新增', '', 'sys:org:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (21, 18, '修改', '', 'sys:org:update,sys:org:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (22, 18, '删除', '', 'sys:org:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (23, 1, '角色管理', 'sys/role/index', '', 0, 0, 'icon-team', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:06:39');
INSERT INTO `sys_menu` VALUES (24, 23, '查询', '', 'sys:role:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (25, 23, '新增', '', 'sys:role:save,sys:role:menu,sys:org:list', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (26, 23, '修改', '', 'sys:role:update,sys:role:info,sys:role:menu,sys:org:list,sys:user:page', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (27, 23, '删除', '', 'sys:role:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (28, 1, '用户管理', 'sys/user/index', '', 0, 0, 'icon-user', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:06:16');
INSERT INTO `sys_menu` VALUES (29, 28, '查询', '', 'sys:user:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (30, 28, '新增', '', 'sys:user:save,sys:role:list', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (31, 28, '修改', '', 'sys:user:update,sys:user:info,sys:role:list', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (32, 28, '删除', '', 'sys:user:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (33, 0, '应用管理', '', '', 0, 0, 'icon-appstore', 18, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:08:19');
INSERT INTO `sys_menu` VALUES (34, 1, '附件管理', 'sys/attachment/index', NULL, 0, 0, 'icon-folder-fill', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (35, 34, '查看', '', 'sys:attachment:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (36, 34, '上传', '', 'sys:attachment:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (37, 34, '删除', '', 'sys:attachment:delete', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (38, 0, '日志管理', '', '', 0, 0, 'icon-filedone', 19, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:08:14');
INSERT INTO `sys_menu` VALUES (39, 38, '登录日志', 'sys/log/login', 'sys:log:login', 0, 0, 'icon-solution', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 11:01:26');
INSERT INTO `sys_menu` VALUES (40, 33, '消息管理', '', '', 0, 0, 'icon-message', 2, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (41, 40, '短信日志', 'message/sms/log/index', 'sms:log', 0, 0, 'icon-detail', 1, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (42, 40, '短信平台', 'message/sms/platform/index', NULL, 0, 0, 'icon-whatsapp', 0, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (43, 42, '查看', '', 'sms:platform:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (44, 42, '新增', '', 'sms:platform:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (45, 42, '修改', '', 'sms:platform:update,sms:platform:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (46, 42, '删除', '', 'sms:platform:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:01:47', 10000, '2022-09-27 11:01:47');
INSERT INTO `sys_menu` VALUES (47, 1, '定时任务', 'quartz/schedule/index', NULL, 0, 0, 'icon-reloadtime', 0, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (48, 47, '查看', '', 'schedule:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (49, 47, '新增', '', 'schedule:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (50, 47, '修改', '', 'schedule:update,schedule:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (51, 47, '删除', '', 'schedule:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (52, 47, '立即运行', '', 'schedule:run', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (53, 47, '日志', '', 'schedule:log', 1, 0, '', 4, 0, 0, 10000, '2022-09-27 11:02:02', 10000, '2022-09-27 11:02:02');
INSERT INTO `sys_menu` VALUES (54, 0, '全局管理', '', '', 0, 0, 'icon-wallet', 20, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2023-01-12 10:29:04');
INSERT INTO `sys_menu` VALUES (55, 54, '数据项目管理', 'global-manage/project/index', '', 0, 0, 'icon-detail', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-10-09 12:41:16');
INSERT INTO `sys_menu` VALUES (56, 54, '数仓分层展示', 'global-manage/layer/index', '', 0, 0, 'icon-table1', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-09-27 17:12:21');
INSERT INTO `sys_menu` VALUES (57, 55, '查看', '', 'data-integrate:project:page', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 20:46:51', 10000, '2022-09-27 20:46:51');
INSERT INTO `sys_menu` VALUES (58, 55, '新增', '', 'data-integrate:project:save', 1, 0, '', 1, 0, 0, 10000, '2022-09-27 20:46:51', 10000, '2022-09-27 20:46:51');
INSERT INTO `sys_menu` VALUES (59, 55, '修改', '', 'data-integrate:project:update,data-integrate:project:info', 1, 0, '', 2, 0, 0, 10000, '2022-09-27 20:46:51', 10000, '2022-09-27 20:46:51');
INSERT INTO `sys_menu` VALUES (60, 55, '删除', '', 'data-integrate:project:delete', 1, 0, '', 3, 0, 0, 10000, '2022-09-27 20:46:51', 10000, '2022-09-27 20:46:51');
INSERT INTO `sys_menu` VALUES (61, 55, '项目成员', '', 'data-integrate:project:users', 1, 0, '', 0, 0, 0, 10000, '2022-09-27 21:28:39', 10000, '2022-09-27 21:28:39');
INSERT INTO `sys_menu` VALUES (62, 55, '添加成员', '', 'data-integrate:project:adduser', 1, 0, '', 4, 0, 0, 10000, '2022-10-07 12:00:15', 10000, '2022-10-07 12:00:25');
INSERT INTO `sys_menu` VALUES (63, 56, '查看', '', 'data-integrate:layer:page', 1, 0, '', 0, 0, 0, 10000, '2022-10-08 16:55:11', 10000, '2022-10-08 16:55:11');
INSERT INTO `sys_menu` VALUES (66, 56, '修改', '', 'data-integrate:layer:update,data-integrate:layer:info', 1, 0, '', 1, 0, 0, 10000, '2022-10-08 17:30:36', 10000, '2022-10-08 17:30:36');
INSERT INTO `sys_menu` VALUES (67, 0, '数据集成', '', '', 0, 0, 'icon-control', 11, 0, 0, 10000, '2022-10-09 12:40:06', 10000, '2024-01-23 22:31:28');
INSERT INTO `sys_menu` VALUES (68, 67, '数据库管理', 'data-integrate/database/index', '', 0, 0, 'icon-insertrowright', 0, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-10-09 12:49:18');
INSERT INTO `sys_menu` VALUES (69, 67, '文件管理', 'data-integrate/file-category/index', '', 0, 0, 'icon-layout', 1, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-10-28 16:50:34');
INSERT INTO `sys_menu` VALUES (70, 67, '数据接入', 'data-integrate/access/index', '', 0, 0, 'icon-rotate-right', 2, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-10-09 12:51:30');
INSERT INTO `sys_menu` VALUES (71, 67, '贴源数据', 'data-integrate/ods/index', '', 0, 0, 'icon-border', 3, 0, 0, 10000, '2022-09-27 11:01:26', 10000, '2022-10-09 12:53:18');
INSERT INTO `sys_menu` VALUES (72, 0, '数据开发', '', '', 0, 0, 'icon-Function', 12, 0, 0, 10000, '2022-10-09 12:56:37', 10000, '2024-01-23 22:31:24');
INSERT INTO `sys_menu` VALUES (78, 72, '数据生产', 'data-development/production/index', '', 0, 0, 'icon-Console-SQL', 1, 0, 0, 10000, '2022-10-09 13:02:24', 10000, '2023-01-03 21:32:34');
INSERT INTO `sys_menu` VALUES (80, 68, '查看', '', 'data-integrate:database:page', 1, 0, '', 0, 0, 0, 10000, '2022-10-09 17:36:31', 10000, '2022-10-09 17:36:31');
INSERT INTO `sys_menu` VALUES (81, 68, '新增', '', 'data-integrate:database:save', 1, 0, '', 1, 0, 0, 10000, '2022-10-09 17:36:56', 10000, '2022-10-09 17:38:02');
INSERT INTO `sys_menu` VALUES (82, 68, '修改', '', 'data-integrate:database:info,data-integrate:database:update', 1, 0, '', 2, 0, 0, 10000, '2022-10-09 17:37:29', 10000, '2022-10-09 17:38:10');
INSERT INTO `sys_menu` VALUES (83, 68, '删除', '', 'data-integrate:database:delete', 1, 0, '', 3, 0, 0, 10000, '2022-10-09 17:37:54', 10000, '2022-10-09 17:37:54');
INSERT INTO `sys_menu` VALUES (84, 70, '新增', '', 'data-integrate:access:save', 1, 0, '', 1, 0, 0, 10000, '2022-10-24 22:09:49', 10000, '2022-10-24 22:10:06');
INSERT INTO `sys_menu` VALUES (85, 70, '查看', '', 'data-integrate:access:page', 1, 0, '', 0, 0, 0, 10000, '2022-10-24 22:09:49', 10000, '2022-10-24 22:10:38');
INSERT INTO `sys_menu` VALUES (86, 70, '修改', '', 'data-integrate:access:update,data-integrate:access:info', 1, 0, '', 2, 0, 0, 10000, '2022-10-24 22:09:49', 10000, '2022-10-24 22:12:14');
INSERT INTO `sys_menu` VALUES (87, 70, '删除', '', 'data-integrate:access:delete', 1, 0, '', 3, 0, 0, 10000, '2022-10-24 22:09:49', 10000, '2022-10-24 22:12:19');
INSERT INTO `sys_menu` VALUES (88, 70, '发布', '', 'data-integrate:access:release', 1, 0, '', 5, 0, 0, 10000, '2022-10-27 14:32:34', 10000, '2022-10-27 14:32:34');
INSERT INTO `sys_menu` VALUES (89, 70, '取消发布', '', 'data-integrate:access:cancle', 1, 0, '', 6, 0, 0, 10000, '2022-10-27 14:33:06', 10000, '2022-10-27 14:33:06');
INSERT INTO `sys_menu` VALUES (90, 70, '手动执行', '', 'data-integrate:access:selfhandler', 1, 0, '', 7, 0, 0, 10000, '2022-10-27 22:13:07', 10000, '2022-10-27 22:13:07');
INSERT INTO `sys_menu` VALUES (91, 0, '数据治理', '', '', 0, 0, 'icon-insertrowbelow', 13, 0, 0, 10000, '2022-10-29 12:59:30', 10000, '2024-01-23 22:31:20');
INSERT INTO `sys_menu` VALUES (92, 91, '元数据', '', '', 0, 0, 'icon-file-exception', 0, 0, 0, 10000, '2022-10-29 13:01:36', 10000, '2023-01-20 13:38:53');
INSERT INTO `sys_menu` VALUES (93, 92, '元模型', 'data-governance/metamodel/index', '', 0, 0, 'icon-database', 0, 0, 0, 10000, '2022-10-29 13:05:35', 10000, '2023-03-28 11:35:56');
INSERT INTO `sys_menu` VALUES (94, 92, '元数据采集', 'data-governance/metadata-collect/index', '', 0, 0, 'icon-right-square', 1, 0, 0, 10000, '2022-10-29 13:05:35', 10000, '2023-01-20 13:38:45');
INSERT INTO `sys_menu` VALUES (97, 92, '元数据管理', 'data-governance/metadata-manage/index', '', 0, 0, 'icon-reconciliation', 2, 0, 0, 10000, '2022-10-29 13:05:35', 10000, '2023-01-20 13:38:42');
INSERT INTO `sys_menu` VALUES (98, 91, '数据血缘', 'data-governance/data-blood/index', '', 0, 0, 'icon-deleterow', 1, 0, 0, 10000, '2022-10-29 13:13:23', 10000, '2023-01-20 13:38:38');
INSERT INTO `sys_menu` VALUES (99, 0, '数据资产', '', '', 0, 0, 'icon-codelibrary-fill', 15, 0, 0, 10000, '2022-10-29 13:48:15', 10000, '2024-01-23 22:31:12');
INSERT INTO `sys_menu` VALUES (100, 99, '资产目录', 'data-assets/catalog/index', '', 0, 0, 'icon-minus-square-fill', 0, 0, 0, 10000, '2022-10-29 13:48:53', 10000, '2023-07-19 10:45:35');
INSERT INTO `sys_menu` VALUES (101, 99, '资产总览', 'data-assets/resource-overview/index', '', 0, 0, 'icon-aim', 1, 0, 0, 10000, '2022-10-29 13:50:30', 10000, '2023-01-20 13:39:06');
INSERT INTO `sys_menu` VALUES (102, 0, '数据服务', '', '', 0, 0, 'icon-transaction', 14, 0, 0, 10000, '2022-10-29 13:52:16', 10000, '2024-01-23 22:31:16');
INSERT INTO `sys_menu` VALUES (103, 102, 'API 目录', 'data-service/api-group/index', '', 0, 0, 'icon-filesearch', 0, 0, 0, 10000, '2022-10-29 13:57:03', 10000, '2023-02-16 14:48:41');
INSERT INTO `sys_menu` VALUES (105, 0, '数据集市', '', '', 0, 0, 'icon-reconciliation', 16, 0, 0, 10000, '2022-10-29 13:57:03', 10000, '2024-01-23 22:31:07');
INSERT INTO `sys_menu` VALUES (106, 105, '资产查阅', 'data-market/resource/index', '', 0, 0, 'icon-sever', 0, 0, 0, 10000, '2022-10-29 13:57:03', 10000, '2023-07-22 22:57:50');
INSERT INTO `sys_menu` VALUES (108, 105, '我的申请', 'data-market/my-apply/index', '', 0, 0, 'icon-user', 2, 0, 0, 10000, '2022-10-29 13:57:03', 10000, '2023-01-20 13:39:33');
INSERT INTO `sys_menu` VALUES (109, 105, '服务审批', 'data-market/service-check/index', '', 0, 0, 'icon-book', 3, 0, 0, 10000, '2022-10-29 13:57:03', 10000, '2023-01-20 13:39:36');
INSERT INTO `sys_menu` VALUES (110, 69, '分组新增', '', 'data-integrate:fileCategory:save', 1, 0, '', 0, 0, 0, 10000, '2022-11-14 15:17:40', 10000, '2022-11-14 15:17:55');
INSERT INTO `sys_menu` VALUES (111, 69, '分组编辑', '', 'data-integrate:fileCategory:update', 1, 0, '', 1, 0, 0, 10000, '2022-11-14 15:17:40', 10000, '2022-11-14 15:18:20');
INSERT INTO `sys_menu` VALUES (112, 69, '分组删除', '', 'data-integrate:fileCategory:delete', 1, 0, '', 2, 0, 0, 10000, '2022-11-14 15:17:40', 10000, '2022-11-14 15:18:44');
INSERT INTO `sys_menu` VALUES (113, 69, '分页查询', '', 'data-integrate:file:page', 2, 0, '', 3, 0, 0, 10000, '2022-11-18 14:22:42', 10000, '2022-11-18 14:23:04');
INSERT INTO `sys_menu` VALUES (114, 69, '新增', '', 'data-integrate:file:save', 1, 0, '', 4, 0, 0, 10000, '2022-11-18 14:22:42', 10000, '2022-11-18 14:25:48');
INSERT INTO `sys_menu` VALUES (115, 69, '修改', '', 'data-integrate:file:info,data-integrate:file:update', 1, 0, '', 5, 0, 0, 10000, '2022-11-18 14:22:42', 10000, '2022-11-18 14:26:27');
INSERT INTO `sys_menu` VALUES (116, 69, '删除', '', 'data-integrate:file:delete', 1, 0, '', 6, 0, 0, 10000, '2022-11-18 14:22:42', 10000, '2022-11-18 14:27:04');
INSERT INTO `sys_menu` VALUES (122, 143, 'Flink 集群实例', 'data-development/cluster/index', '', 0, 0, 'icon-appstore-fill', 0, 0, 0, 10000, '2022-12-03 11:21:39', 10000, '2023-01-18 13:53:44');
INSERT INTO `sys_menu` VALUES (123, 122, '查询', '', 'data-development:cluster:page', 2, 0, '', 0, 0, 0, 10000, '2022-12-03 11:22:35', 10000, '2022-12-03 11:22:35');
INSERT INTO `sys_menu` VALUES (124, 122, '添加', '', 'data-development:cluster:save', 1, 0, '', 1, 0, 0, 10000, '2022-12-03 11:23:09', 10000, '2022-12-03 11:23:09');
INSERT INTO `sys_menu` VALUES (125, 122, '修改', '', 'data-development:cluster:info,data-development:cluster:update', 1, 0, '', 2, 0, 0, 10000, '2022-12-03 11:24:47', 10000, '2022-12-03 11:24:47');
INSERT INTO `sys_menu` VALUES (126, 122, '删除', '', 'data-development:cluster:delete', 1, 0, '', 3, 0, 0, 10000, '2022-12-03 11:25:10', 10000, '2022-12-03 11:25:10');
INSERT INTO `sys_menu` VALUES (127, 143, 'Hadoop 集群配置', 'data-development/cluster-configuration/index', '', 0, 0, 'icon-insertrowabove', 1, 0, 0, 10000, '2022-12-21 20:39:34', 10000, '2023-01-18 13:53:50');
INSERT INTO `sys_menu` VALUES (128, 127, '查询', '', 'data-development:cluster-configuration:page', 1, 0, '', 0, 0, 0, 10000, '2022-12-21 20:42:02', 10000, '2022-12-21 20:42:02');
INSERT INTO `sys_menu` VALUES (129, 127, '添加', '', 'data-development:cluster-configuration:save', 1, 0, '', 1, 0, 0, 10000, '2022-12-21 20:42:39', 10000, '2022-12-21 20:42:39');
INSERT INTO `sys_menu` VALUES (130, 127, '修改', '', 'data-development:cluster-configuration:update,data-development:cluster-configuration:info', 1, 0, '', 2, 0, 0, 10000, '2022-12-21 20:43:11', 10000, '2022-12-21 20:43:11');
INSERT INTO `sys_menu` VALUES (131, 127, '删除', '', 'data-development:cluster-configuration:delete', 1, 0, '', 3, 0, 0, 10000, '2022-12-21 20:43:35', 10000, '2022-12-21 20:43:35');
INSERT INTO `sys_menu` VALUES (132, 72, '配置中心', 'data-development/sys-config/index', '', 0, 0, 'icon-project', 7, 0, 0, 10000, '2022-12-28 17:45:56', 10000, '2023-01-14 19:13:59');
INSERT INTO `sys_menu` VALUES (133, 72, '运维中心', 'data-development/task-history/index', '', 0, 0, 'icon-send', 4, 0, 0, 10000, '2023-01-03 21:30:58', 10000, '2023-01-14 19:13:11');
INSERT INTO `sys_menu` VALUES (135, 142, '调度管理', 'data-development/schedule/index', '', 0, 0, 'icon-calendar-check', 0, 0, 0, 10000, '2023-01-14 19:11:46', 10000, '2023-01-18 13:52:27');
INSERT INTO `sys_menu` VALUES (136, 135, '查询', '', 'data-development:schedule:page', 2, 0, '', 0, 0, 0, 10000, '2023-01-14 19:17:04', 10000, '2023-01-14 19:17:04');
INSERT INTO `sys_menu` VALUES (137, 135, '新增', '', 'data-development:schedule:save', 1, 0, '', 1, 0, 0, 10000, '2023-01-14 19:17:28', 10000, '2023-01-14 19:17:28');
INSERT INTO `sys_menu` VALUES (138, 135, '编辑', '', 'data-development:schedule:info,data-development:schedule:update', 1, 0, '', 2, 0, 0, 10000, '2023-01-14 19:17:54', 10000, '2023-01-14 19:17:54');
INSERT INTO `sys_menu` VALUES (139, 135, '删除', '', 'data-development:schedule:delete', 1, 0, '', 3, 0, 0, 10000, '2023-01-14 19:18:13', 10000, '2023-01-14 19:18:13');
INSERT INTO `sys_menu` VALUES (141, 135, '执行', '', 'data-development:schedule:run', 1, 0, '', 4, 0, 0, 10000, '2023-01-17 17:05:56', 10000, '2023-01-17 17:06:26');
INSERT INTO `sys_menu` VALUES (142, 72, '调度中心', '', '', 0, 0, 'icon-calendar', 3, 0, 0, 10000, '2023-01-18 13:49:14', 10000, '2023-01-18 13:51:10');
INSERT INTO `sys_menu` VALUES (143, 72, '资源中心', '', '', 0, 0, 'icon-Partition', 6, 0, 0, 10000, '2023-01-18 13:52:46', 10000, '2023-01-18 13:53:37');
INSERT INTO `sys_menu` VALUES (144, 142, '调度记录', 'data-development/schedule-record/index', '', 0, 0, 'icon-insertrowabove', 1, 0, 0, 10000, '2023-01-18 15:59:03', 10000, '2023-01-18 15:59:22');
INSERT INTO `sys_menu` VALUES (145, 144, '查询', '', 'data-development:schedule:record:page', 2, 0, '', 0, 0, 0, 10000, '2023-01-18 16:00:04', 10000, '2023-01-18 16:00:04');
INSERT INTO `sys_menu` VALUES (146, 144, '删除', '', 'data-development:schedule:record:delete', 1, 0, '', 1, 0, 0, 10000, '2023-01-18 16:00:30', 10000, '2023-01-18 16:00:30');
INSERT INTO `sys_menu` VALUES (147, 135, '发布', '', 'data-development:schedule:release', 1, 0, '', 5, 0, 0, 10000, '2023-01-19 21:45:38', 10000, '2023-01-19 21:46:34');
INSERT INTO `sys_menu` VALUES (148, 135, '取消发布', '', 'data-development:schedule:cancle', 1, 0, '', 6, 0, 0, 10000, '2023-01-19 21:47:00', 10000, '2023-01-19 21:47:00');
INSERT INTO `sys_menu` VALUES (149, 103, '修改', '', 'data-service:api-group:info,data-service:api-group:update', 2, 0, '', 1, 0, 0, 10000, '2023-01-30 11:41:31', 10000, '2023-02-06 16:04:35');
INSERT INTO `sys_menu` VALUES (150, 103, '删除', '', 'data-service:api-group:delete', 2, 0, '', 3, 0, 0, 10000, '2023-01-30 11:42:01', 10000, '2023-02-06 16:04:50');
INSERT INTO `sys_menu` VALUES (151, 103, '添加', '', 'data-service:api-group:save', 2, 0, '', 2, 0, 0, 10000, '2023-01-30 11:43:09', 10000, '2023-01-30 11:43:09');
INSERT INTO `sys_menu` VALUES (152, 103, '查看API', '', 'data-service:api-config:page', 2, 0, '', 0, 0, 0, 10000, '2023-02-06 16:04:24', 10000, '2023-02-06 16:11:16');
INSERT INTO `sys_menu` VALUES (153, 103, '新增API', '', 'data-service:api-config:save', 1, 0, '', 0, 0, 0, 10000, '2023-02-06 16:12:02', 10000, '2023-02-06 16:12:02');
INSERT INTO `sys_menu` VALUES (154, 103, '修改API', '', 'data-service:api-config:update,data-service:api-config:info', 1, 0, '', 0, 0, 0, 10000, '2023-02-06 16:12:33', 10000, '2023-02-06 16:12:33');
INSERT INTO `sys_menu` VALUES (155, 103, '删除API', '', 'data-service:api-config:delete', 1, 0, '', 0, 0, 0, 10000, '2023-02-06 16:12:58', 10000, '2023-02-14 09:56:38');
INSERT INTO `sys_menu` VALUES (156, 103, '上线', '', 'data-service:api-config:online', 1, 0, '', 0, 0, 0, 10000, '2023-02-15 11:15:52', 10000, '2023-02-15 11:16:23');
INSERT INTO `sys_menu` VALUES (157, 103, '下线', '', 'data-service:api-config:offline', 1, 0, '', 0, 0, 0, 10000, '2023-02-15 11:16:37', 10000, '2023-02-15 11:16:37');
INSERT INTO `sys_menu` VALUES (158, 102, 'API 权限', 'data-service/app/index', '', 0, 0, 'icon-propertysafety', 1, 0, 0, 10000, '2023-02-16 14:48:26', 10000, '2023-02-16 14:56:47');
INSERT INTO `sys_menu` VALUES (159, 158, '查询', '', 'data-service:app:page', 2, 0, '', 0, 0, 0, 10000, '2023-02-16 14:50:15', 10000, '2023-02-16 14:50:15');
INSERT INTO `sys_menu` VALUES (160, 158, '保存', '', 'data-service:app:save', 1, 0, '', 1, 0, 0, 10000, '2023-02-16 14:50:39', 10000, '2023-02-16 14:50:39');
INSERT INTO `sys_menu` VALUES (161, 158, '更新', '', 'data-service:app:update,data-service:app:info', 1, 0, '', 2, 0, 0, 10000, '2023-02-16 14:51:10', 10000, '2023-02-16 14:51:10');
INSERT INTO `sys_menu` VALUES (162, 158, '删除', '', 'data-service:app:delete', 1, 0, '', 3, 0, 0, 10000, '2023-02-16 14:51:28', 10000, '2023-02-16 14:51:39');
INSERT INTO `sys_menu` VALUES (163, 158, '授权', '', 'data-service:app:auth', 1, 0, '', 4, 0, 0, 10000, '2023-02-17 11:37:39', 10000, '2023-02-17 11:37:39');
INSERT INTO `sys_menu` VALUES (164, 158, '取消授权', '', 'data-service:app:cancel-auth', 1, 0, '', 5, 0, 0, 10000, '2023-02-20 14:11:42', 10000, '2023-02-20 14:13:31');
INSERT INTO `sys_menu` VALUES (165, 102, 'API 日志', 'data-service/log/index', '', 0, 0, 'icon-detail', 2, 0, 0, 10000, '2023-02-22 14:47:37', 10000, '2023-02-22 14:48:31');
INSERT INTO `sys_menu` VALUES (166, 165, '查询', '', 'data-service:log:page', 2, 0, '', 0, 0, 0, 10000, '2023-02-22 14:49:07', 10000, '2023-02-22 14:49:07');
INSERT INTO `sys_menu` VALUES (167, 165, '删除', '', 'data-service:log:delete', 1, 0, '', 1, 0, 0, 10000, '2023-02-22 14:49:25', 10000, '2023-02-22 14:49:25');
INSERT INTO `sys_menu` VALUES (168, 94, '查询', '', 'data-governance:metadata-collect:page', 2, 0, '', 0, 0, 0, 10000, '2023-04-03 10:39:26', 10000, '2023-04-03 10:39:26');
INSERT INTO `sys_menu` VALUES (169, 94, '编辑', '', 'data-governance:metadata-collect:info,data-governance:metadata-collect:update', 1, 0, '', 1, 0, 0, 10000, '2023-04-03 10:40:06', 10000, '2023-04-03 10:40:06');
INSERT INTO `sys_menu` VALUES (170, 94, '保存', '', 'data-governance:metadata-collect:save', 1, 0, '', 2, 0, 0, 10000, '2023-04-03 10:40:25', 10000, '2023-04-03 10:40:42');
INSERT INTO `sys_menu` VALUES (171, 94, '删除', '', 'data-governance:metadata-collect:delete', 1, 0, '', 3, 0, 0, 10000, '2023-04-03 10:41:05', 10000, '2023-04-03 10:41:05');
INSERT INTO `sys_menu` VALUES (172, 94, '发布', '', 'data-governance:metadata-collect:release', 1, 0, '', 4, 0, 0, 10000, '2023-04-05 12:21:56', 10000, '2023-04-05 12:21:56');
INSERT INTO `sys_menu` VALUES (173, 94, '取消发布', '', 'data-governance:metadata-collect:cancel', 1, 0, '', 5, 0, 0, 10000, '2023-04-05 12:22:19', 10000, '2023-04-05 12:22:19');
INSERT INTO `sys_menu` VALUES (174, 94, '执行', '', 'data-governance:metadata-collect:hand-run', 1, 0, '', 6, 0, 0, 10000, '2023-04-06 09:59:53', 10000, '2023-04-06 09:59:53');
INSERT INTO `sys_menu` VALUES (175, 91, '数据标准', '', '', 0, 0, 'icon-calculator', 2, 0, 0, 10000, '2023-05-08 09:39:12', 10000, '2023-05-08 09:39:25');
INSERT INTO `sys_menu` VALUES (176, 175, '标准管理', 'data-governance/data-standard/index', '', 0, 0, 'icon-wallet', 0, 0, 0, 10000, '2023-05-08 09:41:37', 10000, '2023-05-08 09:42:17');
INSERT INTO `sys_menu` VALUES (177, 91, '数据质量', '', '', 0, 0, 'icon-creditcard', 3, 0, 0, 10000, '2023-05-28 09:46:54', 10000, '2023-05-28 09:46:54');
INSERT INTO `sys_menu` VALUES (178, 177, '质量规则', 'data-governance/quality-rule/index', '', 0, 0, 'icon-USB-fill', 0, 0, 0, 10000, '2023-05-28 09:47:52', 10000, '2023-05-28 09:47:52');
INSERT INTO `sys_menu` VALUES (179, 177, '规则配置', 'data-governance/quality-rule/rule-category', '', 0, 0, 'icon-formatpainter-fill', 1, 0, 0, 10000, '2023-05-29 10:40:34', 10000, '2023-05-29 10:40:56');
INSERT INTO `sys_menu` VALUES (180, 177, '质量任务', 'data-governance/quality-task/index', '', 0, 0, 'icon-database-fill', 2, 0, 0, 10000, '2023-06-24 21:43:46', 10000, '2023-06-24 21:44:14');
INSERT INTO `sys_menu` VALUES (181, 105, '我的应用', 'data-market/my-app/index', '', 0, 0, 'icon-merge-cells', 3, 0, 0, 10000, '2023-08-23 16:41:34', 10000, '2023-08-23 16:43:20');
INSERT INTO `sys_menu` VALUES (182, 91, '主数据管理', '', '', 0, 0, 'icon-detail', 5, 0, 0, 10000, '2023-09-27 10:29:45', 10000, '2023-09-27 10:30:45');
INSERT INTO `sys_menu` VALUES (183, 182, '主数据模型', 'data-governance/master-data/index', '', 0, 0, 'icon-insertrowleft', 0, 0, 0, 10000, '2023-09-27 10:31:16', 10000, '2023-09-27 10:31:16');
INSERT INTO `sys_menu` VALUES (184, 182, '主数据派发', 'data-governance/master-distribute/index', '', 0, 0, 'icon-send', 1, 0, 0, 10000, '2023-10-08 11:44:45', 10000, '2023-10-08 11:45:06');
INSERT INTO `sys_menu` VALUES (185, 72, '数据表', 'data-development/data-table/index', '', 0, 0, 'icon-insertrowright', 0, 0, 0, 10000, '2023-10-13 09:19:45', 10000, '2023-10-13 09:19:45');
INSERT INTO `sys_menu` VALUES (186, 143, 'JAR 包管理', 'data-development/jar/index', '', 0, 0, 'icon-layout-fill', 2, 0, 0, 10000, '2023-11-13 10:40:10', 10000, '2023-11-13 10:40:10');
INSERT INTO `sys_menu` VALUES (187, 186, '查询', '', 'data-development:jar:page', 2, 0, '', 0, 0, 0, 10000, '2023-11-13 10:41:48', 10000, '2023-11-13 10:41:48');
INSERT INTO `sys_menu` VALUES (188, 186, '新增', '', 'data-development:jar:save', 1, 0, '', 1, 0, 0, 10000, '2023-11-13 10:42:18', 10000, '2023-11-13 10:42:18');
INSERT INTO `sys_menu` VALUES (189, 186, '修改', '', 'data-development:jar:update,data-development:jar:info', 1, 0, '', 2, 0, 0, 10000, '2023-11-13 10:42:59', 10000, '2023-11-13 10:44:02');
INSERT INTO `sys_menu` VALUES (190, 186, '删除', '', 'data-development:jar:delete', 1, 0, '', 3, 0, 0, 10000, '2023-11-13 10:43:18', 10000, '2023-11-13 10:44:07');
INSERT INTO `sys_menu` VALUES (191, 91, '数据标签', '', '', 0, 0, 'icon-formatpainter', 5, 0, 0, 10000, '2023-12-24 12:07:57', 10000, '2023-12-24 12:07:57');
INSERT INTO `sys_menu` VALUES (192, 191, '标签类型', 'data-governance/label-type/index', '', 0, 0, 'icon-rotate-right', 0, 0, 0, 10000, '2023-12-24 12:09:18', 10000, '2023-12-24 12:17:09');
INSERT INTO `sys_menu` VALUES (193, 192, '查询', '', 'data-governance:label-type:page', 2, 0, '', 0, 0, 0, 10000, '2023-12-24 12:10:10', 10000, '2023-12-24 12:10:10');
INSERT INTO `sys_menu` VALUES (194, 192, '新增', '', 'data-governance:label-type:save', 1, 0, '', 1, 0, 0, 10000, '2023-12-24 12:10:32', 10000, '2023-12-24 12:10:45');
INSERT INTO `sys_menu` VALUES (195, 192, '修改', '', 'data-governance:label-type:info,data-governance:label-type:update', 1, 0, '', 2, 0, 0, 10000, '2023-12-24 12:11:15', 10000, '2023-12-24 12:11:15');
INSERT INTO `sys_menu` VALUES (196, 192, '删除', '', 'data-governance:label-type:delete', 1, 0, '', 3, 0, 0, 10000, '2023-12-24 12:11:37', 10000, '2023-12-24 12:11:37');
INSERT INTO `sys_menu` VALUES (197, 191, '标签实体', 'data-governance/label-model/index', '', 0, 0, 'icon-YUAN-circle-fill', 1, 0, 0, 10000, '2023-12-26 18:09:34', 10000, '2023-12-26 18:09:34');
INSERT INTO `sys_menu` VALUES (198, 197, '查询', '', 'data-governance:label-model:page', 2, 0, '', 0, 0, 0, 10000, '2023-12-26 18:10:54', 10000, '2023-12-26 18:10:54');
INSERT INTO `sys_menu` VALUES (199, 197, '新增', '', 'data-governance:label-model:save', 1, 0, '', 1, 0, 0, 10000, '2023-12-26 18:11:15', 10000, '2023-12-26 18:11:15');
INSERT INTO `sys_menu` VALUES (200, 197, '更新', '', 'data-governance:label-model:info,data-governance:label-model:update', 1, 0, '', 2, 0, 0, 10000, '2023-12-26 18:11:44', 10000, '2023-12-26 18:11:44');
INSERT INTO `sys_menu` VALUES (201, 197, '删除', '', 'data-governance:label-model:delete', 1, 0, '', 3, 0, 0, 10000, '2023-12-26 18:12:05', 10000, '2023-12-26 18:12:05');
INSERT INTO `sys_menu` VALUES (202, 191, '标签类目', 'data-governance/label/index', '', 0, 0, 'icon-diff', 2, 0, 0, 10000, '2023-12-26 18:16:04', 10000, '2023-12-26 18:16:04');
INSERT INTO `sys_menu` VALUES (203, 191, '智能查询', 'data-governance/label-query/index', '', 0, 0, 'icon-read', 3, 0, 0, 10000, '2023-12-26 18:17:54', 10000, '2023-12-26 18:17:54');
INSERT INTO `sys_menu` VALUES (204, 0, '供应链管理', '', '', 0, 0, 'icon-Partition', 0, 0, 0, 10000, '2023-12-01 09:16:12', 10000, '2023-12-01 09:20:05');
INSERT INTO `sys_menu` VALUES (205, 204, '供需平衡模拟', 'supply-chain/supply-demand-balance/index', '', 0, 0, '', 1, 0, 0, 10000, '2023-12-01 09:18:15', 10000, '2023-12-01 09:18:15');
INSERT INTO `sys_menu` VALUES (206, 204, '库存模拟与控制', 'supply-chain/inventory-control/index', '', 0, 0, '', 2, 0, 0, 10000, '2023-12-01 09:18:50', 10000, '2023-12-01 09:18:50');
INSERT INTO `sys_menu` VALUES (207, 204, '系统参数配置', 'supply-chain/system-parameter/index', '', 0, 0, '', 0, 0, 0, 10000, '2023-12-01 09:19:35', 10000, '2023-12-01 09:19:35');
INSERT INTO `sys_menu` VALUES (208, 194, '查询', '', 'supply-chain:system_parameter:page', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:20:44', 10000, '2023-12-01 09:20:44');
INSERT INTO `sys_menu` VALUES (209, 194, '初始化', '', 'supply-chain:system_parameter:init', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:21:25', 10000, '2023-12-01 09:22:44');
INSERT INTO `sys_menu` VALUES (210, 194, '详情', '', 'supply-chain:system_parameter:info', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:21:47', 10000, '2023-12-01 09:21:47');
INSERT INTO `sys_menu` VALUES (211, 194, '保存', '', 'supply-chain:system_parameter:save', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:22:04', 10000, '2023-12-01 09:22:04');
INSERT INTO `sys_menu` VALUES (212, 194, '修改', '', 'supply-chain:system_parameter:update', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:22:21', 10000, '2023-12-01 09:22:21');
INSERT INTO `sys_menu` VALUES (214, 204, '交付周期管理', 'supply-chain/period-tree-view/index', '', 0, 0, '', 3, 0, 0, 10000, '2023-12-30 20:57:18', 10000, '2024-01-08 11:50:50');
INSERT INTO `sys_menu` VALUES (216, 0, '大屏设计器', '', '', 0, 0, 'icon-calculator-fill', 2, 0, 0, 10000, '2023-12-03 00:03:53', 10000, '2023-12-03 00:23:27');
INSERT INTO `sys_menu` VALUES (217, 216, '大屏管理', 'bigscreen/dashboard', '', 0, 0, '', 1, 0, 0, 10000, '2023-12-03 00:05:54', 10000, '2023-12-03 00:06:50');
INSERT INTO `sys_menu` VALUES (218, 216, '资源库', 'bigscreen/resources', '', 0, 0, '', 2, 0, 0, 10000, '2023-12-03 00:07:38', 10000, '2023-12-03 00:09:03');
INSERT INTO `sys_menu` VALUES (219, 216, '组件库', 'bigscreen/components', '', 0, 0, '', 3, 0, 0, 10000, '2023-12-03 00:09:54', 10000, '2023-12-03 00:19:32');
INSERT INTO `sys_menu` VALUES (220, 216, '数据源管理', 'bigscreen/dataSource', '', 0, 0, '', 4, 0, 0, 10000, '2023-12-03 00:10:51', 10000, '2023-12-03 00:20:02');
INSERT INTO `sys_menu` VALUES (221, 216, '数据集管理', 'bigscreen/dataSet', '', 0, 0, '', 5, 0, 0, 10000, '2023-12-03 00:11:30', 10000, '2023-12-03 00:20:32');
INSERT INTO `sys_menu` VALUES (222, 216, '地图数据管理', 'bigscreen/map-data', '', 0, 0, '', 6, 0, 1, 10000, '2023-12-03 00:12:06', 10000, '2023-12-03 00:21:29');
INSERT INTO `sys_menu` VALUES (223, 194, '删除', '', 'supply-chain:system_parameter:delete', 1, 0, '', 0, 0, 0, 10000, '2023-12-01 09:22:36', 10000, '2023-12-01 09:22:36');
INSERT INTO `sys_menu` VALUES (224, 0, '物联网', '', '', 0, 0, 'icon-merge-cells', 3, 0, 0, 10000, '2024-03-25 13:36:04', 10000, '2024-03-26 18:17:25');
INSERT INTO `sys_menu` VALUES (225, 0, '视频监控', '', '', 0, 0, 'icon-formatpainter', 4, 0, 0, 10000, '2024-03-25 13:36:55', 10000, '2024-03-26 18:17:38');
INSERT INTO `sys_menu` VALUES (226, 0, 'AI监管', '', '', 0, 0, 'icon-switchuser', 5, 0, 0, 10000, '2024-03-25 13:37:17', 10000, '2024-03-26 18:18:51');
INSERT INTO `sys_menu` VALUES (227, 224, '设备接入', '', '', 0, 0, 'icon-QQ-square-fill', 1, 0, 0, 10000, '2024-03-26 18:21:58', 10000, '2024-04-07 21:37:53');
INSERT INTO `sys_menu` VALUES (228, 224, 'OTA', 'iot/ota/upgradePack/index', '', 0, 0, 'icon-slack-circle-fill', 2, 0, 0, 10000, '2024-03-26 18:22:21', 10000, '2024-04-07 21:31:51');
INSERT INTO `sys_menu` VALUES (229, 224, '规则引擎', '', '', 0, 0, 'icon-database', 3, 0, 0, 10000, '2024-03-26 18:22:56', 10000, '2024-04-07 21:32:48');
INSERT INTO `sys_menu` VALUES (230, 224, '消息中心', '', '', 0, 0, 'icon-comment', 4, 0, 0, 10000, '2024-03-26 18:23:24', 10000, '2024-04-07 21:52:58');
INSERT INTO `sys_menu` VALUES (231, 224, '通道管理', '', '', 0, 0, 'icon-QQ', 5, 0, 0, 10000, '2024-03-26 18:23:41', 10000, '2024-04-07 21:41:35');
INSERT INTO `sys_menu` VALUES (232, 224, '告警中心', '', '', 0, 0, 'icon-bell', 6, 0, 0, 10000, '2024-03-26 18:23:52', 10000, '2024-04-07 21:46:17');
INSERT INTO `sys_menu` VALUES (233, 224, '租户管理', '', '', 0, 0, 'icon-switchuser', 7, 0, 0, 10000, '2024-03-26 18:24:05', 10000, '2024-04-07 21:52:35');
INSERT INTO `sys_menu` VALUES (234, 227, '品类管理', 'iot/equipment/categories/index', '', 0, 0, 'icon-insertrowleft', 1, 0, 0, 10000, '2024-04-03 14:58:39', 10000, '2024-04-03 15:05:22');
INSERT INTO `sys_menu` VALUES (235, 227, '产品管理', 'iot/equipment/products/index', '', 0, 0, 'icon-codelibrary', 2, 0, 0, 10000, '2024-04-07 21:21:39', 10000, '2024-04-07 21:23:50');
INSERT INTO `sys_menu` VALUES (236, 227, '设备分组', 'iot/equipment/devices/deviceGroup', '', 0, 0, 'icon-merge-cells', 3, 0, 0, 10000, '2024-04-07 21:23:26', 10000, '2024-04-07 21:24:03');
INSERT INTO `sys_menu` VALUES (237, 227, '设备列表', 'iot/equipment/devices/list', '', 0, 0, 'icon-formatpainter', 4, 0, 0, 10000, '2024-04-07 21:25:56', 10000, '2024-04-07 21:26:21');
INSERT INTO `sys_menu` VALUES (238, 227, '插件管理', 'iot/plugins/index', '', 0, 0, 'icon-Partition', 5, 0, 0, 10000, '2024-04-07 21:27:40', 10000, '2024-04-08 10:52:03');
INSERT INTO `sys_menu` VALUES (239, 227, '虚拟设备', 'iot/equipment/devices/virtualDevices', '', 0, 0, 'icon-expend', 6, 0, 0, 10000, '2024-04-07 21:30:35', 10000, '2024-04-07 21:30:35');
INSERT INTO `sys_menu` VALUES (240, 229, '定时任务', 'iot/ruleEngine/scheduledTask/index', '', 0, 0, 'icon-calendar', 1, 0, 0, 10000, '2024-04-07 21:34:21', 10000, '2024-04-07 21:37:16');
INSERT INTO `sys_menu` VALUES (241, 229, '规则管理', 'iot/ruleEngine/ruleSys/index', '', 0, 0, 'icon-earth', 2, 0, 0, 10000, '2024-04-07 21:36:06', 10000, '2024-04-07 21:36:06');
INSERT INTO `sys_menu` VALUES (242, 230, '消息列表', 'iot/messageCenter/list', '', 0, 0, 'icon-sound-fill', 1, 0, 0, 10000, '2024-04-07 21:39:35', 10000, '2024-04-07 21:39:35');
INSERT INTO `sys_menu` VALUES (243, 231, '通道配置', 'iot/channel/config', '', 0, 0, 'icon-aliwangwang-fill', 1, 0, 0, 10000, '2024-04-07 21:43:42', 10000, '2024-04-07 21:43:42');
INSERT INTO `sys_menu` VALUES (244, 231, '模板配置', 'iot/channel/template', '', 0, 0, 'icon-slack-square-fill', 2, 0, 0, 10000, '2024-04-07 21:45:02', 10000, '2024-04-07 21:45:02');
INSERT INTO `sys_menu` VALUES (245, 232, '告警列表', 'iot/alarm/list', '', 0, 0, 'icon-message', 1, 0, 0, 10000, '2024-04-07 21:47:46', 10000, '2024-04-07 21:53:43');
INSERT INTO `sys_menu` VALUES (246, 232, '告警配置', 'iot/alarm/config', '', 0, 0, 'icon-setting-fill', 2, 0, 0, 10000, '2024-04-07 21:49:09', 10000, '2024-04-07 21:49:09');
INSERT INTO `sys_menu` VALUES (247, 233, '租户管理', '', '', 0, 0, 'icon-unorderedlist', 1, 0, 0, 10000, '2024-04-07 21:50:36', 10000, '2024-04-07 21:50:36');
INSERT INTO `sys_menu` VALUES (248, 233, '租户套餐管理', '', '', 0, 0, 'icon-pic-right', 2, 0, 0, 10000, '2024-04-07 21:51:40', 10000, '2024-04-07 21:51:40');

SET FOREIGN_KEY_CHECKS = 1;
