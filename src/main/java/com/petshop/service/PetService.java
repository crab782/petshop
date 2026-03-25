package com.petshop.service;

import com.petshop.entity.Pet;
import com.petshop.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    public Pet create(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet findById(Integer id) {
        return petRepository.findById(id).orElse(null);
    }

    public List<Pet> findByUserId(Integer userId) {
        return petRepository.findByUserId(userId);
    }

    public Pet update(Pet pet) {
        return petRepository.save(pet);
    }

    public void delete(Integer id) {
        petRepository.deleteById(id);
    }
}
