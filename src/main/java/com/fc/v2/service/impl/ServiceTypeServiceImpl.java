package com.fc.v2.service.impl;

import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.ServiceTypeMapper;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.service.ITServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeServiceImpl extends ServiceImpl<ServiceTypeMapper, ServiceType> implements ITServiceTypeService {

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Override
    public ServiceType selectServiceTypeById(Long id) {
        return serviceTypeMapper.selectServiceTypeById(id);
    }

    @Override
    public List<ServiceType> selectServiceTypeList(Wrapper<ServiceType> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<ServiceType> selectServiceTypeList(ServiceType serviceType) {
        return serviceTypeMapper.selectServiceTypeList(serviceType);
    }

    @Override
    public int insertServiceType(ServiceType serviceType) {
        return this.baseMapper.insert(serviceType);
    }

    @Override
    public int updateServiceType(ServiceType serviceType) {
        return this.baseMapper.updateById(serviceType);
    }

    @Override
    public int deleteServiceTypeByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        return this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
    }

    @Override
    public int deleteServiceTypeById(Long id) {
        return this.baseMapper.deleteById(id);
    }

    @Override
    public List<ServiceType> selectEnabledServiceTypeList() {
        return serviceTypeMapper.selectEnabledServiceTypeList();
    }
}
