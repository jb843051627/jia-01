package com.fc.v2.mapper.auto;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.TOrder;

public interface TOrderMapper extends BaseMapper<TOrder> {

    public TOrder selectOrderById(Long id);

    public List<TOrder> selectOrderList(TOrder tOrder, Date startDate, Date endDate);

    public TOrder selectOrderDetailById(Long id);
}
