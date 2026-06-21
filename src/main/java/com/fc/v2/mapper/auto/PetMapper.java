package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.Pet;

public interface PetMapper extends BaseMapper<Pet> {

    public Pet selectPetById(Long id);

    public List<Pet> selectPetList(Pet pet);

    public Pet selectPetDetailById(Long id);
}
