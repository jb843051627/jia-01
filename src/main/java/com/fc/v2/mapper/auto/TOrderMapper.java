package com.fc.v2.mapper.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.TOrder;
import com.fc.v2.model.custom.DailyTrendVO;
import org.apache.ibatis.annotations.Param;

public interface TOrderMapper extends BaseMapper<TOrder> {

    public TOrder selectOrderById(Long id);

    public List<TOrder> selectOrderList(TOrder tOrder, Date startDate, Date endDate);

    public TOrder selectOrderDetailById(Long id);

    public Integer countOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public BigDecimal sumRevenueByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public List<DailyTrendVO> sumRevenueGroupByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
