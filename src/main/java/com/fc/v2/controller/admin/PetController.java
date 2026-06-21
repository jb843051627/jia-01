package com.fc.v2.controller.admin;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.conf.V2Config;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.model.auto.Pet;
import com.fc.v2.model.auto.PetPhoto;
import com.fc.v2.service.ITPetPhotoService;
import com.fc.v2.service.ITPetService;
import com.fc.v2.util.file.FileUploadUtils;
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
import org.springframework.web.multipart.MultipartFile;

@Api(value = "宠物管理", tags = "宠物管理")
@Controller
@RequestMapping("/PetController")
public class PetController extends BaseController {

    private final String prefix = "admin/pet";

    @Autowired
    private ITPetService petService;

    @Autowired
    private ITPetPhotoService petPhotoService;

    @Autowired
    private V2Config v2Config;

    @ApiOperation(value = "宠物列表跳转", notes = "宠物列表跳转")
    @GetMapping("/view")
    @RequiresPermissions("system:pet:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "宠物列表查询", action = "list")
    @ApiOperation(value = "宠物列表查询", notes = "宠物列表查询")
    @GetMapping("/list")
    @RequiresPermissions("system:pet:list")
    @ResponseBody
    public ResultTable list(Pet pet) {
        startPage();
        List<Pet> list = petService.selectPetList(pet);
        PageInfo<Pet> page = new PageInfo<>(list);
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "新增宠物跳转", notes = "新增宠物跳转")
    @GetMapping("/add")
    @RequiresPermissions("system:pet:add")
    public String add(ModelMap modelMap) {
        return prefix + "/add";
    }

    @Log(title = "新增宠物", action = "add")
    @ApiOperation(value = "新增宠物", notes = "新增宠物")
    @PostMapping("/add")
    @RequiresPermissions("system:pet:add")
    @ResponseBody
    public AjaxResult add(Pet pet) {
        return toAjax(petService.insertPet(pet));
    }

    @Log(title = "删除宠物", action = "remove")
    @ApiOperation(value = "删除宠物", notes = "删除宠物")
    @DeleteMapping("/remove")
    @RequiresPermissions("system:pet:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(petService.deletePetByIds(ids));
    }

    @ApiOperation(value = "修改宠物跳转", notes = "修改宠物跳转")
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:pet:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        Pet pet = petService.selectPetById(id);
        List<PetPhoto> photos = petPhotoService.selectPetPhotoByPetId(id);
        mmap.put("pet", pet);
        mmap.put("photos", photos);
        return prefix + "/edit";
    }

    @Log(title = "修改宠物", action = "edit")
    @ApiOperation(value = "修改宠物", notes = "修改宠物")
    @PostMapping("/edit")
    @RequiresPermissions("system:pet:edit")
    @ResponseBody
    public AjaxResult editSave(Pet pet) {
        return toAjax(petService.updatePet(pet));
    }

    @ApiOperation(value = "宠物详情跳转", notes = "宠物详情跳转")
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:pet:view")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        Pet pet = petService.selectPetDetailById(id);
        mmap.put("pet", pet);
        return prefix + "/detail";
    }

    @ApiOperation(value = "获取宠物详情", notes = "获取宠物详情")
    @GetMapping("/detailData/{id}")
    @RequiresPermissions("system:pet:view")
    @ResponseBody
    public AjaxResult detailData(@PathVariable("id") Long id) {
        Pet pet = petService.selectPetDetailById(id);
        return AjaxResult.successData(200, pet);
    }

    @Log(title = "上传宠物照片", action = "upload")
    @ApiOperation(value = "上传宠物照片", notes = "上传宠物照片")
    @PostMapping("/uploadPhoto")
    @RequiresPermissions("system:pet:upload")
    @ResponseBody
    public AjaxResult uploadPhoto(@RequestParam("petId") Long petId, @RequestParam("file") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                String photoUrl = FileUploadUtils.upload(v2Config.getProfile(), v2Config.getPetPhotoPath(), file);
                PetPhoto petPhoto = new PetPhoto();
                petPhoto.setPetId(petId);
                petPhoto.setPhotoUrl(photoUrl);
                petPhoto.setPhotoName(file.getOriginalFilename());
                petPhoto.setSort(0);
                return toAjax(petPhotoService.insertPetPhoto(petPhoto));
            }
            return error("上传照片不能为空");
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    @Log(title = "删除宠物照片", action = "remove")
    @ApiOperation(value = "删除宠物照片", notes = "删除宠物照片")
    @DeleteMapping("/removePhoto")
    @RequiresPermissions("system:pet:deletePhoto")
    @ResponseBody
    public AjaxResult removePhoto(Long id) {
        return toAjax(petPhotoService.deletePetPhotoByIdWithFile(id));
    }

    @ApiOperation(value = "获取宠物照片列表", notes = "获取宠物照片列表")
    @GetMapping("/photos/{petId}")
    @RequiresPermissions("system:pet:view")
    @ResponseBody
    public AjaxResult photos(@PathVariable("petId") Long petId) {
        List<PetPhoto> photos = petPhotoService.selectPetPhotoByPetId(petId);
        return AjaxResult.successData(200, photos);
    }

    @ApiOperation(value = "获取宠物下拉列表", notes = "获取宠物下拉列表")
    @GetMapping("/selectList")
    @RequiresPermissions("system:pet:list")
    @ResponseBody
    public AjaxResult selectList() {
        List<Pet> list = petService.selectPetList(new QueryWrapper<>());
        return AjaxResult.successData(200, list);
    }
}
