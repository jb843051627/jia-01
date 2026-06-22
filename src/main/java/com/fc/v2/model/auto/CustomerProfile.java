package com.fc.v2.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fc.v2.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@TableName("t_customer_profile")
@ApiModel(value = "CustomerProfile", description = "客户画像表")
public class CustomerProfile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户电话")
    private String customerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "首次服务时间")
    private Date firstServiceTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "最近服务时间")
    private Date lastServiceTime;

    @ApiModelProperty(value = "累计预约数")
    private Integer totalAppointments;

    @ApiModelProperty(value = "累计订单数")
    private Integer totalOrders;

    @ApiModelProperty(value = "累计消费金额")
    private BigDecimal totalSpent;

    @ApiModelProperty(value = "标签(逗号分隔)")
    private String tags;

    @TableField(exist = false)
    @ApiModelProperty(value = "标签列表")
    private List<CustomerTag> tagList;

    @TableField(exist = false)
    @ApiModelProperty(value = "预约时间线")
    private List<Appointment> appointmentTimeline;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单时间线")
    private List<TOrder> orderTimeline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Date getFirstServiceTime() {
        return firstServiceTime;
    }

    public void setFirstServiceTime(Date firstServiceTime) {
        this.firstServiceTime = firstServiceTime;
    }

    public Date getLastServiceTime() {
        return lastServiceTime;
    }

    public void setLastServiceTime(Date lastServiceTime) {
        this.lastServiceTime = lastServiceTime;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<CustomerTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<CustomerTag> tagList) {
        this.tagList = tagList;
    }

    public List<Appointment> getAppointmentTimeline() {
        return appointmentTimeline;
    }

    public void setAppointmentTimeline(List<Appointment> appointmentTimeline) {
        this.appointmentTimeline = appointmentTimeline;
    }

    public List<TOrder> getOrderTimeline() {
        return orderTimeline;
    }

    public void setOrderTimeline(List<TOrder> orderTimeline) {
        this.orderTimeline = orderTimeline;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("customerName", getCustomerName())
                .append("customerPhone", getCustomerPhone())
                .append("firstServiceTime", getFirstServiceTime())
                .append("lastServiceTime", getLastServiceTime())
                .append("totalAppointments", getTotalAppointments())
                .append("totalOrders", getTotalOrders())
                .append("totalSpent", getTotalSpent())
                .append("tags", getTags())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
