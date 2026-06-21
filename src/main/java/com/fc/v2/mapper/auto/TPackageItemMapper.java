package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.TPackageItem;

public interface TPackageItemMapper extends BaseMapper<TPackageItem> {

    public List<TPackageItem> selectPackageItemsByPackageId(Long packageId);

    public int deletePackageItemsByPackageId(Long packageId);
}
