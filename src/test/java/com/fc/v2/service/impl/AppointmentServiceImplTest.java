package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.mapper.auto.AppointmentMapper;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.AppointmentStatus;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.service.ITCustomerProfileService;
import com.fc.v2.service.ITServiceFlowService;
import com.fc.v2.service.ITServiceTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("预约 Service 层单元测试")
class AppointmentServiceImplTest {

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private ITServiceTypeService serviceTypeService;

    @Mock
    private ITServiceFlowService serviceFlowService;

    @Mock
    private ITCustomerProfileService customerProfileService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment buildValidAppointment() {
        Appointment a = new Appointment();
        a.setId(1L);
        a.setPetId(100L);
        a.setServiceTypeId(10L);
        a.setCustomerName("张三");
        a.setCustomerPhone("13800138000");
        a.setAppointmentTime(new Date());
        return a;
    }

    private ServiceType buildValidServiceType() {
        ServiceType st = new ServiceType();
        st.setId(10L);
        st.setName("洗澡");
        st.setPrice(new BigDecimal("128"));
        st.setDuration(90);
        return st;
    }

    @Nested
    @DisplayName("创建预约")
    class InsertAppointment {

        @Test
        @DisplayName("正常创建预约")
        void insertAppointment_success() {
            Appointment appointment = buildValidAppointment();
            ServiceType serviceType = buildValidServiceType();

            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(serviceType);
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(null))).thenReturn(Collections.emptyList());
            when(appointmentMapper.insert(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(200, result.get("code"));
            assertEquals("预约成功", result.get("msg"));
            assertEquals("0", appointment.getStatus());
            assertNotNull(appointment.getAppointmentNo());
            assertNotNull(appointment.getEndTime());
            assertEquals(new BigDecimal("128"), appointment.getAmount());

            verify(customerProfileService).refreshOrCreateProfile("张三", "13800138000");
        }

        @Test
        @DisplayName("预约时间为空返回错误")
        void insertAppointment_nullTime() {
            Appointment appointment = buildValidAppointment();
            appointment.setAppointmentTime(null);

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("预约时间不能为空", result.get("msg"));
        }

        @Test
        @DisplayName("未选择服务类型返回错误")
        void insertAppointment_nullServiceType() {
            Appointment appointment = buildValidAppointment();
            appointment.setServiceTypeId(null);

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("请选择服务类型", result.get("msg"));
        }

        @Test
        @DisplayName("服务类型不存在返回错误")
        void insertAppointment_serviceTypeNotFound() {
            Appointment appointment = buildValidAppointment();
            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(null);

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("服务类型不存在", result.get("msg"));
        }

        @Test
        @DisplayName("时间冲突返回错误")
        void insertAppointment_timeConflict() {
            Appointment appointment = buildValidAppointment();
            ServiceType serviceType = buildValidServiceType();

            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(serviceType);
            Appointment conflict = new Appointment();
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(null)))
                    .thenReturn(Arrays.asList(conflict));

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("该时间段已有预约，请选择其他时间", result.get("msg"));
        }

        @Test
        @DisplayName("数据库插入失败返回错误")
        void insertAppointment_insertFail() {
            Appointment appointment = buildValidAppointment();
            ServiceType serviceType = buildValidServiceType();

            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(serviceType);
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(null))).thenReturn(Collections.emptyList());
            when(appointmentMapper.insert(any(Appointment.class))).thenReturn(0);

            AjaxResult result = appointmentService.insertAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("预约失败", result.get("msg"));
        }
    }

    @Nested
    @DisplayName("状态流转")
    class UpdateStatus {

        @Test
        @DisplayName("合法流转：待确认 → 已确认")
        void updateStatus_pendingToConfirmed() {
            Appointment current = buildValidAppointment();
            current.setStatus("0");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateStatus(1L, "1");

            assertEquals(200, result.get("code"));
            assertEquals("状态更新成功", result.get("msg"));
            verify(customerProfileService).refreshOrCreateProfile("张三", "13800138000");
        }

        @Test
        @DisplayName("合法流转：待确认 → 已取消")
        void updateStatus_pendingToCancelled() {
            Appointment current = buildValidAppointment();
            current.setStatus("0");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateStatus(1L, "4");

            assertEquals(200, result.get("code"));
        }

        @Test
        @DisplayName("合法流转：已确认 → 服务中")
        void updateStatus_confirmedToServing() {
            Appointment current = buildValidAppointment();
            current.setStatus("1");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateStatus(1L, "2");

            assertEquals(200, result.get("code"));
            verify(serviceFlowService).createFlowNodes(1L);
        }

        @Test
        @DisplayName("合法流转：已确认 → 已取消")
        void updateStatus_confirmedToCancelled() {
            Appointment current = buildValidAppointment();
            current.setStatus("1");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateStatus(1L, "4");

            assertEquals(200, result.get("code"));
        }

        @Test
        @DisplayName("合法流转：服务中 → 已完成")
        void updateStatus_servingToCompleted() {
            Appointment current = buildValidAppointment();
            current.setStatus("2");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateStatus(1L, "3");

            assertEquals(200, result.get("code"));
        }

        @Test
        @DisplayName("非法跳转：待确认 → 已完成")
        void updateStatus_pendingToCompleted_illegal() {
            Appointment current = buildValidAppointment();
            current.setStatus("0");

            when(appointmentMapper.selectById(1L)).thenReturn(current);

            AjaxResult result = appointmentService.updateStatus(1L, "3");

            assertEquals(500, result.get("code"));
            assertTrue(result.get("msg").toString().contains("非法状态流转"));
        }

        @Test
        @DisplayName("非法跳转：待确认 → 服务中")
        void updateStatus_pendingToServing_illegal() {
            Appointment current = buildValidAppointment();
            current.setStatus("0");

            when(appointmentMapper.selectById(1L)).thenReturn(current);

            AjaxResult result = appointmentService.updateStatus(1L, "2");

            assertEquals(500, result.get("code"));
            assertTrue(result.get("msg").toString().contains("非法状态流转"));
        }

        @Test
        @DisplayName("非法跳转：已完成 → 服务中")
        void updateStatus_completedToServing_illegal() {
            Appointment current = buildValidAppointment();
            current.setStatus("3");

            when(appointmentMapper.selectById(1L)).thenReturn(current);

            AjaxResult result = appointmentService.updateStatus(1L, "2");

            assertEquals(500, result.get("code"));
            assertTrue(result.get("msg").toString().contains("非法状态流转"));
        }

        @Test
        @DisplayName("非法跳转：已取消 → 已确认")
        void updateStatus_cancelledToConfirmed_illegal() {
            Appointment current = buildValidAppointment();
            current.setStatus("4");

            when(appointmentMapper.selectById(1L)).thenReturn(current);

            AjaxResult result = appointmentService.updateStatus(1L, "1");

            assertEquals(500, result.get("code"));
            assertTrue(result.get("msg").toString().contains("非法状态流转"));
        }

        @Test
        @DisplayName("预约不存在")
        void updateStatus_notFound() {
            when(appointmentMapper.selectById(999L)).thenReturn(null);

            AjaxResult result = appointmentService.updateStatus(999L, "1");

            assertEquals(500, result.get("code"));
            assertEquals("预约不存在", result.get("msg"));
        }

        @Test
        @DisplayName("数据库更新失败")
        void updateStatus_dbFail() {
            Appointment current = buildValidAppointment();
            current.setStatus("0");

            when(appointmentMapper.selectById(1L)).thenReturn(current);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(0);

            AjaxResult result = appointmentService.updateStatus(1L, "1");

            assertEquals(500, result.get("code"));
            assertEquals("状态更新失败", result.get("msg"));
        }
    }

    @Nested
    @DisplayName("其他方法")
    class OtherMethods {

        @Test
        @DisplayName("根据ID查询预约")
        void selectAppointmentById() {
            Appointment appointment = buildValidAppointment();
            when(appointmentMapper.selectAppointmentById(1L)).thenReturn(appointment);

            Appointment result = appointmentService.selectAppointmentById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("查询预约详情-填充状态名")
        void selectAppointmentDetailById() {
            Appointment appointment = buildValidAppointment();
            appointment.setStatus("1");
            when(appointmentMapper.selectAppointmentDetailById(1L)).thenReturn(appointment);

            Appointment result = appointmentService.selectAppointmentDetailById(1L);

            assertNotNull(result);
            assertEquals("已确认", result.getStatusName());
        }

        @Test
        @DisplayName("查询预约详情-不存在返回null")
        void selectAppointmentDetailById_null() {
            when(appointmentMapper.selectAppointmentDetailById(999L)).thenReturn(null);

            Appointment result = appointmentService.selectAppointmentDetailById(999L);

            assertNull(result);
        }

        @Test
        @DisplayName("列表查询-填充状态名")
        void selectAppointmentList_withStatusName() {
            Appointment a1 = new Appointment();
            a1.setStatus("0");
            Appointment a2 = new Appointment();
            a2.setStatus("3");

            when(appointmentMapper.selectAppointmentList(any(Appointment.class), any(), any()))
                    .thenReturn(Arrays.asList(a1, a2));

            List<Appointment> result = appointmentService.selectAppointmentList(new Appointment(), null, null);

            assertEquals(2, result.size());
            assertEquals("待确认", result.get(0).getStatusName());
            assertEquals("已完成", result.get(1).getStatusName());
        }

        @Test
        @DisplayName("修改预约-正常")
        void updateAppointment_success() {
            Appointment appointment = buildValidAppointment();
            appointment.setId(1L);
            ServiceType serviceType = buildValidServiceType();

            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(serviceType);
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(1L))).thenReturn(Collections.emptyList());
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            AjaxResult result = appointmentService.updateAppointment(appointment);

            assertEquals(200, result.get("code"));
            assertEquals("修改成功", result.get("msg"));
        }

        @Test
        @DisplayName("修改预约-时间冲突")
        void updateAppointment_conflict() {
            Appointment appointment = buildValidAppointment();
            appointment.setId(1L);
            ServiceType serviceType = buildValidServiceType();

            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(serviceType);
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(1L)))
                    .thenReturn(Arrays.asList(new Appointment()));

            AjaxResult result = appointmentService.updateAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("该时间段已有预约，请选择其他时间", result.get("msg"));
        }

        @Test
        @DisplayName("删除预约-单个")
        void deleteAppointmentById() {
            Appointment appointment = buildValidAppointment();
            when(appointmentMapper.selectById(1L)).thenReturn(appointment);
            when(appointmentMapper.deleteById(1L)).thenReturn(1);

            int result = appointmentService.deleteAppointmentById(1L);

            assertEquals(1, result);
            verify(customerProfileService).refreshOrCreateProfile("张三", "13800138000");
        }

        @Test
        @DisplayName("删除预约-批量")
        void deleteAppointmentByIds() {
            Appointment a1 = buildValidAppointment();
            a1.setId(1L);
            Appointment a2 = buildValidAppointment();
            a2.setId(2L);
            a2.setCustomerName("李四");
            a2.setCustomerPhone("13900139000");

            when(appointmentMapper.selectById(1L)).thenReturn(a1);
            when(appointmentMapper.selectById(2L)).thenReturn(a2);
            when(appointmentMapper.deleteBatchIds(anyList())).thenReturn(2);

            int result = appointmentService.deleteAppointmentByIds("1,2");

            assertEquals(2, result);
            verify(customerProfileService).refreshOrCreateProfile("张三", "13800138000");
            verify(customerProfileService).refreshOrCreateProfile("李四", "13900139000");
        }

        @Test
        @DisplayName("生成预约单号格式正确")
        void generateAppointmentNo() {
            String no = appointmentService.generateAppointmentNo();
            assertTrue(no.startsWith("AP"));
            assertTrue(no.length() > 10);
        }

        @Test
        @DisplayName("检查时间冲突")
        void checkTimeConflict() {
            when(appointmentMapper.checkTimeConflict(any(Date.class), any(Date.class), eq(null)))
                    .thenReturn(Collections.emptyList());

            List<Appointment> result = appointmentService.checkTimeConflict(new Date(), new Date(), null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("根据宠物ID查询预约")
        void selectAppointmentByPetId() {
            when(appointmentMapper.selectAppointmentByPetId(100L)).thenReturn(Arrays.asList(buildValidAppointment()));

            List<Appointment> result = appointmentService.selectAppointmentByPetId(100L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("修改预约-预约时间为空")
        void updateAppointment_nullTime() {
            Appointment appointment = buildValidAppointment();
            appointment.setAppointmentTime(null);

            AjaxResult result = appointmentService.updateAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("预约时间不能为空", result.get("msg"));
        }

        @Test
        @DisplayName("修改预约-服务类型为空")
        void updateAppointment_nullServiceType() {
            Appointment appointment = buildValidAppointment();
            appointment.setServiceTypeId(null);

            AjaxResult result = appointmentService.updateAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("请选择服务类型", result.get("msg"));
        }

        @Test
        @DisplayName("修改预约-服务类型不存在")
        void updateAppointment_serviceTypeNotFound() {
            Appointment appointment = buildValidAppointment();
            when(serviceTypeService.selectServiceTypeById(10L)).thenReturn(null);

            AjaxResult result = appointmentService.updateAppointment(appointment);

            assertEquals(500, result.get("code"));
            assertEquals("服务类型不存在", result.get("msg"));
        }
    }
}
