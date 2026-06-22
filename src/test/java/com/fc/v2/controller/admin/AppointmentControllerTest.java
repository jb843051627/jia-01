package com.fc.v2.controller.admin;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.Pet;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.service.ITAppointmentService;
import com.fc.v2.service.ITPetService;
import com.fc.v2.service.ITServiceTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("预约 Controller 层单元测试")
class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ITAppointmentService appointmentService;

    @Mock
    private ITPetService petService;

    @Mock
    private ITServiceTypeService serviceTypeService;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .build();
    }

    private Appointment buildValidAppointment() {
        Appointment a = new Appointment();
        a.setId(1L);
        a.setPetId(100L);
        a.setServiceTypeId(10L);
        a.setCustomerName("张三");
        a.setCustomerPhone("13800138000");
        a.setAppointmentTime(new Date());
        a.setStatus("0");
        a.setAmount(new BigDecimal("128"));
        return a;
    }

    @Nested
    @DisplayName("新增预约接口")
    class AddAppointment {

        @Test
        @DisplayName("POST /AppointmentController/add - 创建成功")
        void add_success() throws Exception {
            AjaxResult success = AjaxResult.success("预约成功");
            when(appointmentService.insertAppointment(any(Appointment.class))).thenReturn(success);

            mockMvc.perform(post("/AppointmentController/add")
                            .param("petId", "100")
                            .param("serviceTypeId", "10")
                            .param("customerName", "张三")
                            .param("customerPhone", "13800138000")
                            .param("appointmentTime", "2026-06-22 10:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("预约成功"));
        }

        @Test
        @DisplayName("POST /AppointmentController/add - 时间冲突返回错误")
        void add_conflict() throws Exception {
            AjaxResult error = AjaxResult.error("该时间段已有预约，请选择其他时间");
            when(appointmentService.insertAppointment(any(Appointment.class))).thenReturn(error);

            mockMvc.perform(post("/AppointmentController/add")
                            .param("petId", "100")
                            .param("serviceTypeId", "10")
                            .param("customerName", "张三")
                            .param("customerPhone", "13800138000")
                            .param("appointmentTime", "2026-06-22 10:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("该时间段已有预约，请选择其他时间"));
        }

        @Test
        @DisplayName("POST /AppointmentController/add - 缺少预约时间返回错误")
        void add_missingTime() throws Exception {
            AjaxResult error = AjaxResult.error("预约时间不能为空");
            when(appointmentService.insertAppointment(any(Appointment.class))).thenReturn(error);

            mockMvc.perform(post("/AppointmentController/add")
                            .param("petId", "100")
                            .param("serviceTypeId", "10")
                            .param("customerName", "张三")
                            .param("customerPhone", "13800138000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("预约列表接口")
    class ListAppointment {

        @Test
        @DisplayName("GET /AppointmentController/list - 查询成功")
        void list_success() throws Exception {
            Appointment a1 = buildValidAppointment();
            Appointment a2 = buildValidAppointment();
            a2.setId(2L);
            a2.setCustomerName("李四");

            when(appointmentService.selectAppointmentList(any(Appointment.class), nullable(Date.class), nullable(Date.class)))
                    .thenReturn(Arrays.asList(a1, a2));

            mockMvc.perform(get("/AppointmentController/list")
                            .param("page", "1")
                            .param("limit", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data", hasSize(2)));
        }

        @Test
        @DisplayName("GET /AppointmentController/list - 带日期筛选")
        void list_withDateFilter() throws Exception {
            when(appointmentService.selectAppointmentList(any(Appointment.class), nullable(Date.class), nullable(Date.class)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/AppointmentController/list")
                            .param("page", "1")
                            .param("limit", "10")
                            .param("startDate", "2026-06-01")
                            .param("endDate", "2026-06-30"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("GET /AppointmentController/list - 空列表")
        void list_empty() throws Exception {
            when(appointmentService.selectAppointmentList(any(Appointment.class), isNull(), isNull()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/AppointmentController/list"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }
    }

    @Nested
    @DisplayName("状态流转接口")
    class UpdateStatus {

        @Test
        @DisplayName("POST /AppointmentController/updateStatus - 合法流转成功")
        void updateStatus_success() throws Exception {
            AjaxResult success = AjaxResult.success("状态更新成功");
            when(appointmentService.updateStatus(1L, "1")).thenReturn(success);

            mockMvc.perform(post("/AppointmentController/updateStatus")
                            .param("id", "1")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("状态更新成功"));
        }

        @Test
        @DisplayName("POST /AppointmentController/updateStatus - 非法跳转报错")
        void updateStatus_illegal() throws Exception {
            AjaxResult error = AjaxResult.error("非法状态流转：不能从待确认变更为已完成");
            when(appointmentService.updateStatus(1L, "3")).thenReturn(error);

            mockMvc.perform(post("/AppointmentController/updateStatus")
                            .param("id", "1")
                            .param("status", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value(containsString("非法状态流转")));
        }

        @Test
        @DisplayName("POST /AppointmentController/updateStatus - 预约不存在")
        void updateStatus_notFound() throws Exception {
            AjaxResult error = AjaxResult.error("预约不存在");
            when(appointmentService.updateStatus(999L, "1")).thenReturn(error);

            mockMvc.perform(post("/AppointmentController/updateStatus")
                            .param("id", "999")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("预约不存在"));
        }
    }

    @Nested
    @DisplayName("删除预约接口")
    class RemoveAppointment {

        @Test
        @DisplayName("DELETE /AppointmentController/remove - 删除成功")
        void remove_success() throws Exception {
            when(appointmentService.deleteAppointmentByIds("1")).thenReturn(1);

            mockMvc.perform(delete("/AppointmentController/remove")
                            .param("ids", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("DELETE /AppointmentController/remove - 删除失败")
        void remove_fail() throws Exception {
            when(appointmentService.deleteAppointmentByIds("999")).thenReturn(0);

            mockMvc.perform(delete("/AppointmentController/remove")
                            .param("ids", "999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("修改预约接口")
    class EditAppointment {

        @Test
        @DisplayName("POST /AppointmentController/edit - 修改成功")
        void edit_success() throws Exception {
            AjaxResult success = AjaxResult.success("修改成功");
            when(appointmentService.updateAppointment(any(Appointment.class))).thenReturn(success);

            mockMvc.perform(post("/AppointmentController/edit")
                            .param("id", "1")
                            .param("petId", "100")
                            .param("serviceTypeId", "10")
                            .param("customerName", "张三")
                            .param("customerPhone", "13800138000")
                            .param("appointmentTime", "2026-06-22 10:00:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.msg").value("修改成功"));
        }
    }

    @Nested
    @DisplayName("检查时间冲突接口")
    class CheckTimeConflict {

        @Test
        @DisplayName("GET /AppointmentController/checkTimeConflict - 无冲突")
        void checkTimeConflict_noConflict() throws Exception {
            when(appointmentService.checkTimeConflict(any(Date.class), any(Date.class), nullable(Long.class)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/AppointmentController/checkTimeConflict")
                            .param("startTime", "2026-06-22 10:00:00")
                            .param("endTime", "2026-06-22 11:30:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("GET /AppointmentController/checkTimeConflict - 有冲突")
        void checkTimeConflict_hasConflict() throws Exception {
            when(appointmentService.checkTimeConflict(any(Date.class), any(Date.class), nullable(Long.class)))
                    .thenReturn(Arrays.asList(new Appointment()));

            mockMvc.perform(get("/AppointmentController/checkTimeConflict")
                            .param("startTime", "2026-06-22 10:00:00")
                            .param("endTime", "2026-06-22 11:30:00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("页面跳转接口")
    class ViewPages {

        @Test
        @DisplayName("GET /AppointmentController/view - 列表页面跳转")
        void view() throws Exception {
            mockMvc.perform(get("/AppointmentController/view"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("admin/appointment/list"));
        }

        @Test
        @DisplayName("GET /AppointmentController/add - 新增页面跳转")
        void addPage() throws Exception {
            when(petService.selectPetList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
            when(serviceTypeService.selectEnabledServiceTypeList()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/AppointmentController/add"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("admin/appointment/add"));
        }

        @Test
        @DisplayName("GET /AppointmentController/edit/{id} - 编辑页面跳转")
        void editPage() throws Exception {
            Appointment appointment = buildValidAppointment();
            when(appointmentService.selectAppointmentById(1L)).thenReturn(appointment);
            when(petService.selectPetList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());
            when(serviceTypeService.selectEnabledServiceTypeList()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/AppointmentController/edit/1"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("admin/appointment/edit"));
        }

        @Test
        @DisplayName("GET /AppointmentController/detail/{id} - 详情页面跳转")
        void detailPage() throws Exception {
            Appointment appointment = buildValidAppointment();
            when(appointmentService.selectAppointmentDetailById(1L)).thenReturn(appointment);

            mockMvc.perform(get("/AppointmentController/detail/1"))
                    .andExpect(status().isOk())
                    .andExpect(forwardedUrl("admin/appointment/detail"));
        }
    }
}
