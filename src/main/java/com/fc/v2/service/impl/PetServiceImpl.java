package com.fc.v2.service.impl;

import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.PetMapper;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.AppointmentStatus;
import com.fc.v2.model.auto.Pet;
import com.fc.v2.model.auto.PetPhoto;
import com.fc.v2.service.ITAppointmentService;
import com.fc.v2.service.ITPetPhotoService;
import com.fc.v2.service.ITPetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements ITPetService {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private ITPetPhotoService petPhotoService;

    @Autowired
    private ITAppointmentService appointmentService;

    @Override
    public Pet selectPetById(Long id) {
        return petMapper.selectPetById(id);
    }

    @Override
    public List<Pet> selectPetList(Wrapper<Pet> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Pet> selectPetList(Pet pet) {
        return petMapper.selectPetList(pet);
    }

    @Override
    public int insertPet(Pet pet) {
        return this.baseMapper.insert(pet);
    }

    @Override
    public int updatePet(Pet pet) {
        return this.baseMapper.updateById(pet);
    }

    @Override
    public int deletePetByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        for (String id : idsArray) {
            Long petId = Long.parseLong(id);
            petPhotoService.deletePetPhotoByPetIdWithFile(petId);
        }
        return this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
    }

    @Override
    public int deletePetById(Long id) {
        petPhotoService.deletePetPhotoByPetIdWithFile(id);
        return this.baseMapper.deleteById(id);
    }

    @Override
    public Pet selectPetDetailById(Long id) {
        Pet pet = petMapper.selectPetDetailById(id);
        if (pet != null) {
            List<PetPhoto> photos = petPhotoService.selectPetPhotoByPetId(id);
            pet.setPhotos(photos);
            List<Appointment> serviceRecords = appointmentService.selectAppointmentByPetId(id);
            for (Appointment appointment : serviceRecords) {
                appointment.setStatusName(AppointmentStatus.getNameByCode(appointment.getStatus()));
            }
            pet.setServiceRecords(serviceRecords);
        }
        return pet;
    }

    @Override
    public PageInfo<Pet> selectPetPage(Pet pet, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Pet> list = petMapper.selectPetList(pet);
        return new PageInfo<>(list);
    }
}
