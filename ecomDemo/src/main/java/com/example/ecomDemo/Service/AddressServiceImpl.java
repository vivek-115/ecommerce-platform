package com.example.ecomDemo.Service;

import com.example.ecomDemo.Entity.Address;
import com.example.ecomDemo.Entity.User;
import com.example.ecomDemo.Exception.ResourceNotFoundException;
import com.example.ecomDemo.PayLoad.AddressDTO;
import com.example.ecomDemo.Repositry.AddressRepo;
import com.example.ecomDemo.Repositry.OrderRepo;
import com.example.ecomDemo.Repositry.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements  AddressService {

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrderRepo orderRepo;


    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
       List<Address> addressList=user.getAddress();
       addressList.add(address);

       user.setAddress(addressList);
       address.setUser(user);

      Address savedAddress= addressRepo.save(address);


        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
      List<Address> addresses=  addressRepo.findAll();
     List<AddressDTO> addressDTOList= addresses.stream().map(address -> modelMapper.map(address,AddressDTO.class)).toList();
        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressById(Integer addressId) {
       Address address= addressRepo.findById(addressId)
               .orElseThrow(()-> new ResourceNotFoundException("Address with Address Id: "+addressId+" does not exits"));


        return  modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
        List<Address> addressList = user.getAddress();
      List<AddressDTO> addressDTOList=  addressList.stream().map(address -> modelMapper.map(address,AddressDTO.class)).toList();
        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddresById(AddressDTO addressDTO, Integer addressId) {
      Address addressFromDB=  addressRepo.findById(addressId)
              .orElseThrow(()-> new ResourceNotFoundException("Address with Address Id: "+addressId+" does not exits"));

      addressFromDB.setStreet(addressDTO.getStreet());
      addressFromDB.setBuilding(addressDTO.getBuilding());
      addressFromDB.setCity(addressDTO.getCity());
      addressFromDB.setState(addressDTO.getState());
      addressFromDB.setCountry(addressDTO.getCountry());
      addressFromDB.setPincode(addressDTO.getPincode());

    Address updatedAddress=  addressRepo.save(addressFromDB);
        User user = addressFromDB.getUser();

        user.getAddress().removeIf(address ->address.getAddressId()==(addressId));
        user.getAddress().add(updatedAddress);

        userRepo.save(user);


        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

//    @Override
//    public String deleteAddressById(Integer addressId) {
//      Address addressFromDB=  addressRepo.findById(addressId)
//              .orElseThrow(()-> new ResourceNotFoundException("Address with Address Id: "+addressId+" does not exits"));
//
//    User user=  addressFromDB.getUser();
//    user.getAddress().removeIf(address -> address.getAddressId()==addressId);
//    userRepo.save(user);
//
//    addressRepo.delete(addressFromDB);
//        return "Address with Address ID: "+addressId+" deleted SuccessFully from the record";
//    }

    @Override
    public String deleteAddressById(Integer addressId) {
        Address addressFromDB = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID: " + addressId + " does not exist"));

        // 🛑 Prevent deletion if address is used in any orders
        if (orderRepo.existsByAddress(addressFromDB)) {
            throw new IllegalStateException("Cannot delete address because it's associated with existing orders.");
        }

        // ❌ Optional, but keeping it to maintain user-address consistency
        User user = addressFromDB.getUser();
        if (user != null) {
            user.getAddress().removeIf(address -> address.getAddressId() == addressId);
            userRepo.save(user);
        }

        addressRepo.delete(addressFromDB);

        return "Address with Address ID: " + addressId + " deleted successfully from the record";
    }
}
