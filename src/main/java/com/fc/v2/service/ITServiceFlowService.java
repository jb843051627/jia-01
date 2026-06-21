package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.auto.ServiceFlow;

public interface ITServiceFlowService extends IService<ServiceFlow> {

    public List<ServiceFlow> selectServiceFlowByAppointmentId(Long appointmentId);

    public AjaxResult createFlowNodes(Long appointmentId);

    public AjaxResult completeNode(Long id, String remark);

    public AjaxResult updateNodeRemark(Long id, String remark);

    public AjaxResult uploadNodePhoto(Long id, String photoUrl, String photoName);

    public ServiceFlow selectActiveNodeByAppointmentId(Long appointmentId);

    public ServiceFlow selectServiceFlowById(Long id);
}
