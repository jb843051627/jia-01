package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.ServiceTypeMapper;
import com.fc.v2.mapper.auto.TPackageItemMapper;
import com.fc.v2.mapper.auto.TPackageMapper;
import com.fc.v2.model.auto.ServiceType;
import com.fc.v2.model.auto.TPackage;
import com.fc.v2.model.auto.TPackageItem;
import com.fc.v2.service.ITPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TPackageServiceImpl extends ServiceImpl<TPackageMapper, TPackage> implements ITPackageService {

    @Autowired
    private TPackageMapper tPackageMapper;

    @Autowired
    private TPackageItemMapper tPackageItemMapper;

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Override
    public TPackage selectPackageById(Long id) {
        return tPackageMapper.selectPackageById(id);
    }

    @Override
    public TPackage selectPackageDetailById(Long id) {
        TPackage tPackage = tPackageMapper.selectPackageById(id);
        if (tPackage != null) {
            tPackage.setItems(tPackageItemMapper.selectPackageItemsByPackageId(id));
            tPackage.setStatusName("0".equals(tPackage.getStatus()) ? "启用" : "禁用");
        }
        return tPackage;
    }

    @Override
    public List<TPackage> selectPackageList(Wrapper<TPackage> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<TPackage> selectPackageList(TPackage tPackage) {
        List<TPackage> list = tPackageMapper.selectPackageList(tPackage);
        for (TPackage item : list) {
            item.setStatusName("0".equals(item.getStatus()) ? "启用" : "禁用");
        }
        return list;
    }

    @Override
    public List<TPackage> selectEnabledPackageList() {
        return tPackageMapper.selectEnabledPackageList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertPackage(TPackage tPackage) {
        if (tPackage.getName() == null || tPackage.getName().trim().isEmpty()) {
            return AjaxResult.error("套餐名称不能为空");
        }
        if (tPackage.getItems() == null || tPackage.getItems().isEmpty()) {
            return AjaxResult.error("请至少选择一个商品");
        }

        BigDecimal originalPrice = BigDecimal.ZERO;
        for (TPackageItem item : tPackage.getItems()) {
            if (item.getServiceTypeId() == null) {
                return AjaxResult.error("商品ID不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return AjaxResult.error("商品数量必须大于0");
            }
            ServiceType serviceType = serviceTypeMapper.selectServiceTypeById(item.getServiceTypeId());
            if (serviceType == null) {
                return AjaxResult.error("商品不存在");
            }
            originalPrice = originalPrice.add(serviceType.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        tPackage.setOriginalPrice(originalPrice);
        if (tPackage.getPackagePrice() == null) {
            tPackage.setPackagePrice(originalPrice);
        }
        if (tPackage.getStatus() == null) {
            tPackage.setStatus("0");
        }

        this.baseMapper.insert(tPackage);

        for (TPackageItem item : tPackage.getItems()) {
            item.setId(IdWorker.getId());
            item.setPackageId(tPackage.getId());
            tPackageItemMapper.insert(item);
        }

        return AjaxResult.success("新增套餐成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePackage(TPackage tPackage) {
        if (tPackage.getId() == null) {
            return AjaxResult.error("套餐ID不能为空");
        }
        if (tPackage.getName() == null || tPackage.getName().trim().isEmpty()) {
            return AjaxResult.error("套餐名称不能为空");
        }
        if (tPackage.getItems() == null || tPackage.getItems().isEmpty()) {
            return AjaxResult.error("请至少选择一个商品");
        }

        BigDecimal originalPrice = BigDecimal.ZERO;
        for (TPackageItem item : tPackage.getItems()) {
            if (item.getServiceTypeId() == null) {
                return AjaxResult.error("商品ID不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return AjaxResult.error("商品数量必须大于0");
            }
            ServiceType serviceType = serviceTypeMapper.selectServiceTypeById(item.getServiceTypeId());
            if (serviceType == null) {
                return AjaxResult.error("商品不存在");
            }
            originalPrice = originalPrice.add(serviceType.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        tPackage.setOriginalPrice(originalPrice);
        if (tPackage.getPackagePrice() == null) {
            tPackage.setPackagePrice(originalPrice);
        }

        this.baseMapper.updateById(tPackage);

        tPackageItemMapper.deletePackageItemsByPackageId(tPackage.getId());
        for (TPackageItem item : tPackage.getItems()) {
            item.setId(IdWorker.getId());
            item.setPackageId(tPackage.getId());
            tPackageItemMapper.insert(item);
        }

        return AjaxResult.success("修改套餐成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePackageByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        for (String id : idsArray) {
            tPackageItemMapper.deletePackageItemsByPackageId(Long.parseLong(id));
        }
        return this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePackageById(Long id) {
        tPackageItemMapper.deletePackageItemsByPackageId(id);
        return this.baseMapper.deleteById(id);
    }
}
