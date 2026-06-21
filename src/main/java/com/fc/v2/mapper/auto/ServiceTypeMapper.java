package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.ServiceType;

public interface ServiceTypeMapper extends BaseMapper<ServiceType> {

    public ServiceType selectServiceTypeById(Long id);

    public List<ServiceType> selectServiceTypeList(ServiceType serviceType);

    public List<ServiceType> selectEnabledServiceTypeList();
}
