package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.model.custom.ServiceTypeStatsVO;
import org.apache.ibatis.annotations.Param;

public interface ServiceTypeMapper extends BaseMapper<ServiceType> {

    public ServiceType selectServiceTypeById(Long id);

    public List<ServiceType> selectServiceTypeList(ServiceType serviceType);

    public List<ServiceType> selectEnabledServiceTypeList();

    public int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    public int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    public List<ServiceTypeStatsVO> countOrdersByServiceType();
}
