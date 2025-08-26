package org.khube.main.controller;

import jakarta.validation.Valid;
import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;
import org.khube.main.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Creates a new address.
     *
     * @param addressCreateDto the address data to be created
     * @return ResponseEntity with the created AddressDto and HTTP status
     */
    @PostMapping("/create")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressCreateDto addressCreateDto) {
        if (addressCreateDto == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        AddressDto createdEmployee = addressService.createAddress(addressCreateDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    /**
     * Fetches an address by its ID.
     *
     * @param addressId the ID of the address to fetch
     * @return ResponseEntity with the AddressDto and HTTP status
     */
    @GetMapping("/fetch/{addressId}")
    public ResponseEntity<AddressDto> fetchAddressById(@PathVariable("addressId") Long addressId) {
        if(addressId == null) {
            throw new IllegalArgumentException("Address ID must not be null");
        }
        AddressDto addressDto = addressService.fetchAddressById(addressId);
        return new ResponseEntity<>(addressDto, HttpStatus.OK);
    }

    /**
     * Fetches all addresses.
     *
     * @return ResponseEntity with a list of AddressDto and HTTP status
     */
    @GetMapping("/fetch/all")
    public ResponseEntity<List<AddressDto>> fetchAllAddresses() {
        List<AddressDto> addressList = addressService.fetchAllAddresses();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

}
