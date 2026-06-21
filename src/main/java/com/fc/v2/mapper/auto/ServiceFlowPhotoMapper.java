package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.ServiceFlowPhoto;

public interface ServiceFlowPhotoMapper extends BaseMapper<ServiceFlowPhoto> {

    public ServiceFlowPhoto selectServiceFlowPhotoById(Long id);

    public List<ServiceFlowPhoto> selectServiceFlowPhotoByFlowId(Long flowId);

    public int deleteServiceFlowPhotoByFlowId(Long flowId);
}
