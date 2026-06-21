package com.fc.v2.service.impl;

import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.PetPhotoMapper;
import com.fc.v2.model.auto.PetPhoto;
import com.fc.v2.service.ITPetPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetPhotoServiceImpl extends ServiceImpl<PetPhotoMapper, PetPhoto> implements ITPetPhotoService {

    @Autowired
    private PetPhotoMapper petPhotoMapper;

    @Override
    public PetPhoto selectPetPhotoById(Long id) {
        return petPhotoMapper.selectPetPhotoById(id);
    }

    @Override
    public List<PetPhoto> selectPetPhotoList(Wrapper<PetPhoto> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<PetPhoto> selectPetPhotoList(PetPhoto petPhoto) {
        return petPhotoMapper.selectPetPhotoList(petPhoto);
    }

    @Override
    public int insertPetPhoto(PetPhoto petPhoto) {
        return this.baseMapper.insert(petPhoto);
    }

    @Override
    public int updatePetPhoto(PetPhoto petPhoto) {
        return this.baseMapper.updateById(petPhoto);
    }

    @Override
    public int deletePetPhotoByIds(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        return this.baseMapper.deleteBatchIds(Arrays.asList(idsArray));
    }

    @Override
    public int deletePetPhotoById(Long id) {
        return this.baseMapper.deleteById(id);
    }

    @Override
    public List<PetPhoto> selectPetPhotoByPetId(Long petId) {
        return petPhotoMapper.selectPetPhotoByPetId(petId);
    }

    @Override
    public int deletePetPhotoByPetId(Long petId) {
        return petPhotoMapper.deletePetPhotoByPetId(petId);
    }

    @Override
    public int batchInsertPetPhoto(List<PetPhoto> photos) {
        int count = 0;
        for (PetPhoto photo : photos) {
            count += this.baseMapper.insert(photo);
        }
        return count;
    }
}
