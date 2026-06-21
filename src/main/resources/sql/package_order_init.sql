-- 添加服务类型库存字段
ALTER TABLE t_service_type ADD COLUMN stock INT DEFAULT 0 COMMENT '库存数量' AFTER duration;

-- 套餐表
CREATE TABLE IF NOT EXISTS t_package (
    id BIGINT NOT NULL COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    original_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价合计',
    package_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '套餐价格',
    description VARCHAR(500) DEFAULT NULL COMMENT '套餐描述',
    status VARCHAR(1) DEFAULT '0' COMMENT '状态 0=启用,1=禁用',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐表';

-- 套餐商品关联表
CREATE TABLE IF NOT EXISTS t_package_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    service_type_id BIGINT NOT NULL COMMENT '服务类型ID(商品ID)',
    quantity INT DEFAULT 1 COMMENT '数量',
    PRIMARY KEY (id),
    KEY idx_package_id (package_id),
    KEY idx_service_type_id (service_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐商品关联表';

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    appointment_id BIGINT DEFAULT NULL COMMENT '预约ID',
    customer_name VARCHAR(50) DEFAULT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(20) DEFAULT NULL COMMENT '客户电话',
    pet_id BIGINT DEFAULT NULL COMMENT '宠物ID',
    package_id BIGINT DEFAULT NULL COMMENT '套餐ID',
    original_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价金额',
    order_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '订单金额',
    status VARCHAR(1) DEFAULT '0' COMMENT '订单状态 0=待付款,1=已付款,2=服务中,3=已完成,4=已取消',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    service_start_time DATETIME DEFAULT NULL COMMENT '服务开始时间',
    service_end_time DATETIME DEFAULT NULL COMMENT '服务结束时间',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_appointment_id (appointment_id),
    KEY idx_customer_phone (customer_phone),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品关联表
CREATE TABLE IF NOT EXISTS t_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    service_type_id BIGINT NOT NULL COMMENT '服务类型ID(商品ID)',
    service_name VARCHAR(100) DEFAULT NULL COMMENT '商品名称(冗余)',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    quantity INT DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10,2) DEFAULT 0.00 COMMENT '小计',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_service_type_id (service_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品关联表';
