package com.fc.v2.service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fc.v2.model.auto.PetPhoto;

public interface ITPetPhotoService extends IService<PetPhoto> {

    public PetPhoto selectPetPhotoById(Long id);

    public List<PetPhoto> selectPetPhotoList(Wrapper<PetPhoto> queryWrapper);

    public List<PetPhoto> selectPetPhotoList(PetPhoto petPhoto);

    public int insertPetPhoto(PetPhoto petPhoto);

    public int updatePetPhoto(PetPhoto petPhoto);

    public int deletePetPhotoByIds(String ids);

    public int deletePetPhotoById(Long id);

    public List<PetPhoto> selectPetPhotoByPetId(Long petId);

    public int deletePetPhotoByPetId(Long petId);

    public int batchInsertPetPhoto(List<PetPhoto> photos);
}
