package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Pet;
import com.petshop.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetMapper petMapper;

    public Pet create(Pet pet) {
        petMapper.insert(pet);
        return pet;
    }

    public Pet findById(Integer id) {
        return petMapper.selectById(id);
    }

    public List<Pet> findByUserId(Integer userId) {
        return petMapper.selectList(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getUserId, userId));
    }

    public Pet update(Pet pet) {
        petMapper.updateById(pet);
        return pet;
    }

    public void delete(Integer id) {
        petMapper.deleteById(id);
    }
}
