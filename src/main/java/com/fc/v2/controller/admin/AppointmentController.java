package com.fc.v2.controller.admin;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.Pet;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.service.ITAppointmentService;
import com.fc.v2.service.ITPetService;
import com.fc.v2.service.ITServiceTypeService;
import com.fc.v2.util.DateUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.log.Log;

@Api(value = "预约管理", tags = "预约管理")
@Controller
@RequestMapping("/AppointmentController")
public class AppointmentController extends BaseController {

    private final String prefix = "admin/appointment";

    @Autowired
    private ITAppointmentService appointmentService;

    @Autowired
    private ITPetService petService;

    @Autowired
    private ITServiceTypeService serviceTypeService;

    @ApiOperation(value = "预约列表跳转", notes = "预约列表跳转")
    @GetMapping("/view")
    @RequiresPermissions("system:appointment:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "预约列表查询", action = "list")
    @ApiOperation(value = "预约列表查询", notes = "预约列表查询")
    @GetMapping("/list")
    @RequiresPermissions("system:appointment:list")
    @ResponseBody
    public ResultTable list(Appointment appointment,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate) {
        Date start = null;
        Date end = null;
        if (startDate != null && !startDate.isEmpty()) {
            start = DateUtils.parseDate(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = DateUtils.parseDate(endDate);
        }
        startPage();
        List<Appointment> list = appointmentService.selectAppointmentList(appointment, start, end);
        PageInfo<Appointment> page = new PageInfo<>(list);
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "新增预约跳转", notes = "新增预约跳转")
    @GetMapping("/add")
    @RequiresPermissions("system:appointment:add")
    public String add(ModelMap modelMap) {
        List<Pet> petList = petService.selectPetList(new QueryWrapper<>());
        List<ServiceType> serviceTypeList = serviceTypeService.selectEnabledServiceTypeList();
        modelMap.put("petList", petList);
        modelMap.put("serviceTypeList", serviceTypeList);
        return prefix + "/add";
    }

    @Log(title = "新增预约", action = "add")
    @ApiOperation(value = "新增预约", notes = "新增预约")
    @PostMapping("/add")
    @RequiresPermissions("system:appointment:add")
    @ResponseBody
    public AjaxResult add(Appointment appointment) {
        return appointmentService.insertAppointment(appointment);
    }

    @Log(title = "删除预约", action = "remove")
    @ApiOperation(value = "删除预约", notes = "删除预约")
    @DeleteMapping("/remove")
    @RequiresPermissions("system:appointment:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(appointmentService.deleteAppointmentByIds(ids));
    }

    @ApiOperation(value = "修改预约跳转", notes = "修改预约跳转")
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:appointment:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        Appointment appointment = appointmentService.selectAppointmentById(id);
        List<Pet> petList = petService.selectPetList(new QueryWrapper<>());
        List<ServiceType> serviceTypeList = serviceTypeService.selectEnabledServiceTypeList();
        mmap.put("appointment", appointment);
        mmap.put("petList", petList);
        mmap.put("serviceTypeList", serviceTypeList);
        return prefix + "/edit";
    }

    @Log(title = "修改预约", action = "edit")
    @ApiOperation(value = "修改预约", notes = "修改预约")
    @PostMapping("/edit")
    @RequiresPermissions("system:appointment:edit")
    @ResponseBody
    public AjaxResult editSave(Appointment appointment) {
        return appointmentService.updateAppointment(appointment);
    }

    @Log(title = "更新预约状态", action = "updateStatus")
    @ApiOperation(value = "更新预约状态", notes = "更新预约状态")
    @PostMapping("/updateStatus")
    @RequiresPermissions("system:appointment:updateStatus")
    @ResponseBody
    public AjaxResult updateStatus(Long id, String status) {
        return appointmentService.updateStatus(id, status);
    }

    @ApiOperation(value = "检查时间冲突", notes = "检查时间冲突")
    @GetMapping("/checkTimeConflict")
    @RequiresPermissions("system:appointment:check")
    @ResponseBody
    public AjaxResult checkTimeConflict(Date startTime, Date endTime, Long excludeId) {
        List<Appointment> conflicts = appointmentService.checkTimeConflict(startTime, endTime, excludeId);
        if (!conflicts.isEmpty()) {
            return AjaxResult.error("该时间段已有预约，请选择其他时间");
        }
        return AjaxResult.success();
    }

    @ApiOperation(value = "预约详情跳转", notes = "预约详情跳转")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        Appointment appointment = appointmentService.selectAppointmentDetailById(id);
        mmap.put("appointment", appointment);
        return prefix + "/detail";
    }
}
