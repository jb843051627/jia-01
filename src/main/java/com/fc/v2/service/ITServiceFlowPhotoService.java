package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.model.auto.ServiceFlowPhoto;

public interface ITServiceFlowPhotoService extends IService<ServiceFlowPhoto> {

    public ServiceFlowPhoto selectServiceFlowPhotoById(Long id);

    public List<ServiceFlowPhoto> selectServiceFlowPhotoByFlowId(Long flowId);

    public int insertServiceFlowPhoto(ServiceFlowPhoto photo);

    public int deleteServiceFlowPhotoById(Long id);

    public int deleteServiceFlowPhotoByFlowId(Long flowId);

    public int deleteServiceFlowPhotoByIdWithFile(Long id);

    public int deleteServiceFlowPhotoByFlowIdWithFile(Long flowId);
}
