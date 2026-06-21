package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.ServiceTypeMapper;
import com.fc.v2.mapper.auto.TOrderItemMapper;
import com.fc.v2.mapper.auto.TOrderMapper;
import com.fc.v2.mapper.auto.TPackageItemMapper;
import com.fc.v2.mapper.auto.TPackageMapper;
import com.fc.v2.model.auto.OrderStatus;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.model.auto.TOrder;
import com.fc.v2.model.auto.TOrderItem;
import com.fc.v2.model.auto.TPackage;
import com.fc.v2.model.auto.TPackageItem;
import com.fc.v2.service.ITOrderService;
import com.fc.v2.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {

    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private TOrderItemMapper tOrderItemMapper;

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Autowired
    private TPackageItemMapper tPackageItemMapper;

    @Autowired
    private TPackageMapper tPackageMapper;

    @Override
    public TOrder selectOrderById(Long id) {
        return tOrderMapper.selectOrderById(id);
    }

    @Override
    public TOrder selectOrderDetailById(Long id) {
        TOrder tOrder = tOrderMapper.selectOrderDetailById(id);
        if (tOrder != null) {
            tOrder.setItems(tOrderItemMapper.selectOrderItemsByOrderId(id));
            tOrder.setStatusName(OrderStatus.getNameByCode(tOrder.getStatus()));
        }
        return tOrder;
    }

    @Override
    public List<TOrder> selectOrderList(Wrapper<TOrder> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<TOrder> selectOrderList(TOrder tOrder, Date startDate, Date endDate) {
        List<TOrder> list = tOrderMapper.selectOrderList(tOrder, startDate, endDate);
        for (TOrder item : list) {
            item.setStatusName(OrderStatus.getNameByCode(item.getStatus()));
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createOrder(TOrder tOrder) {
        if (tOrder.getCustomerName() == null || tOrder.getCustomerName().trim().isEmpty()) {
            return AjaxResult.error("客户姓名不能为空");
        }
        if (tOrder.getCustomerPhone() == null || tOrder.getCustomerPhone().trim().isEmpty()) {
            return AjaxResult.error("客户电话不能为空");
        }

        List<TOrderItem> items = tOrder.getItems();
        Map<Long, Integer> stockMap = new HashMap<>();
        BigDecimal originalAmount = BigDecimal.ZERO;
        BigDecimal orderAmount = BigDecimal.ZERO;

        if (tOrder.getPackageId() != null) {
            TPackage tPackage = tPackageMapper.selectPackageById(tOrder.getPackageId());
            if (tPackage == null) {
                return AjaxResult.error("套餐不存在");
            }
            List<TPackageItem> packageItems = tPackageItemMapper.selectPackageItemsByPackageId(tOrder.getPackageId());
            if (packageItems == null || packageItems.isEmpty()) {
                return AjaxResult.error("套餐没有商品");
            }
            for (TPackageItem pkgItem : packageItems) {
                int qty = pkgItem.getQuantity();
                stockMap.merge(pkgItem.getServiceTypeId(), qty, Integer::sum);
                ServiceType st = serviceTypeMapper.selectServiceTypeById(pkgItem.getServiceTypeId());
                if (st == null) {
                    return AjaxResult.error("商品不存在");
                }
                if (st.getName() != null) {
                    pkgItem.setServiceName(st.getName());
                }
                if (st.getPrice() != null) {
                    pkgItem.setServicePrice(st.getPrice());
                }
                if (st.getPrice() != null) {
                    originalAmount = originalAmount.add(st.getPrice().multiply(new BigDecimal(qty)));
                }
            }
            orderAmount = tPackage.getPackagePrice() != null ? tPackage.getPackagePrice() : originalAmount;
        } else if (items != null && !items.isEmpty()) {
            for (TOrderItem item : items) {
                if (item.getServiceTypeId() == null) {
                    return AjaxResult.error("商品ID不能为空");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    return AjaxResult.error("商品数量必须大于0");
                }
                stockMap.merge(item.getServiceTypeId(), item.getQuantity(), Integer::sum);
            }
            for (TOrderItem item : items) {
                ServiceType st = serviceTypeMapper.selectServiceTypeById(item.getServiceTypeId());
                if (st == null) {
                    return AjaxResult.error("商品不存在");
                }
                item.setServiceName(st.getName());
                item.setPrice(st.getPrice());
                BigDecimal subtotal = st.getPrice() != null ? st.getPrice().multiply(new BigDecimal(item.getQuantity())) : BigDecimal.ZERO;
                item.setSubtotal(subtotal);
                originalAmount = originalAmount.add(subtotal);
            }
            orderAmount = originalAmount;
        } else {
            return AjaxResult.error("请选择套餐或商品");
        }

        if (tOrder.getOrderAmount() != null) {
            orderAmount = tOrder.getOrderAmount();
        } else if (orderAmount.compareTo(BigDecimal.ZERO) == 0) {
            orderAmount = originalAmount;
        }

        tOrder.setOriginalAmount(originalAmount);
        tOrder.setOrderAmount(orderAmount);
        tOrder.setOrderNo(generateOrderNo());
        tOrder.setStatus(OrderStatus.PENDING_PAYMENT.getCode());

        this.baseMapper.insert(tOrder);

        if (tOrder.getPackageId() != null) {
            List<TPackageItem> packageItems = tPackageItemMapper.selectPackageItemsByPackageId(tOrder.getPackageId());
            for (TPackageItem pkgItem : packageItems) {
                TOrderItem orderItem = new TOrderItem();
                orderItem.setId(IdWorker.getId());
                orderItem.setOrderId(tOrder.getId());
                orderItem.setServiceTypeId(pkgItem.getServiceTypeId());
                orderItem.setServiceName(pkgItem.getServiceName());
                orderItem.setPrice(pkgItem.getServicePrice());
                orderItem.setQuantity(pkgItem.getQuantity());
                if (pkgItem.getServicePrice() != null) {
                    orderItem.setSubtotal(pkgItem.getServicePrice().multiply(new BigDecimal(pkgItem.getQuantity())));
                }
                tOrderItemMapper.insert(orderItem);
            }
        } else if (items != null) {
            for (TOrderItem item : items) {
                item.setId(IdWorker.getId());
                item.setOrderId(tOrder.getId());
                tOrderItemMapper.insert(item);
            }
        }

        for (Map.Entry<Long, Integer> entry : stockMap.entrySet()) {
            int affected = serviceTypeMapper.decreaseStock(entry.getKey(), entry.getValue());
            if (affected == 0) {
                ServiceType st = serviceTypeMapper.selectServiceTypeById(entry.getKey());
                throw new RuntimeException("商品[" + (st != null ? st.getName() : "未知") + "]库存不足");
            }
        }

        return AjaxResult.success("下单成功");
    }

    @Override
    public AjaxResult updateOrder(TOrder tOrder) {
        if (tOrder.getId() == null) {
            return AjaxResult.error("订单ID不能为空");
        }
        int result = this.baseMapper.updateById(tOrder);
        if (result > 0) {
            return AjaxResult.success("修改订单成功");
        }
        return AjaxResult.error("修改订单失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrderByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        for (String id : idsArray) {
            Long orderId = Long.parseLong(id);
            restoreStock(orderId);
            tOrderItemMapper.deleteOrderItemsByOrderId(orderId);
        }
        return this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOrderById(Long id) {
        restoreStock(id);
        tOrderItemMapper.deleteOrderItemsByOrderId(id);
        return this.baseMapper.deleteById(id);
    }

    private void restoreStock(Long orderId) {
        List<TOrderItem> orderItems = tOrderItemMapper.selectOrderItemsByOrderId(orderId);
        if (orderItems != null && !orderItems.isEmpty()) {
            for (TOrderItem item : orderItems) {
                if (item.getServiceTypeId() != null && item.getQuantity() != null) {
                    serviceTypeMapper.increaseStock(item.getServiceTypeId(), item.getQuantity());
                }
            }
        }
    }

    @Override
    public String generateOrderNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String idStr = String.valueOf(IdWorker.getId()).substring(6);
        return "OD" + dateStr + idStr;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStatus(Long id, String status) {
        TOrder existingOrder = tOrderMapper.selectOrderById(id);
        if (existingOrder == null) {
            return AjaxResult.error("订单不存在");
        }

        OrderStatus currentStatus = OrderStatus.getByCode(existingOrder.getStatus());
        OrderStatus targetStatus = OrderStatus.getByCode(status);
        if (targetStatus == null) {
            return AjaxResult.error("目标状态无效");
        }

        String transitionError = validateStatusTransition(currentStatus, targetStatus);
        if (transitionError != null) {
            return AjaxResult.error(transitionError);
        }

        TOrder tOrder = new TOrder();
        tOrder.setId(id);
        tOrder.setStatus(status);

        if (OrderStatus.PAID.getCode().equals(status)) {
            tOrder.setPayTime(new Date());
        } else if (OrderStatus.SERVING.getCode().equals(status)) {
            tOrder.setServiceStartTime(new Date());
        } else if (OrderStatus.COMPLETED.getCode().equals(status)) {
            tOrder.setServiceEndTime(new Date());
        } else if (OrderStatus.CANCELLED.getCode().equals(status)) {
            if (currentStatus != OrderStatus.PENDING_PAYMENT
                    && currentStatus != OrderStatus.PAID
                    && currentStatus != OrderStatus.SERVING) {
                return AjaxResult.error("当前状态不允许取消订单");
            }
            restoreStock(id);
        }

        int result = this.baseMapper.updateById(tOrder);
        if (result > 0) {
            return AjaxResult.success("状态更新成功");
        }
        return AjaxResult.error("状态更新失败");
    }

    private String validateStatusTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        if (currentStatus == null) {
            return "当前订单状态无效";
        }
        if (currentStatus == targetStatus) {
            return null;
        }
        switch (currentStatus) {
            case PENDING_PAYMENT:
                if (targetStatus == OrderStatus.PAID || targetStatus == OrderStatus.CANCELLED) {
                    return null;
                }
                return "待付款状态只能改为已付款或已取消";
            case PAID:
                if (targetStatus == OrderStatus.SERVING || targetStatus == OrderStatus.CANCELLED) {
                    return null;
                }
                return "已付款状态只能改为服务中或已取消";
            case SERVING:
                if (targetStatus == OrderStatus.COMPLETED || targetStatus == OrderStatus.CANCELLED) {
                    return null;
                }
                return "服务中状态只能改为已完成或已取消";
            case COMPLETED:
            case CANCELLED:
                return "已完成或已取消的订单不能再修改状态";
            default:
                return "未知订单状态";
        }
    }
}
