package com.fc.v2.common.quartz.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.model.auto.TOrder;
import com.fc.v2.service.ITOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 示例定时任务类
 * 包含：每日统计报表、每小时检查超时未派单
 *
 * @author sample
 */
@Component("sampleTask")
public class SampleTask {

    private static final Logger logger = LoggerFactory.getLogger(SampleTask.class);

    @Autowired
    private ITOrderService orderService;

    /**
     * 每天凌晨统计报表
     * 统计前一天的订单数据：订单总数、完成订单数、总金额等
     */
    public void dailyStatisticsReport() {
        logger.info("============= 开始执行【每日统计报表】定时任务 =============");
        try {
            Date yesterday = DateUtil.yesterday();
            String dayStart = DateUtil.format(DateUtil.beginOfDay(yesterday), "yyyy-MM-dd HH:mm:ss");
            String dayEnd = DateUtil.format(DateUtil.endOfDay(yesterday), "yyyy-MM-dd HH:mm:ss");

            QueryWrapper<TOrder> orderQuery = new QueryWrapper<>();
            orderQuery.between("create_time", dayStart, dayEnd);
            List<TOrder> orderList = orderService.list(orderQuery);

            int totalOrders = orderList.size();
            int completedOrders = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (TOrder order : orderList) {
                if ("3".equals(order.getStatus())) {
                    completedOrders++;
                }
                if (order.getOrderAmount() != null) {
                    totalAmount = totalAmount.add(order.getOrderAmount());
                }
            }

            logger.info("报表日期: {}", DateUtil.format(yesterday, "yyyy-MM-dd"));
            logger.info("订单总数: {}", totalOrders);
            logger.info("完成订单数: {}", completedOrders);
            logger.info("订单总金额: {} 元", totalAmount);
            logger.info("============= 【每日统计报表】定时任务执行完成 =============");

        } catch (Exception e) {
            logger.error("【每日统计报表】定时任务执行异常", e);
            throw new RuntimeException("每日统计报表执行异常: " + e.getMessage(), e);
        }
    }

    /**
     * 每小时检查超时未派单
     * 检查创建时间超过2小时但仍未派单的订单，并记录提醒
     * 状态：0=待付款,1=已付款(待派单),2=服务中,3=已完成,4=已取消
     */
    public void checkTimeoutDispatch() {
        logger.info("============= 开始执行【检查超时未派单】定时任务 =============");
        try {
            Date twoHoursAgo = DateUtil.offsetHour(new Date(), -2);
            String twoHoursAgoStr = DateUtil.format(twoHoursAgo, "yyyy-MM-dd HH:mm:ss");

            QueryWrapper<TOrder> orderQuery = new QueryWrapper<>();
            orderQuery.lt("create_time", twoHoursAgoStr);
            orderQuery.in("status", "0", "1");
            List<TOrder> timeoutOrders = orderService.list(orderQuery);

            int timeoutCount = timeoutOrders.size();
            logger.info("当前检查时间: {}", DateUtil.now());
            logger.info("距当前时间2小时前: {}", twoHoursAgoStr);
            logger.info("超时未派单订单数量: {}", timeoutCount);

            if (timeoutCount > 0) {
                for (TOrder order : timeoutOrders) {
                    logger.warn("订单[{}]超时未派单，订单号:{}, 创建时间: {}, 当前状态: {}",
                            order.getId(),
                            order.getOrderNo(),
                            order.getCreateTime() != null ? DateUtil.format(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : "未知",
                            getOrderStatusText(order.getStatus()));
                }
            }

            logger.info("============= 【检查超时未派单】定时任务执行完成 =============");

        } catch (Exception e) {
            logger.error("【检查超时未派单】定时任务执行异常", e);
            throw new RuntimeException("检查超时未派单执行异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(String status) {
        if (status == null) return "未知";
        switch (status) {
            case "0": return "待付款";
            case "1": return "已付款(待派单)";
            case "2": return "服务中";
            case "3": return "已完成";
            case "4": return "已取消";
            default: return "未知状态(" + status + ")";
        }
    }
}
