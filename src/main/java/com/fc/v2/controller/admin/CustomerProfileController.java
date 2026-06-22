package com.fc.v2.controller.admin;

import java.util.List;

import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.auto.CustomerProfile;
import com.fc.v2.service.ITCustomerProfileService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Api(value = "客户画像管理", tags = "客户画像管理")
@Controller
@RequestMapping("/CustomerProfileController")
public class CustomerProfileController extends BaseController {

    private final String prefix = "admin/customerProfile";

    @Autowired
    private ITCustomerProfileService customerProfileService;

    @ApiOperation(value = "客户画像列表跳转", notes = "客户画像列表跳转")
    @GetMapping("/view")
    @RequiresPermissions("system:customerProfile:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "客户画像列表查询", action = "list")
    @ApiOperation(value = "客户画像列表查询", notes = "客户画像列表查询")
    @GetMapping("/list")
    @RequiresPermissions("system:customerProfile:list")
    @ResponseBody
    public ResultTable list(CustomerProfile customerProfile,
                            @RequestParam(required = false) String keyword) {
        startPage();
        List<CustomerProfile> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = customerProfileService.searchByKeyword(keyword.trim());
        } else {
            list = customerProfileService.selectProfileList(customerProfile);
        }
        PageInfo<CustomerProfile> page = new PageInfo<>(list);
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "客户画像详情跳转", notes = "客户画像详情跳转")
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:customerProfile:detail")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        CustomerProfile profile = customerProfileService.selectProfileDetailById(id);
        mmap.put("profile", profile);
        return prefix + "/detail";
    }

    @Log(title = "添加客户标签", action = "addTag")
    @ApiOperation(value = "添加客户标签", notes = "添加客户标签")
    @PostMapping("/addTag")
    @RequiresPermissions("system:customerProfile:addTag")
    @ResponseBody
    public AjaxResult addTag(Long profileId, String tagName) {
        return customerProfileService.addTag(profileId, tagName);
    }

    @Log(title = "删除客户标签", action = "removeTag")
    @ApiOperation(value = "删除客户标签", notes = "删除客户标签")
    @PostMapping("/removeTag")
    @RequiresPermissions("system:customerProfile:removeTag")
    @ResponseBody
    public AjaxResult removeTag(Long tagId) {
        return customerProfileService.removeTag(tagId);
    }

    @ApiOperation(value = "获取客户标签列表", notes = "获取客户标签列表")
    @GetMapping("/tags/{profileId}")
    @RequiresPermissions("system:customerProfile:detail")
    @ResponseBody
    public AjaxResult tags(@PathVariable("profileId") Long profileId) {
        CustomerProfile profile = customerProfileService.selectProfileDetailById(profileId);
        if (profile != null) {
            return AjaxResult.successData(200, profile.getTagList());
        }
        return AjaxResult.error("客户画像不存在");
    }

    @ApiOperation(value = "刷新客户画像", notes = "手动刷新客户画像统计数据")
    @PostMapping("/refresh/{id}")
    @RequiresPermissions("system:customerProfile:detail")
    @ResponseBody
    public AjaxResult refresh(@PathVariable("id") Long id) {
        customerProfileService.refreshProfileStats(id);
        return AjaxResult.success("刷新成功");
    }
}
