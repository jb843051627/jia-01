package com.fc.v2.service.impl;

import java.io.File;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.conf.V2Config;
import com.fc.v2.mapper.auto.ServiceFlowPhotoMapper;
import com.fc.v2.model.auto.ServiceFlowPhoto;
import com.fc.v2.service.ITServiceFlowPhotoService;
import com.fc.v2.util.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceFlowPhotoServiceImpl extends ServiceImpl<ServiceFlowPhotoMapper, ServiceFlowPhoto> implements ITServiceFlowPhotoService {

    @Autowired
    private ServiceFlowPhotoMapper serviceFlowPhotoMapper;

    @Autowired
    private V2Config v2Config;

    @Override
    public ServiceFlowPhoto selectServiceFlowPhotoById(Long id) {
        return serviceFlowPhotoMapper.selectServiceFlowPhotoById(id);
    }

    @Override
    public List<ServiceFlowPhoto> selectServiceFlowPhotoByFlowId(Long flowId) {
        return serviceFlowPhotoMapper.selectServiceFlowPhotoByFlowId(flowId);
    }

    @Override
    public int insertServiceFlowPhoto(ServiceFlowPhoto photo) {
        return this.baseMapper.insert(photo);
    }

    @Override
    public int deleteServiceFlowPhotoById(Long id) {
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int deleteServiceFlowPhotoByFlowId(Long flowId) {
        return serviceFlowPhotoMapper.deleteServiceFlowPhotoByFlowId(flowId);
    }

    private void deletePhotoFile(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return;
        }
        try {
            String relativePath = photoUrl;
            if (relativePath.startsWith(FileUploadUtils.RESOURCE_PREFIX)) {
                relativePath = relativePath.substring(FileUploadUtils.RESOURCE_PREFIX.length());
            }
            String realPath = v2Config.getProfile() + relativePath;
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int deleteServiceFlowPhotoByIdWithFile(Long id) {
        ServiceFlowPhoto photo = selectServiceFlowPhotoById(id);
        if (photo != null) {
            deletePhotoFile(photo.getPhotoUrl());
        }
        return deleteServiceFlowPhotoById(id);
    }

    @Override
    public int deleteServiceFlowPhotoByFlowIdWithFile(Long flowId) {
        List<ServiceFlowPhoto> photos = selectServiceFlowPhotoByFlowId(flowId);
        if (photos != null) {
            for (ServiceFlowPhoto photo : photos) {
                deletePhotoFile(photo.getPhotoUrl());
            }
        }
        return deleteServiceFlowPhotoByFlowId(flowId);
    }
}
