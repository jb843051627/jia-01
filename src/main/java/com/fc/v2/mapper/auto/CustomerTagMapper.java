package com.fc.v2.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.CustomerTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerTagMapper extends BaseMapper<CustomerTag> {

    List<CustomerTag> selectTagsByProfileId(Long profileId);

    int deleteTagById(Long id);

    int deleteTagsByProfileId(Long profileId);

    CustomerTag selectTagByProfileIdAndName(@Param("profileId") Long profileId, @Param("tagName") String tagName);
}
