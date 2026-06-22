package com.fc.v2.service.impl;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.mapper.auto.AppointmentMapper;
import com.fc.v2.mapper.auto.CustomerProfileMapper;
import com.fc.v2.mapper.auto.CustomerTagMapper;
import com.fc.v2.mapper.auto.TOrderMapper;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.AppointmentStatus;
import com.fc.v2.model.auto.CustomerProfile;
import com.fc.v2.model.auto.CustomerTag;
import com.fc.v2.model.auto.OrderStatus;
import com.fc.v2.model.auto.TOrder;
import com.fc.v2.service.ITCustomerProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileServiceImpl extends ServiceImpl<CustomerProfileMapper, CustomerProfile> implements ITCustomerProfileService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProfileServiceImpl.class);

    @Autowired
    private CustomerProfileMapper customerProfileMapper;

    @Autowired
    private CustomerTagMapper customerTagMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private TOrderMapper tOrderMapper;

    @Override
    public CustomerProfile selectProfileById(Long id) {
        return customerProfileMapper.selectProfileById(id);
    }

    @Override
    public CustomerProfile selectProfileDetailById(Long id) {
        CustomerProfile profile = customerProfileMapper.selectProfileById(id);
        if (profile != null) {
            List<CustomerTag> tagList = customerTagMapper.selectTagsByProfileId(id);
            profile.setTagList(tagList);

            Appointment appointmentQuery = new Appointment();
            appointmentQuery.setCustomerPhone(profile.getCustomerPhone());
            List<Appointment> appointments = appointmentMapper.selectAppointmentList(appointmentQuery, null, null);
            for (Appointment apt : appointments) {
                apt.setStatusName(AppointmentStatus.getNameByCode(apt.getStatus()));
            }
            profile.setAppointmentTimeline(appointments);

            TOrder orderQuery = new TOrder();
            orderQuery.setCustomerPhone(profile.getCustomerPhone());
            List<TOrder> orders = tOrderMapper.selectOrderList(orderQuery, null, null);
            for (TOrder order : orders) {
                order.setStatusName(OrderStatus.getNameByCode(order.getStatus()));
            }
            profile.setOrderTimeline(orders);
        }
        return profile;
    }

    @Override
    public List<CustomerProfile> selectProfileList(CustomerProfile customerProfile) {
        return customerProfileMapper.selectProfileList(customerProfile);
    }

    @Override
    public List<CustomerProfile> searchByKeyword(String keyword) {
        return customerProfileMapper.selectProfileListByPhoneOrName(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addTag(Long profileId, String tagName) {
        CustomerProfile profile = customerProfileMapper.selectProfileById(profileId);
        if (profile == null) {
            return AjaxResult.error("客户画像不存在");
        }
        if (tagName == null || tagName.trim().isEmpty()) {
            return AjaxResult.error("标签名称不能为空");
        }

        CustomerTag existing = customerTagMapper.selectTagByProfileIdAndName(profileId, tagName.trim());
        if (existing != null) {
            return AjaxResult.error("标签已存在");
        }

        CustomerTag tag = new CustomerTag();
        tag.setProfileId(profileId);
        tag.setTagName(tagName.trim());
        tag.setCreateTime(new Date());
        customerTagMapper.insert(tag);

        updateTagsField(profileId);
        return AjaxResult.success("添加标签成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult removeTag(Long tagId) {
        CustomerTag tag = customerTagMapper.selectById(tagId);
        if (tag == null) {
            return AjaxResult.error("标签不存在");
        }
        customerTagMapper.deleteTagById(tagId);
        updateTagsField(tag.getProfileId());
        return AjaxResult.success("删除标签成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshOrCreateProfile(String customerName, String customerPhone) {
        if (customerPhone == null || customerPhone.trim().isEmpty()) {
            return;
        }
        CustomerProfile profile = customerProfileMapper.selectProfileByPhone(customerPhone);
        if (profile == null) {
            profile = new CustomerProfile();
            profile.setCustomerName(customerName);
            profile.setCustomerPhone(customerPhone);
            profile.setFirstServiceTime(null);
            profile.setLastServiceTime(new Date());
            profile.setTotalAppointments(0);
            profile.setTotalOrders(0);
            profile.setTotalSpent(java.math.BigDecimal.ZERO);
            this.baseMapper.insert(profile);
        } else {
            if (customerName != null && !customerName.trim().isEmpty()) {
                profile.setCustomerName(customerName);
            }
            this.baseMapper.updateById(profile);
        }
        refreshProfileStats(profile.getId());
    }

    @Override
    public void refreshProfileStats(Long profileId) {
        try {
            customerProfileMapper.updateProfileStats(profileId);
        } catch (Exception e) {
            logger.error("刷新客户画像统计失败, profileId={}", profileId, e);
        }
    }

    private void updateTagsField(Long profileId) {
        List<CustomerTag> tags = customerTagMapper.selectTagsByProfileId(profileId);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(tags.get(i).getTagName());
        }
        CustomerProfile update = new CustomerProfile();
        update.setId(profileId);
        update.setTags(sb.toString());
        update.setUpdateTime(new Date());
        this.baseMapper.updateById(update);
    }
}
