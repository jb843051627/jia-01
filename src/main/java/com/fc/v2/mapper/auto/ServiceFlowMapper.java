package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.ServiceFlow;

public interface ServiceFlowMapper extends BaseMapper<ServiceFlow> {

    public List<ServiceFlow> selectServiceFlowByAppointmentId(Long appointmentId);

    public ServiceFlow selectActiveNodeByAppointmentId(Long appointmentId);
}
