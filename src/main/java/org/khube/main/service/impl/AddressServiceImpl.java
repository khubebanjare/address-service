package org.khube.main.service.impl;

import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;
import org.khube.main.entity.Address;
import org.khube.main.exception.AddressAlreadyExistsException;
import org.khube.main.exception.AddressNotFoundException;
import org.khube.main.mapper.AddressMapper;
import org.khube.main.repository.AddressRepository;
import org.khube.main.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Creates a new address based on the provided AddressCreateDto.
     *
     * @param addressCreateDto the DTO containing address details
     * @return the created AddressDto
     * @throws IllegalArgumentException if addressCreateDto is null
     * @throws AddressAlreadyExistsException if an address with the same ID already exists
     */
    @Override
    public AddressDto createAddress(AddressCreateDto addressCreateDto) {
        if (addressCreateDto == null){
            throw new IllegalArgumentException("Address must not be null");
        }

        Address addressMap = AddressMapper.mapToEntity(addressCreateDto);
        addressRepository.findByAddressId(addressMap.getAddressId())
                .ifPresent(existingAddress -> {
                    throw new AddressAlreadyExistsException("Address with ID " + addressMap.getAddressId() + " already exists");
                });
        Address address = addressRepository.save(AddressMapper.mapToEntity(addressCreateDto));
        return AddressMapper.mapToDto(address);
    }

    /**
     * Fetches an address by its ID.
     *
     * @param addressId the ID of the address to fetch
     * @return the AddressDto if found, or throws an exception if not found
     * @throws AddressNotFoundException if no address with the given ID exists
     */
    public AddressDto fetchAddressById(Long addressId) {
        return addressRepository.findByAddressId(addressId)
                .map(AddressMapper::mapToDto)
                .orElseThrow(() -> new AddressNotFoundException("Address with ID " + addressId + " not found"));
    }

    /**
     * Fetches all addresses from the repository.
     *
     * @return a list of AddressDto containing all addresses
     */
    public List<AddressDto> fetchAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(AddressMapper::mapToDto)
                .toList();
    }
}
