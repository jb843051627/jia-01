-- =============================================
-- 定时任务初始化数据
-- 包含：每日统计报表、每小时检查超时未派单
-- =============================================

-- 可选：如果表不存在则创建（表结构与现有 t_sys_quartz_job 保持一致）
CREATE TABLE IF NOT EXISTS `t_sys_quartz_job` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT NULL COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT 'cron计划策略 1立即执行 2执行一次 3放弃执行',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` tinyint DEFAULT '0' COMMENT '任务状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度表';

-- 可选：如果日志表不存在则创建
CREATE TABLE IF NOT EXISTS `t_sys_quartz_job_log` (
  `id` bigint NOT NULL COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` tinyint DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT NULL COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务调度日志表';

-- =============================================
-- 插入两个示例任务
-- 注意：ID 使用雪花算法生成的 Long 型数字
-- =============================================

-- 示例任务1：每天凌晨统计报表（每天00:05执行）
INSERT INTO `t_sys_quartz_job`
(`id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `remark`)
VALUES
('1809012345678901234', '每日统计报表', 'SYSTEM', 'sampleTask.dailyStatisticsReport()', '0 5 0 * * ?', '3', '1', '0', 'admin', NOW(), '每天凌晨00:05执行，统计前一天的订单数据（订单总数、完成订单数、总金额等）');

-- 示例任务2：每小时检查超时未派单（每小时第0分钟执行）
INSERT INTO `t_sys_quartz_job`
(`id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `remark`)
VALUES
('1809012345678901235', '检查超时未派单', 'SYSTEM', 'sampleTask.checkTimeoutDispatch()', '0 0 * * * ?', '3', '1', '0', 'admin', NOW(), '每小时整点执行，检查创建时间超过2小时但仍未派单的订单');

-- =============================================
-- 常用 cron 表达式参考：
-- 0 0 2 * * ?        每天凌晨2点
-- 0 0 0 * * ?        每天零点
-- 0 0 * * * ?        每小时整点
-- 0 */5 * * * ?      每5分钟
-- 0 0/30 * * * ?     每30分钟
-- 0 0 1 * * ?        每天凌晨1点
-- 0 0 8 * * ?        每天早上8点
-- 0 0 22 * * ?       每天晚上10点
-- =============================================
