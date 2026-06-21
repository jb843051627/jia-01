-- 宠物表
DROP TABLE IF EXISTS `t_pet`;
CREATE TABLE `t_pet` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '宠物名字',
  `breed` varchar(50) DEFAULT NULL COMMENT '品种',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `gender` char(1) DEFAULT '0' COMMENT '性别 0=公,1=母',
  `owner_name` varchar(50) NOT NULL COMMENT '主人姓名',
  `owner_phone` varchar(20) DEFAULT NULL COMMENT '主人电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_breed` (`breed`),
  KEY `idx_owner_name` (`owner_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物表';

-- 宠物照片表
DROP TABLE IF EXISTS `t_pet_photo`;
CREATE TABLE `t_pet_photo` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `pet_id` bigint(20) NOT NULL COMMENT '宠物ID',
  `photo_url` varchar(255) NOT NULL COMMENT '照片URL',
  `photo_name` varchar(100) DEFAULT NULL COMMENT '照片名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pet_id` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物照片表';

-- 服务类型表
DROP TABLE IF EXISTS `t_service_type`;
CREATE TABLE `t_service_type` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '服务名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '服务价格',
  `duration` int(11) DEFAULT NULL COMMENT '服务时长(分钟)',
  `description` varchar(500) DEFAULT NULL COMMENT '服务描述',
  `status` char(1) DEFAULT '0' COMMENT '状态 0=启用,1=禁用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务类型表';

-- 预约表
DROP TABLE IF EXISTS `t_appointment`;
CREATE TABLE `t_appointment` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `appointment_no` varchar(32) NOT NULL COMMENT '预约单号',
  `pet_id` bigint(20) NOT NULL COMMENT '宠物ID',
  `service_type_id` bigint(20) NOT NULL COMMENT '服务类型ID',
  `customer_name` varchar(50) NOT NULL COMMENT '客户姓名',
  `customer_phone` varchar(20) NOT NULL COMMENT '客户电话',
  `appointment_time` datetime NOT NULL COMMENT '预约时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` char(1) DEFAULT '0' COMMENT '状态 0=待确认,1=已确认,2=服务中,3=已完成,4=已取消',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_appointment_time` (`appointment_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 插入默认服务类型数据
INSERT INTO `t_service_type` (`id`, `name`, `price`, `duration`, `description`, `status`, `create_by`, `create_time`) VALUES
(1, '基础殡葬服务', 1980.00, 120, '包含遗体清洁、告别仪式、基本火化', '0', 'admin', NOW()),
(2, '高级殡葬服务', 3980.00, 180, '包含遗体清洁、整容、告别仪式、独立火化、骨灰盒', '0', 'admin', NOW()),
(3, '豪华殡葬服务', 6880.00, 240, '包含遗体清洁、整容、守灵、告别仪式、独立火化、高档骨灰盒、下葬仪式', '0', 'admin', NOW()),
(4, '宠物祭祀服务', 580.00, 60, '提供宠物祭祀场地和用品', '0', 'admin', NOW()),
(5, '骨灰寄存服务', 365.00, 0, '按年计费，提供专业骨灰寄存', '0', 'admin', NOW());

-- ========================================
-- 宠物管理菜单和权限
-- ========================================

-- 宠物管理目录
INSERT INTO `t_sys_permission` VALUES
(9201000000000000001, '宠物管理', '宠物档案管理', NULL, 0, 0, NULL, 0, 'layui-icon layui-icon-group', 7, 0, 'admin', NOW(), NULL, NULL, NULL);

-- 宠物管理菜单
INSERT INTO `t_sys_permission` VALUES
(9201000000000000002, '宠物管理', '宠物管理列表', '/PetController/view', 0, 9201000000000000001, 'system:pet:view', 1, 'layui-icon layui-icon-list', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

-- 宠物管理按钮权限
INSERT INTO `t_sys_permission` VALUES
(9201000000000000003, '宠物列表', '宠物列表查询', '/PetController/list', 0, 9201000000000000002, 'system:pet:list', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9201000000000000004, '宠物添加', '宠物添加', '/PetController/add', 0, 9201000000000000002, 'system:pet:add', 2, 'layui-icon layui-icon-add-1', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9201000000000000005, '宠物删除', '宠物删除', '/PetController/remove', 0, 9201000000000000002, 'system:pet:remove', 2, 'layui-icon layui-icon-delete', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9201000000000000006, '宠物修改', '宠物修改', '/PetController/edit', 0, 9201000000000000002, 'system:pet:edit', 2, 'layui-icon layui-icon-edit', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9201000000000000007, '照片上传', '照片上传', '/PetController/uploadPhoto', 0, 9201000000000000002, 'system:pet:upload', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9201000000000000008, '照片删除', '照片删除', '/PetController/removePhoto', 0, 9201000000000000002, 'system:pet:deletePhoto', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

-- ========================================
-- 预约管理菜单和权限
-- ========================================

-- 预约管理目录
INSERT INTO `t_sys_permission` VALUES
(9202000000000000001, '预约管理', '预约服务管理', NULL, 0, 0, NULL, 0, 'layui-icon layui-icon-date', 8, 0, 'admin', NOW(), NULL, NULL, NULL);

-- 预约管理菜单
INSERT INTO `t_sys_permission` VALUES
(9202000000000000002, '预约管理', '预约管理列表', '/AppointmentController/view', 0, 9202000000000000001, 'system:appointment:view', 1, 'layui-icon layui-icon-list', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

-- 预约管理按钮权限
INSERT INTO `t_sys_permission` VALUES
(9202000000000000003, '预约列表', '预约列表查询', '/AppointmentController/list', 0, 9202000000000000002, 'system:appointment:list', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9202000000000000004, '预约添加', '预约添加', '/AppointmentController/add', 0, 9202000000000000002, 'system:appointment:add', 2, 'layui-icon layui-icon-add-1', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9202000000000000005, '预约删除', '预约删除', '/AppointmentController/remove', 0, 9202000000000000002, 'system:appointment:remove', 2, 'layui-icon layui-icon-delete', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9202000000000000006, '预约修改', '预约修改', '/AppointmentController/edit', 0, 9202000000000000002, 'system:appointment:edit', 2, 'layui-icon layui-icon-edit', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9202000000000000007, '状态更新', '预约状态更新', '/AppointmentController/updateStatus', 0, 9202000000000000002, 'system:appointment:updateStatus', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9202000000000000008, '冲突检测', '预约时间冲突检测', '/AppointmentController/checkTimeConflict', 0, 9202000000000000002, 'system:appointment:check', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

-- ========================================
-- 为超级管理员角色分配权限 (超级管理员角色ID为488243256161730560)
-- ========================================
INSERT INTO `t_sys_permission_role` (`id`, `role_id`, `permission_id`) VALUES
(9201000000000000101, 488243256161730560, 9201000000000000001),
(9201000000000000102, 488243256161730560, 9201000000000000002),
(9201000000000000103, 488243256161730560, 9201000000000000003),
(9201000000000000104, 488243256161730560, 9201000000000000004),
(9201000000000000105, 488243256161730560, 9201000000000000005),
(9201000000000000106, 488243256161730560, 9201000000000000006),
(9201000000000000107, 488243256161730560, 9201000000000000007),
(9201000000000000108, 488243256161730560, 9201000000000000008),
(9202000000000000101, 488243256161730560, 9202000000000000001),
(9202000000000000102, 488243256161730560, 9202000000000000002),
(9202000000000000103, 488243256161730560, 9202000000000000003),
(9202000000000000104, 488243256161730560, 9202000000000000004),
(9202000000000000105, 488243256161730560, 9202000000000000005),
(9202000000000000106, 488243256161730560, 9202000000000000006),
(9202000000000000107, 488243256161730560, 9202000000000000007),
(9202000000000000108, 488243256161730560, 9202000000000000008);

-- ========================================
-- 服务流程表
-- ========================================
DROP TABLE IF EXISTS `t_service_flow`;
CREATE TABLE `t_service_flow` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `appointment_id` bigint(20) NOT NULL COMMENT '预约ID',
  `node_name` varchar(50) NOT NULL COMMENT '节点名称',
  `node_code` varchar(50) NOT NULL COMMENT '节点编码(reception/farewell/cremation/ashes)',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态 0=待处理,1=进行中,2=已完成',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_id` (`appointment_id`),
  KEY `idx_node_code` (`node_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务流程节点表';

-- ========================================
-- 服务流程节点照片表
-- ========================================
DROP TABLE IF EXISTS `t_service_flow_photo`;
CREATE TABLE `t_service_flow_photo` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `flow_id` bigint(20) NOT NULL COMMENT '流程节点ID',
  `photo_url` varchar(255) NOT NULL COMMENT '照片URL',
  `photo_name` varchar(100) DEFAULT NULL COMMENT '照片名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_flow_id` (`flow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务流程节点照片表';

-- ========================================
-- 服务流程管理权限
-- ========================================
INSERT INTO `t_sys_permission` VALUES
(9203000000000000001, '服务流程查看', '查看服务流程', '/ServiceFlowController/list', 0, 9202000000000000002, 'system:serviceFlow:list', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9203000000000000002, '服务流程操作', '完成节点/上传照片', '/ServiceFlowController/completeNode', 0, 9202000000000000002, 'system:serviceFlow:operate', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

INSERT INTO `t_sys_permission_role` (`id`, `role_id`, `permission_id`) VALUES
(9203000000000000101, 488243256161730560, 9203000000000000001),
(9203000000000000102, 488243256161730560, 9203000000000000002);

-- ========================================
-- 增量更新脚本（已有数据库执行以下SQL）
-- ========================================
-- 1. 创建服务流程节点照片表
-- CREATE TABLE `t_service_flow_photo` (
--   `id` bigint(20) NOT NULL COMMENT '主键ID',
--   `flow_id` bigint(20) NOT NULL COMMENT '流程节点ID',
--   `photo_url` varchar(255) NOT NULL COMMENT '照片URL',
--   `photo_name` varchar(100) DEFAULT NULL COMMENT '照片名称',
--   `sort` int(11) DEFAULT '0' COMMENT '排序',
--   `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
--   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
--   PRIMARY KEY (`id`),
--   KEY `idx_flow_id` (`flow_id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务流程节点照片表';

-- 2. 从 t_service_flow 表移除 photo_url 列
-- ALTER TABLE `t_service_flow` DROP COLUMN `photo_url`;
