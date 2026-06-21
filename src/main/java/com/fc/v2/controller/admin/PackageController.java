package com.fc.v2.controller.admin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.model.auto.TPackage;
import com.fc.v2.model.auto.TPackageItem;
import com.fc.v2.service.ITPackageService;
import com.fc.v2.service.ITServiceTypeService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Api(value = "套餐管理", tags = "套餐管理")
@Controller
@RequestMapping("/PackageController")
public class PackageController extends BaseController {

    private final String prefix = "admin/package";

    @Autowired
    private ITPackageService packageService;

    @Autowired
    private ITServiceTypeService serviceTypeService;

    @ApiOperation(value = "套餐列表跳转", notes = "套餐列表跳转")
    @GetMapping("/view")
    @RequiresPermissions("system:package:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "套餐列表查询", action = "list")
    @ApiOperation(value = "套餐列表查询", notes = "套餐列表查询")
    @GetMapping("/list")
    @RequiresPermissions("system:package:list")
    @ResponseBody
    public ResultTable list(TPackage tPackage) {
        startPage();
        List<TPackage> list = packageService.selectPackageList(tPackage);
        PageInfo<TPackage> page = new PageInfo<>(list);
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "新增套餐跳转", notes = "新增套餐跳转")
    @GetMapping("/add")
    @RequiresPermissions("system:package:add")
    public String add(ModelMap modelMap) {
        List<ServiceType> serviceTypeList = serviceTypeService.selectEnabledServiceTypeList();
        modelMap.put("serviceTypeList", serviceTypeList);
        return prefix + "/edit";
    }

    @Log(title = "新增套餐", action = "add")
    @ApiOperation(value = "新增套餐", notes = "新增套餐")
    @PostMapping("/add")
    @RequiresPermissions("system:package:add")
    @ResponseBody
    public AjaxResult add(TPackage tPackage, @RequestParam(required = false) String itemsJson) {
        if (itemsJson != null && !itemsJson.isEmpty()) {
            List<TPackageItem> items = JSON.parseObject(itemsJson, new TypeReference<List<TPackageItem>>() {});
            tPackage.setItems(items);
        }
        return packageService.insertPackage(tPackage);
    }

    @Log(title = "删除套餐", action = "remove")
    @ApiOperation(value = "删除套餐", notes = "删除套餐")
    @DeleteMapping("/remove")
    @RequiresPermissions("system:package:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(packageService.deletePackageByIds(ids));
    }

    @ApiOperation(value = "修改套餐跳转", notes = "修改套餐跳转")
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:package:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        TPackage tPackage = packageService.selectPackageDetailById(id);
        List<ServiceType> serviceTypeList = serviceTypeService.selectEnabledServiceTypeList();
        mmap.put("tPackage", tPackage);
        mmap.put("serviceTypeList", serviceTypeList);
        mmap.put("itemsJson", JSON.toJSONString(tPackage.getItems()));
        return prefix + "/edit";
    }

    @Log(title = "修改套餐", action = "edit")
    @ApiOperation(value = "修改套餐", notes = "修改套餐")
    @PostMapping("/edit")
    @RequiresPermissions("system:package:edit")
    @ResponseBody
    public AjaxResult editSave(TPackage tPackage, @RequestParam(required = false) String itemsJson) {
        if (itemsJson != null && !itemsJson.isEmpty()) {
            List<TPackageItem> items = JSON.parseObject(itemsJson, new TypeReference<List<TPackageItem>>() {});
            tPackage.setItems(items);
        }
        return packageService.updatePackage(tPackage);
    }

    @ApiOperation(value = "套餐详情", notes = "套餐详情")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        TPackage tPackage = packageService.selectPackageDetailById(id);
        mmap.put("tPackage", tPackage);
        return prefix + "/detail";
    }

    @ApiOperation(value = "获取启用的套餐列表", notes = "获取启用的套餐列表")
    @GetMapping("/enabledList")
    @ResponseBody
    public AjaxResult enabledList() {
        List<TPackage> list = packageService.selectEnabledPackageList();
        return AjaxResult.successData(200, list);
    }

    @ApiOperation(value = "获取套餐详情数据", notes = "获取套餐详情数据")
    @GetMapping("/detailData/{id}")
    @ResponseBody
    public AjaxResult detailData(@PathVariable("id") Long id) {
        TPackage tPackage = packageService.selectPackageDetailById(id);
        return AjaxResult.successData(200, tPackage);
    }
}
