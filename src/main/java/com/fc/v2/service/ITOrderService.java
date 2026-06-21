package com.fc.v2.service;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.auto.TOrder;

public interface ITOrderService extends IService<TOrder> {

    public TOrder selectOrderById(Long id);

    public TOrder selectOrderDetailById(Long id);

    public List<TOrder> selectOrderList(Wrapper<TOrder> queryWrapper);

    public List<TOrder> selectOrderList(TOrder tOrder, Date startDate, Date endDate);

    public AjaxResult createOrder(TOrder tOrder);

    public AjaxResult updateOrder(TOrder tOrder);

    public int deleteOrderByIds(String ids);

    public int deleteOrderById(Long id);

    public String generateOrderNo();

    public AjaxResult updateStatus(Long id, String status);
}
