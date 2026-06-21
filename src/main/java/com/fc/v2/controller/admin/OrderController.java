package com.fc.v2.controller.admin;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.Pet;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.model.auto.TOrder;
import com.fc.v2.model.auto.TOrderItem;
import com.fc.v2.model.auto.TPackage;
import com.fc.v2.service.ITAppointmentService;
import com.fc.v2.service.ITOrderService;
import com.fc.v2.service.ITPetService;
import com.fc.v2.service.ITPackageService;
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

@Api(value = "订单管理", tags = "订单管理")
@Controller
@RequestMapping("/OrderController")
public class OrderController extends BaseController {

    private final String prefix = "admin/order";

    @Autowired
    private ITOrderService orderService;

    @Autowired
    private ITPackageService packageService;

    @Autowired
    private ITServiceTypeService serviceTypeService;

    @Autowired
    private ITPetService petService;

    @Autowired
    private ITAppointmentService appointmentService;

    @ApiOperation(value = "订单列表跳转", notes = "订单列表跳转")
    @GetMapping("/view")
    @RequiresPermissions("system:order:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "订单列表查询", action = "list")
    @ApiOperation(value = "订单列表查询", notes = "订单列表查询")
    @GetMapping("/list")
    @RequiresPermissions("system:order:list")
    @ResponseBody
    public ResultTable list(TOrder tOrder,
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
        List<TOrder> list = orderService.selectOrderList(tOrder, start, end);
        PageInfo<TOrder> page = new PageInfo<>(list);
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "新增订单跳转", notes = "新增订单跳转")
    @GetMapping("/add")
    @RequiresPermissions("system:order:add")
    public String add(ModelMap modelMap) {
        List<TPackage> packageList = packageService.selectEnabledPackageList();
        List<ServiceType> serviceTypeList = serviceTypeService.selectEnabledServiceTypeList();
        List<Pet> petList = petService.selectPetList(new QueryWrapper<>());
        List<Appointment> appointmentList = appointmentService.selectAppointmentList(new QueryWrapper<>());
        modelMap.put("packageList", packageList);
        modelMap.put("serviceTypeList", serviceTypeList);
        modelMap.put("petList", petList);
        modelMap.put("appointmentList", appointmentList);
        return prefix + "/add";
    }

    @Log(title = "新增订单", action = "add")
    @ApiOperation(value = "新增订单", notes = "新增订单")
    @PostMapping("/add")
    @RequiresPermissions("system:order:add")
    @ResponseBody
    public AjaxResult add(TOrder tOrder, @RequestParam(required = false) String itemsJson) {
        if (itemsJson != null && !itemsJson.isEmpty()) {
            List<TOrderItem> items = JSON.parseObject(itemsJson, new TypeReference<List<TOrderItem>>() {});
            tOrder.setItems(items);
        }
        return orderService.createOrder(tOrder);
    }

    @Log(title = "删除订单", action = "remove")
    @ApiOperation(value = "删除订单", notes = "删除订单")
    @DeleteMapping("/remove")
    @RequiresPermissions("system:order:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(orderService.deleteOrderByIds(ids));
    }

    @ApiOperation(value = "订单详情跳转", notes = "订单详情跳转")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        TOrder tOrder = orderService.selectOrderDetailById(id);
        mmap.put("tOrder", tOrder);
        return prefix + "/detail";
    }

    @Log(title = "更新订单状态", action = "updateStatus")
    @ApiOperation(value = "更新订单状态", notes = "更新订单状态")
    @PostMapping("/updateStatus")
    @RequiresPermissions("system:order:updateStatus")
    @ResponseBody
    public AjaxResult updateStatus(Long id, String status) {
        return orderService.updateStatus(id, status);
    }

    @ApiOperation(value = "根据预约获取客户信息", notes = "根据预约获取客户信息")
    @GetMapping("/getCustomerByAppointment/{appointmentId}")
    @ResponseBody
    public AjaxResult getCustomerByAppointment(@PathVariable("appointmentId") Long appointmentId) {
        Appointment appointment = appointmentService.selectAppointmentById(appointmentId);
        if (appointment != null) {
            return AjaxResult.successData(200, appointment);
        }
        return AjaxResult.error("预约不存在");
    }
}
