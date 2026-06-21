package com.fc.v2.controller.admin;

import java.util.List;
import com.fc.v2.common.conf.V2Config;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.auto.ServiceFlow;
import com.fc.v2.service.ITServiceFlowService;
import com.fc.v2.util.file.FileUploadUtils;
import com.fc.v2.util.file.MimeTypeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.log.Log;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "服务流程管理", tags = "服务流程管理")
@Controller
@RequestMapping("/ServiceFlowController")
public class ServiceFlowController extends BaseController {

    @Autowired
    private ITServiceFlowService serviceFlowService;

    @Autowired
    private V2Config v2Config;

    @ApiOperation(value = "获取预约的服务流程节点", notes = "获取预约的服务流程节点列表")
    @GetMapping("/list/{appointmentId}")
    @RequiresPermissions("system:serviceFlow:list")
    @ResponseBody
    public AjaxResult list(@PathVariable("appointmentId") Long appointmentId) {
        List<ServiceFlow> list = serviceFlowService.selectServiceFlowByAppointmentId(appointmentId);
        return AjaxResult.successData(200, list);
    }

    @Log(title = "完成流程节点", action = "completeNode")
    @ApiOperation(value = "完成流程节点", notes = "标记当前节点为完成并激活下一个节点")
    @PostMapping("/completeNode")
    @RequiresPermissions("system:serviceFlow:operate")
    @ResponseBody
    public AjaxResult completeNode(Long id, String remark) {
        return serviceFlowService.completeNode(id, remark);
    }

    @Log(title = "更新节点备注", action = "updateRemark")
    @ApiOperation(value = "更新节点备注", notes = "更新流程节点的备注信息")
    @PostMapping("/updateRemark")
    @RequiresPermissions("system:serviceFlow:operate")
    @ResponseBody
    public AjaxResult updateRemark(Long id, String remark) {
        return serviceFlowService.updateNodeRemark(id, remark);
    }

    @Log(title = "上传节点照片", action = "uploadPhoto")
    @ApiOperation(value = "上传流程节点照片", notes = "上传流程节点的照片")
    @PostMapping("/uploadPhoto")
    @RequiresPermissions("system:serviceFlow:operate")
    @ResponseBody
    public AjaxResult uploadPhoto(@RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String photoUrl = FileUploadUtils.upload(v2Config.getProfile(), v2Config.getServiceFlowPhotoPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
                return serviceFlowService.uploadNodePhoto(id, photoUrl, originalFilename);
            }
            return AjaxResult.error("上传照片不能为空");
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "获取节点详情", notes = "获取流程节点详情")
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:serviceFlow:list")
    @ResponseBody
    public AjaxResult detail(@PathVariable("id") Long id) {
        ServiceFlow node = serviceFlowService.selectServiceFlowById(id);
        return AjaxResult.successData(200, node);
    }
}
