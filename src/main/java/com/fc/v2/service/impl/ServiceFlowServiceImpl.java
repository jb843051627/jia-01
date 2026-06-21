package com.fc.v2.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.mapper.auto.ServiceFlowMapper;
import com.fc.v2.model.auto.ServiceFlow;
import com.fc.v2.model.auto.ServiceFlowPhoto;
import com.fc.v2.service.ITServiceFlowPhotoService;
import com.fc.v2.service.ITServiceFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceFlowServiceImpl extends ServiceImpl<ServiceFlowMapper, ServiceFlow> implements ITServiceFlowService {

    @Autowired
    private ServiceFlowMapper serviceFlowMapper;

    @Autowired
    private ITServiceFlowPhotoService serviceFlowPhotoService;

    private static final String[][] FLOW_NODES = {
            {"reception", "接待"},
            {"farewell", "告别"},
            {"cremation", "火化"},
            {"ashes", "骨灰安置"}
    };

    private static final String STATUS_PENDING = "0";
    private static final String STATUS_ACTIVE = "1";
    private static final String STATUS_COMPLETED = "2";

    @Override
    public List<ServiceFlow> selectServiceFlowByAppointmentId(Long appointmentId) {
        List<ServiceFlow> list = serviceFlowMapper.selectServiceFlowByAppointmentId(appointmentId);
        for (ServiceFlow item : list) {
            item.setStatusName(getStatusName(item.getStatus()));
            List<ServiceFlowPhoto> photos = serviceFlowPhotoService.selectServiceFlowPhotoByFlowId(item.getId());
            item.setPhotos(photos);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createFlowNodes(Long appointmentId) {
        List<ServiceFlow> existing = serviceFlowMapper.selectServiceFlowByAppointmentId(appointmentId);
        if (existing != null && !existing.isEmpty()) {
            return AjaxResult.success("流程节点已存在");
        }

        for (int i = 0; i < FLOW_NODES.length; i++) {
            ServiceFlow node = new ServiceFlow();
            node.setAppointmentId(appointmentId);
            node.setNodeCode(FLOW_NODES[i][0]);
            node.setNodeName(FLOW_NODES[i][1]);
            node.setSortOrder(i + 1);
            if (i == 0) {
                node.setStatus(STATUS_ACTIVE);
            } else {
                node.setStatus(STATUS_PENDING);
            }
            this.baseMapper.insert(node);
        }

        return AjaxResult.success("流程节点创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult completeNode(Long id, String remark) {
        ServiceFlow node = this.baseMapper.selectById(id);
        if (node == null) {
            return AjaxResult.error("节点不存在");
        }
        if (STATUS_COMPLETED.equals(node.getStatus())) {
            return AjaxResult.error("节点已完成");
        }
        if (STATUS_PENDING.equals(node.getStatus())) {
            return AjaxResult.error("当前节点未激活，无法完成");
        }

        node.setStatus(STATUS_COMPLETED);
        node.setCompleteTime(new Date());
        if (remark != null && !remark.isEmpty()) {
            node.setRemark(remark);
        }
        this.baseMapper.updateById(node);

        List<ServiceFlow> allNodes = serviceFlowMapper.selectServiceFlowByAppointmentId(node.getAppointmentId());
        ServiceFlow nextNode = null;
        for (ServiceFlow n : allNodes) {
            if (n.getSortOrder() == node.getSortOrder() + 1) {
                nextNode = n;
                break;
            }
        }

        if (nextNode != null && STATUS_PENDING.equals(nextNode.getStatus())) {
            nextNode.setStatus(STATUS_ACTIVE);
            this.baseMapper.updateById(nextNode);
        }

        return AjaxResult.success("节点已完成");
    }

    @Override
    public AjaxResult updateNodeRemark(Long id, String remark) {
        ServiceFlow node = this.baseMapper.selectById(id);
        if (node == null) {
            return AjaxResult.error("节点不存在");
        }
        node.setRemark(remark);
        this.baseMapper.updateById(node);
        return AjaxResult.success("备注更新成功");
    }

    @Override
    public AjaxResult uploadNodePhoto(Long id, String photoUrl, String photoName) {
        ServiceFlow node = this.baseMapper.selectById(id);
        if (node == null) {
            return AjaxResult.error("节点不存在");
        }

        List<ServiceFlowPhoto> existingPhotos = serviceFlowPhotoService.selectServiceFlowPhotoByFlowId(id);
        int nextSort = 0;
        if (existingPhotos != null && !existingPhotos.isEmpty()) {
            nextSort = existingPhotos.size();
        }

        ServiceFlowPhoto photo = new ServiceFlowPhoto();
        photo.setFlowId(id);
        photo.setPhotoUrl(photoUrl);
        photo.setPhotoName(photoName);
        photo.setSort(nextSort);
        serviceFlowPhotoService.insertServiceFlowPhoto(photo);

        return AjaxResult.success("照片上传成功");
    }

    @Override
    public ServiceFlow selectActiveNodeByAppointmentId(Long appointmentId) {
        return serviceFlowMapper.selectActiveNodeByAppointmentId(appointmentId);
    }

    @Override
    public ServiceFlow selectServiceFlowById(Long id) {
        ServiceFlow node = this.baseMapper.selectById(id);
        if (node != null) {
            node.setStatusName(getStatusName(node.getStatus()));
            List<ServiceFlowPhoto> photos = serviceFlowPhotoService.selectServiceFlowPhotoByFlowId(id);
            node.setPhotos(photos);
        }
        return node;
    }

    private String getStatusName(String status) {
        if (STATUS_PENDING.equals(status)) {
            return "待处理";
        } else if (STATUS_ACTIVE.equals(status)) {
            return "进行中";
        } else if (STATUS_COMPLETED.equals(status)) {
            return "已完成";
        }
        return "未知";
    }
}
