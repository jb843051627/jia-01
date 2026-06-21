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

import java.util.Date;
import java.util.List;

@TableName("t_pet")
@ApiModel(value = "Pet", description = "宠物表")
public class Pet extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "宠物名字")
    private String name;

    @ApiModelProperty(value = "品种")
    private String breed;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "性别 0=公,1=母")
    private String gender;

    @ApiModelProperty(value = "主人姓名")
    private String ownerName;

    @ApiModelProperty(value = "主人电话")
    private String ownerPhone;

    @TableField(exist = false)
    @ApiModelProperty(value = "宠物照片列表")
    private List<PetPhoto> photos;

    @TableField(exist = false)
    @ApiModelProperty(value = "服务记录列表")
    private List<Appointment> serviceRecords;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public List<PetPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PetPhoto> photos) {
        this.photos = photos;
    }

    public List<Appointment> getServiceRecords() {
        return serviceRecords;
    }

    public void setServiceRecords(List<Appointment> serviceRecords) {
        this.serviceRecords = serviceRecords;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("breed", getBreed())
                .append("age", getAge())
                .append("gender", getGender())
                .append("ownerName", getOwnerName())
                .append("ownerPhone", getOwnerPhone())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
