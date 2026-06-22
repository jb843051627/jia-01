package com.fc.v2.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.AppointmentMapper;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.AppointmentStatus;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.service.ITAppointmentService;
import com.fc.v2.service.ITCustomerProfileService;
import com.fc.v2.service.ITServiceFlowService;
import com.fc.v2.service.ITServiceTypeService;
import com.fc.v2.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements ITAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ITServiceTypeService serviceTypeService;

    @Autowired
    private ITServiceFlowService serviceFlowService;

    @Autowired
    private ITCustomerProfileService customerProfileService;

    private static final Map<String, Set<String>> VALID_TRANSITIONS = new HashMap<>();
    static {
        VALID_TRANSITIONS.put("0", new HashSet<>(Arrays.asList("1", "4")));
        VALID_TRANSITIONS.put("1", new HashSet<>(Arrays.asList("2", "4")));
        VALID_TRANSITIONS.put("2", new HashSet<>(Arrays.asList("3")));
        VALID_TRANSITIONS.put("3", Collections.emptySet());
        VALID_TRANSITIONS.put("4", Collections.emptySet());
    }

    @Override
    public Appointment selectAppointmentById(Long id) {
        return appointmentMapper.selectAppointmentById(id);
    }

    @Override
    public List<Appointment> selectAppointmentList(Wrapper<Appointment> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Appointment> selectAppointmentList(Appointment appointment, Date startDate, Date endDate) {
        List<Appointment> list = appointmentMapper.selectAppointmentList(appointment, startDate, endDate);
        for (Appointment item : list) {
            item.setStatusName(AppointmentStatus.getNameByCode(item.getStatus()));
        }
        return list;
    }

    @Override
    public AjaxResult insertAppointment(Appointment appointment) {
        if (appointment.getAppointmentTime() == null) {
            return AjaxResult.error("预约时间不能为空");
        }
        if (appointment.getServiceTypeId() == null) {
            return AjaxResult.error("请选择服务类型");
        }

        ServiceType serviceType = serviceTypeService.selectServiceTypeById(appointment.getServiceTypeId());
        if (serviceType == null) {
            return AjaxResult.error("服务类型不存在");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointment.getAppointmentTime());
        calendar.add(Calendar.MINUTE, serviceType.getDuration() != null ? serviceType.getDuration() : 120);
        appointment.setEndTime(calendar.getTime());
        appointment.setAmount(serviceType.getPrice());

        List<Appointment> conflicts = checkTimeConflict(appointment.getAppointmentTime(), appointment.getEndTime(), null);
        if (!conflicts.isEmpty()) {
            return AjaxResult.error("该时间段已有预约，请选择其他时间");
        }

        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setStatus("0");

        int result = this.baseMapper.insert(appointment);
        if (result > 0) {
            customerProfileService.refreshOrCreateProfile(appointment.getCustomerName(), appointment.getCustomerPhone());
            return AjaxResult.success("预约成功");
        }
        return AjaxResult.error("预约失败");
    }

    @Override
    public AjaxResult updateAppointment(Appointment appointment) {
        if (appointment.getAppointmentTime() == null) {
            return AjaxResult.error("预约时间不能为空");
        }
        if (appointment.getServiceTypeId() == null) {
            return AjaxResult.error("请选择服务类型");
        }

        ServiceType serviceType = serviceTypeService.selectServiceTypeById(appointment.getServiceTypeId());
        if (serviceType == null) {
            return AjaxResult.error("服务类型不存在");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointment.getAppointmentTime());
        calendar.add(Calendar.MINUTE, serviceType.getDuration() != null ? serviceType.getDuration() : 120);
        appointment.setEndTime(calendar.getTime());
        appointment.setAmount(serviceType.getPrice());

        List<Appointment> conflicts = checkTimeConflict(appointment.getAppointmentTime(), appointment.getEndTime(), appointment.getId());
        if (!conflicts.isEmpty()) {
            return AjaxResult.error("该时间段已有预约，请选择其他时间");
        }

        int result = this.baseMapper.updateById(appointment);
        if (result > 0) {
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointmentByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        List<String[]> customerInfoList = new ArrayList<>();
        for (String id : idsArray) {
            Appointment appointment = this.baseMapper.selectById(Long.parseLong(id));
            if (appointment != null) {
                customerInfoList.add(new String[]{appointment.getCustomerName(), appointment.getCustomerPhone()});
            }
        }
        int result = this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
        for (String[] info : customerInfoList) {
            customerProfileService.refreshOrCreateProfile(info[0], info[1]);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointmentById(Long id) {
        Appointment appointment = this.baseMapper.selectById(id);
        int result = this.baseMapper.deleteById(id);
        if (appointment != null) {
            customerProfileService.refreshOrCreateProfile(appointment.getCustomerName(), appointment.getCustomerPhone());
        }
        return result;
    }

    @Override
    public Appointment selectAppointmentDetailById(Long id) {
        Appointment appointment = appointmentMapper.selectAppointmentDetailById(id);
        if (appointment != null) {
            appointment.setStatusName(AppointmentStatus.getNameByCode(appointment.getStatus()));
        }
        return appointment;
    }

    @Override
    public List<Appointment> checkTimeConflict(Date startTime, Date endTime, Long excludeId) {
        return appointmentMapper.checkTimeConflict(startTime, endTime, excludeId);
    }

    @Override
    public List<Appointment> selectAppointmentByPetId(Long petId) {
        return appointmentMapper.selectAppointmentByPetId(petId);
    }

    @Override
    public String generateAppointmentNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String idStr = String.valueOf(IdWorker.getId()).substring(6);
        return "AP" + dateStr + idStr;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStatus(Long id, String status) {
        Appointment current = this.baseMapper.selectById(id);
        if (current == null) {
            return AjaxResult.error("预约不存在");
        }
        Set<String> allowed = VALID_TRANSITIONS.get(current.getStatus());
        if (allowed == null || !allowed.contains(status)) {
            return AjaxResult.error("非法状态流转：不能从" + AppointmentStatus.getNameByCode(current.getStatus()) + "变更为" + AppointmentStatus.getNameByCode(status));
        }

        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus(status);

        if (AppointmentStatus.SERVING.getCode().equals(status)) {
            appointment.setActualStartTime(new Date());
        } else if (AppointmentStatus.COMPLETED.getCode().equals(status)) {
            appointment.setActualEndTime(new Date());
        }

        int result = this.baseMapper.updateById(appointment);
        if (result > 0) {
            if (AppointmentStatus.SERVING.getCode().equals(status)) {
                serviceFlowService.createFlowNodes(id);
            }
            Appointment fullAppointment = this.baseMapper.selectById(id);
            if (fullAppointment != null) {
                customerProfileService.refreshOrCreateProfile(fullAppointment.getCustomerName(), fullAppointment.getCustomerPhone());
            }
            return AjaxResult.success("状态更新成功");
        }
        return AjaxResult.error("状态更新失败");
    }
}
