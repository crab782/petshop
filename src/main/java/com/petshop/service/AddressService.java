package com.petshop.service;

import com.petshop.dto.AddressDTO;
import com.petshop.entity.Address;
import com.petshop.entity.User;
import com.petshop.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public AddressDTO create(Address address) {
        Address savedAddress = addressRepository.save(address);
        return toDTO(savedAddress);
    }

    public AddressDTO findById(Integer id) {
        Address address = addressRepository.findById(id).orElse(null);
        return address != null ? toDTO(address) : null;
    }

    public Address findByIdEntity(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    public List<AddressDTO> findByUserId(Integer userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO update(Address address) {
        Address savedAddress = addressRepository.save(address);
        return toDTO(savedAddress);
    }

    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }

    @Transactional
    public void clearDefaultByUserId(Integer userId) {
        addressRepository.clearDefaultByUserId(userId);
    }

    @Transactional
    public AddressDTO setDefault(Integer userId, Integer addressId) {
        Address address = addressRepository.findById(addressId).orElse(null);
        if (address != null && address.getUser().getId().equals(userId)) {
            addressRepository.clearDefaultByUserId(userId);
            address.setIsDefault(true);
            Address savedAddress = addressRepository.save(address);
            return toDTO(savedAddress);
        }
        return null;
    }

    private AddressDTO toDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .userId(address.getUser() != null ? address.getUser().getId() : null)
                .contactName(address.getContactName())
                .phone(address.getPhone())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .build();
    }

    public Address toEntity(AddressDTO dto, User user) {
        Address address = new Address();
        address.setId(dto.getId());
        address.setContactName(dto.getContactName());
        address.setPhone(dto.getPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        address.setUser(user);
        return address;
    }
}
