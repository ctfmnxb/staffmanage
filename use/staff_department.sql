/*
 Navicat Premium Data Transfer

 Source Server         : sport
 Source Server Type    : MySQL
 Source Server Version : 90100
 Source Host           : localhost:3306
 Source Schema         : staffmanage

 Target Server Type    : MySQL
 Target Server Version : 90100
 File Encoding         : 65001

 Date: 15/08/2025 22:39:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for staff_department
-- ----------------------------
DROP TABLE IF EXISTS `staff_department`;
CREATE TABLE `staff_department`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `dept_id` int NOT NULL DEFAULT 0 COMMENT '部门ID(0表示未分配)',
  `is_manager` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否是部长:0-否,1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_staff_unique`(`staff_id` ASC) USING BTREE,
  INDEX `idx_dept`(`dept_id` ASC) USING BTREE,
  CONSTRAINT `fk_sd_dept` FOREIGN KEY (`dept_id`) REFERENCES `department` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sd_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_department
-- ----------------------------
INSERT INTO `staff_department` VALUES (1, 1, 0, 0);
INSERT INTO `staff_department` VALUES (2, 29, 0, 0);
INSERT INTO `staff_department` VALUES (3, 30, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
