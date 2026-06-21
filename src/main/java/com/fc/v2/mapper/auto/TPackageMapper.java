package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.TPackage;

public interface TPackageMapper extends BaseMapper<TPackage> {

    public TPackage selectPackageById(Long id);

    public List<TPackage> selectPackageList(TPackage tPackage);

    public List<TPackage> selectEnabledPackageList();
}
