DROP TABLE IF EXISTS `t_customer_tag`;
DROP TABLE IF EXISTS `t_customer_profile`;

CREATE TABLE `t_customer_profile` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `customer_name` varchar(50) NOT NULL COMMENT '客户姓名',
  `customer_phone` varchar(20) NOT NULL COMMENT '客户电话',
  `first_service_time` datetime DEFAULT NULL COMMENT '首次服务时间',
  `last_service_time` datetime DEFAULT NULL COMMENT '最近服务时间',
  `total_appointments` int(11) DEFAULT 0 COMMENT '累计预约数',
  `total_orders` int(11) DEFAULT 0 COMMENT '累计订单数',
  `total_spent` decimal(12,2) DEFAULT 0.00 COMMENT '累计消费金额',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_phone` (`customer_phone`),
  KEY `idx_customer_name` (`customer_name`),
  KEY `idx_last_service_time` (`last_service_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户画像表';

CREATE TABLE `t_customer_tag` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `profile_id` bigint(20) NOT NULL COMMENT '客户画像ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_profile_id` (`profile_id`),
  UNIQUE KEY `uk_profile_tag` (`profile_id`, `tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签表';

INSERT INTO `t_sys_permission` VALUES
(9204000000000000001, '客户画像', '客户画像管理', NULL, 0, 0, NULL, 0, 'layui-icon layui-icon-user', 9, 0, 'admin', NOW(), NULL, NULL, NULL);

INSERT INTO `t_sys_permission` VALUES
(9204000000000000002, '客户画像', '客户画像列表', '/CustomerProfileController/view', 0, 9204000000000000001, 'system:customerProfile:view', 1, 'layui-icon layui-icon-list', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

INSERT INTO `t_sys_permission` VALUES
(9204000000000000003, '画像列表', '画像列表查询', '/CustomerProfileController/list', 0, 9204000000000000002, 'system:customerProfile:list', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9204000000000000004, '画像详情', '画像详情查看', '/CustomerProfileController/detail', 0, 9204000000000000002, 'system:customerProfile:detail', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9204000000000000005, '标签添加', '标签添加', '/CustomerProfileController/addTag', 0, 9204000000000000002, 'system:customerProfile:addTag', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL),
(9204000000000000006, '标签删除', '标签删除', '/CustomerProfileController/removeTag', 0, 9204000000000000002, 'system:customerProfile:removeTag', 2, '', 1, 0, 'admin', NOW(), NULL, NULL, NULL);

INSERT INTO `t_sys_permission_role` (`id`, `role_id`, `permission_id`) VALUES
(9204000000000000101, 488243256161730560, 9204000000000000001),
(9204000000000000102, 488243256161730560, 9204000000000000002),
(9204000000000000103, 488243256161730560, 9204000000000000003),
(9204000000000000104, 488243256161730560, 9204000000000000004),
(9204000000000000105, 488243256161730560, 9204000000000000005),
(9204000000000000106, 488243256161730560, 9204000000000000006);
