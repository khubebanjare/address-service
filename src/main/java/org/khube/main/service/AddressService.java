package org.khube.main.service;

import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;

import java.util.List;

public interface AddressService {

    /**
     * Creates a new address.
     *
     * @param addressCreateDto the DTO containing address creation details
     * @return the created AddressDto
     */
    AddressDto createAddress(AddressCreateDto addressCreateDto);

    /**
     * Fetches an address by its ID.
     *
     * @param addressId the ID of the address to fetch
     * @return the AddressDto if found, or throws an exception if not found
     */
    AddressDto fetchAddressById(Long addressId);

    /**
     * Fetches all addresses.
     *
     * @return a list of AddressDto containing all addresses
     */
    List<AddressDto> fetchAllAddresses();
}
