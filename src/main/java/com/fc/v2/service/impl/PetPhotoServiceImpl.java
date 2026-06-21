package com.fc.v2.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.conf.V2Config;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.PetPhotoMapper;
import com.fc.v2.model.auto.PetPhoto;
import com.fc.v2.service.ITPetPhotoService;
import com.fc.v2.util.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetPhotoServiceImpl extends ServiceImpl<PetPhotoMapper, PetPhoto> implements ITPetPhotoService {

    @Autowired
    private PetPhotoMapper petPhotoMapper;

    @Autowired
    private V2Config v2Config;

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

    private void deletePhotoFile(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return;
        }
        try {
            String relativePath = photoUrl;
            if (relativePath.startsWith(FileUploadUtils.RESOURCE_PREFIX)) {
                relativePath = relativePath.substring(FileUploadUtils.RESOURCE_PREFIX.length());
            }
            String realPath = v2Config.getProfile() + relativePath;
            File file = new File(realPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public int deletePetPhotoByIdWithFile(Long id) {
        PetPhoto photo = selectPetPhotoById(id);
        if (photo != null) {
            deletePhotoFile(photo.getPhotoUrl());
        }
        return deletePetPhotoById(id);
    }

    @Override
    public int deletePetPhotoByIdsWithFile(String ids) {
        String[] idsArray = ConvertUtil.toStrArray(ids);
        int count = 0;
        for (String idStr : idsArray) {
            Long id = Long.parseLong(idStr);
            count += deletePetPhotoByIdWithFile(id);
        }
        return count;
    }

    @Override
    public int deletePetPhotoByPetIdWithFile(Long petId) {
        List<PetPhoto> photos = selectPetPhotoByPetId(petId);
        if (photos != null) {
            for (PetPhoto photo : photos) {
                deletePhotoFile(photo.getPhotoUrl());
            }
        }
        return deletePetPhotoByPetId(petId);
    }
}
