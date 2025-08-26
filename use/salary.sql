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

 Date: 13/08/2025 22:38:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for salary
-- ----------------------------
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` bigint NOT NULL COMMENT '员工ID，关联staff表',
  `date` date NOT NULL COMMENT '工资日期(通常为发薪月份)',
  `base_salary` decimal(10, 2) NOT NULL COMMENT '基本工资',
  `bonus` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '奖金',
  `deduction` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '扣除金额',
  `total_salary` decimal(10, 2) NOT NULL COMMENT '总工资(基本工资+奖金-扣除)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_staff_date`(`staff_id` ASC, `date` ASC) USING BTREE COMMENT '员工ID和日期的唯一索引',
  CONSTRAINT `fk_salary_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
