package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.model.auto.Pet;
import com.github.pagehelper.PageInfo;

public interface ITPetService extends IService<Pet> {

    public Pet selectPetById(Long id);

    public List<Pet> selectPetList(Wrapper<Pet> queryWrapper);

    public List<Pet> selectPetList(Pet pet);

    public int insertPet(Pet pet);

    public int updatePet(Pet pet);

    public int deletePetByIds(String ids);

    public int deletePetById(Long id);

    public Pet selectPetDetailById(Long id);

    public PageInfo<Pet> selectPetPage(Pet pet, int pageNum, int pageSize);
}
