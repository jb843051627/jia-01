package com.fc.v2.mapper.auto;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.Appointment;

public interface AppointmentMapper extends BaseMapper<Appointment> {

    public Appointment selectAppointmentById(Long id);

    public List<Appointment> selectAppointmentList(Appointment appointment, Date startDate, Date endDate);

    public Appointment selectAppointmentDetailById(Long id);

    public List<Appointment> checkTimeConflict(Date startTime, Date endTime, Long excludeId);

    public List<Appointment> selectAppointmentByPetId(Long petId);
}
