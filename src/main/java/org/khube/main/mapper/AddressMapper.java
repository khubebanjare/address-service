package org.khube.main.mapper;

import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;
import org.khube.main.entity.Address;

public class AddressMapper {

    private AddressMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Maps AddressCreateDto to Address entity.
     *
     * @param addressCreateDto the DTO to map
     * @return the mapped Address entity
     */
    public static Address mapToEntity(AddressCreateDto addressCreateDto) {
        if (addressCreateDto == null) {
            return null;
        }

        Address address = new Address();
        address.setStreet(addressCreateDto.getStreet());
        address.setLandmark(addressCreateDto.getLandmark());
        address.setCity(addressCreateDto.getCity());
        address.setState(addressCreateDto.getState());
        address.setCountry(addressCreateDto.getCountry());
        address.setPinCode(addressCreateDto.getPinCode());
        return address;
    }

    /**
     * Updates an existing Address entity with values from AddressCreateDto.
     *
     * @param dto the DTO containing new values
     * @param entity the existing Address entity to update
     */
    public static void mapToUpdateEntity(AddressCreateDto dto, Address entity) {
        if (dto == null || entity == null) return;

        if (dto.getStreet() != null) entity.setStreet(dto.getStreet());
        if (dto.getLandmark() != null) entity.setLandmark(dto.getLandmark());
        if (dto.getCity() != null) entity.setCity(dto.getCity());
        if (dto.getState() != null) entity.setState(dto.getState());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getPinCode() != null) entity.setPinCode(dto.getPinCode());
    }

    /**
     * Maps Address entity to AddressDto.
     *
     * @param address the entity to map
     * @return the mapped AddressDto
     */
    public static AddressDto mapToDto(Address address) {
        if (address == null) {
            return null;
        }

        AddressDto addressDto = new AddressDto();
        addressDto.setAddressId(address.getAddressId());
        addressDto.setStreet(address.getStreet());
        addressDto.setLandmark(address.getLandmark());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setCountry(address.getCountry());
        addressDto.setPinCode(address.getPinCode());
        return addressDto;
    }
}
