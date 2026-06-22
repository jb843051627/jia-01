package com.fc.v2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.auto.CustomerProfile;

import java.util.List;

public interface ITCustomerProfileService extends IService<CustomerProfile> {

    CustomerProfile selectProfileById(Long id);

    CustomerProfile selectProfileDetailById(Long id);

    List<CustomerProfile> selectProfileList(CustomerProfile customerProfile);

    List<CustomerProfile> searchByKeyword(String keyword);

    AjaxResult addTag(Long profileId, String tagName);

    AjaxResult removeTag(Long tagId);

    void refreshOrCreateProfile(String customerName, String customerPhone);

    void refreshProfileStats(Long profileId);
}
