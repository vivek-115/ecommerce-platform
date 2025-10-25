package com.example.ecomDemo.Controllers;

import com.example.ecomDemo.Entity.User;
import com.example.ecomDemo.PayLoad.AddressDTO;
import com.example.ecomDemo.Service.AddressService;
import com.example.ecomDemo.Utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        User user=authUtil.loggedInUser();

       AddressDTO savedaddressDTO = addressService.addAddress(addressDTO,user);
       return new ResponseEntity<>(savedaddressDTO, HttpStatus.CREATED);

    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress(){
        List<AddressDTO> addressDTOS=addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Integer addressId){
     AddressDTO addressDTO=   addressService.getAddressById(addressId);
     return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser(){
     User user=   authUtil.loggedInUser();
    List<AddressDTO> addressDTOList= addressService.getAddressByUser(user);
    return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Integer addressId){
     AddressDTO updatedAddress=   addressService.updateAddresById(addressDTO, addressId);
     return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Integer addressId){
     String status=   addressService.deleteAddressById(addressId);
     return new ResponseEntity<>(status,HttpStatus.OK);
    }

}
