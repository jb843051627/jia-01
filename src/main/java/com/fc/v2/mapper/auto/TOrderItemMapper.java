package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.TOrderItem;

public interface TOrderItemMapper extends BaseMapper<TOrderItem> {

    public List<TOrderItem> selectOrderItemsByOrderId(Long orderId);

    public int deleteOrderItemsByOrderId(Long orderId);
}
