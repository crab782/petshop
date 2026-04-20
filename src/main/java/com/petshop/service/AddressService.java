package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.AddressDTO;
import com.petshop.entity.Address;
import com.petshop.entity.User;
import com.petshop.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressMapper addressMapper;

    public AddressDTO create(Address address) {
        addressMapper.insert(address);
        return toDTO(address);
    }

    public AddressDTO findById(Integer id) {
        Address address = addressMapper.selectById(id);
        return address != null ? toDTO(address) : null;
    }

    public Address findByIdEntity(Integer id) {
        return addressMapper.selectById(id);
    }

    public List<AddressDTO> findByUserId(Integer userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AddressDTO update(Address address) {
        addressMapper.updateById(address);
        return toDTO(address);
    }

    public void delete(Integer id) {
        addressMapper.deleteById(id);
    }

    @Transactional
    public void clearDefaultByUserId(Integer userId) {
        addressMapper.clearDefaultByUserId(userId);
    }

    @Transactional
    public AddressDTO setDefault(Integer userId, Integer addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            addressMapper.clearDefaultByUserId(userId);
            address.setIsDefault(true);
            addressMapper.updateById(address);
            return toDTO(address);
        }
        return null;
    }

    private AddressDTO toDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .userId(address.getUserId())
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
        address.setUserId(user.getId());
        return address;
    }
}
