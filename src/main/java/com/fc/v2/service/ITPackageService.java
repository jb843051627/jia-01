package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.auto.TPackage;

public interface ITPackageService extends IService<TPackage> {

    public TPackage selectPackageById(Long id);

    public TPackage selectPackageDetailById(Long id);

    public List<TPackage> selectPackageList(Wrapper<TPackage> queryWrapper);

    public List<TPackage> selectPackageList(TPackage tPackage);

    public List<TPackage> selectEnabledPackageList();

    public AjaxResult insertPackage(TPackage tPackage);

    public AjaxResult updatePackage(TPackage tPackage);

    public int deletePackageByIds(String ids);

    public int deletePackageById(Long id);
}
