package com.fc.v2.mapper.auto;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fc.v2.model.auto.PetPhoto;

public interface PetPhotoMapper extends BaseMapper<PetPhoto> {

    public PetPhoto selectPetPhotoById(Long id);

    public List<PetPhoto> selectPetPhotoList(PetPhoto petPhoto);

    public List<PetPhoto> selectPetPhotoByPetId(Long petId);

    public int deletePetPhotoByPetId(Long petId);
}
