package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.User;
import com.example.ecomDemo.PayLoad.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(@Valid AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Integer addressId);

    List<AddressDTO> getAddressByUser(User user);

    AddressDTO updateAddresById(@Valid AddressDTO addressDTO, Integer addressId);

    String deleteAddressById(Integer addressId);
}
