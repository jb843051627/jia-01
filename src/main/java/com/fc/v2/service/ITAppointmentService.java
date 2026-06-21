package com.fc.v2.service;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.common.domain.AjaxResult;

public interface ITAppointmentService extends IService<Appointment> {

    public Appointment selectAppointmentById(Long id);

    public List<Appointment> selectAppointmentList(Wrapper<Appointment> queryWrapper);

    public List<Appointment> selectAppointmentList(Appointment appointment);

    public AjaxResult insertAppointment(Appointment appointment);

    public AjaxResult updateAppointment(Appointment appointment);

    public int deleteAppointmentByIds(String ids);

    public int deleteAppointmentById(Long id);

    public Appointment selectAppointmentDetailById(Long id);

    public List<Appointment> checkTimeConflict(Date startTime, Date endTime, Long excludeId);

    public List<Appointment> selectAppointmentByPetId(Long petId);

    public String generateAppointmentNo();

    public AjaxResult updateStatus(Long id, String status);
}
