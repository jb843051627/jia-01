package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.model.auto.ServiceType;

public interface ITServiceTypeService extends IService<ServiceType> {

    public ServiceType selectServiceTypeById(Long id);

    public List<ServiceType> selectServiceTypeList(Wrapper<ServiceType> queryWrapper);

    public List<ServiceType> selectServiceTypeList(ServiceType serviceType);

    public int insertServiceType(ServiceType serviceType);

    public int updateServiceType(ServiceType serviceType);

    public int deleteServiceTypeByIds(String ids);

    public int deleteServiceTypeById(Long id);

    public List<ServiceType> selectEnabledServiceTypeList();
}
