package com.fc.v2.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.CustomerProfile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerProfileMapper extends BaseMapper<CustomerProfile> {

    CustomerProfile selectProfileById(Long id);

    CustomerProfile selectProfileByPhone(String customerPhone);

    List<CustomerProfile> selectProfileList(CustomerProfile customerProfile);

    int updateProfileStats(@Param("id") Long id);

    List<CustomerProfile> selectProfileListByPhoneOrName(@Param("keyword") String keyword);
}
