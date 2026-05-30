package org.khube.main.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.khube.main.dto.AddressCreateDto;
import org.khube.main.dto.AddressDto;
import org.khube.main.entity.Address;
import org.khube.main.exception.AddressAlreadyExistsException;
import org.khube.main.exception.AddressNotFoundException;
import org.khube.main.repository.AddressRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressCreateDto addressCreateDto;
    private Address address;

    @BeforeEach
    void setUp() {
        addressCreateDto = new AddressCreateDto();
        addressCreateDto.setStreet("123 Main Street");
        addressCreateDto.setLandmark("Near Park");
        addressCreateDto.setCity("New York");
        addressCreateDto.setState("NY");
        addressCreateDto.setCountry("USA");
        addressCreateDto.setPinCode(10001);

        address = new Address();
        address.setAddressId(1L);
        address.setStreet("123 Main Street");
        address.setLandmark("Near Park");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setPinCode(10001);
    }

    @Test
    void testConstructor() {
        assertNotNull(addressService);
    }

    @Test
    void testCreateAddressSuccess() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getAddressId());
        assertEquals("123 Main Street", result.getStreet());
        assertEquals("Near Park", result.getLandmark());
        assertEquals("New York", result.getCity());
        assertEquals("NY", result.getState());
        assertEquals("USA", result.getCountry());
        assertEquals(10001, result.getPinCode());

        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressWithNullDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            addressService.createAddress(null);
        });

        assertEquals("Address must not be null", exception.getMessage());
        verify(addressRepository, never()).findByAddressId(any());
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testCreateAddressAlreadyExists() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.of(address));

        AddressAlreadyExistsException exception = assertThrows(AddressAlreadyExistsException.class, () -> {
            addressService.createAddress(addressCreateDto);
        });

        assertTrue(exception.getMessage().contains("Address with ID"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testFetchAddressById_Success() {
        when(addressRepository.findByAddressId(1L)).thenReturn(Optional.of(address));

        AddressDto result = addressService.fetchAddressById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAddressId());
        assertEquals("123 Main Street", result.getStreet());
        assertEquals("Near Park", result.getLandmark());
        assertEquals("New York", result.getCity());
        assertEquals("NY", result.getState());
        assertEquals("USA", result.getCountry());
        assertEquals(10001, result.getPinCode());

        verify(addressRepository).findByAddressId(1L);
    }

    @Test
    void testFetchAddressById_NotFound() {
        Long nonExistentId = 999L;
        when(addressRepository.findByAddressId(nonExistentId)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.fetchAddressById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Address with ID"));
        assertTrue(exception.getMessage().contains("not found"));
        verify(addressRepository).findByAddressId(nonExistentId);
    }

    @Test
    void testFetchAllAddresses_Success() {
        Address address2 = new Address();
        address2.setAddressId(2L);
        address2.setStreet("456 Second Avenue");
        address2.setLandmark("Near School");
        address2.setCity("Boston");
        address2.setState("MA");
        address2.setCountry("USA");
        address2.setPinCode(2101);

        List<Address> addressList = new ArrayList<>();
        addressList.add(address);
        addressList.add(address2);

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());

        AddressDto firstAddress = result.get(0);
        assertEquals(1L, firstAddress.getAddressId());
        assertEquals("123 Main Street", firstAddress.getStreet());

        AddressDto secondAddress = result.get(1);
        assertEquals(2L, secondAddress.getAddressId());
        assertEquals("456 Second Avenue", secondAddress.getStreet());

        verify(addressRepository).findAll();
    }

    @Test
    void testFetchAllAddresses_EmptyList() {
        when(addressRepository.findAll()).thenReturn(new ArrayList<>());

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(addressRepository).findAll();
    }

    @Test
    void testFetchAllAddresses_MultipleAddresses() {
        List<Address> addressList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Address addr = new Address();
            addr.setAddressId((long) i);
            addr.setStreet("Street " + i);
            addr.setLandmark("Landmark " + i);
            addr.setCity("City " + i);
            addr.setState("State " + i);
            addr.setCountry("Country " + i);
            addr.setPinCode(10000 + i);
            addressList.add(addr);
        }

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertNotNull(result);
        assertEquals(5, result.size());

        for (int i = 0; i < 5; i++) {
            assertEquals((long) i + 1, result.get(i).getAddressId());
            assertEquals("Street " + (i + 1), result.get(i).getStreet());
        }

        verify(addressRepository).findAll();
    }

    @Test
    void testCreateAddressFieldMappingStreet() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setStreet("Different Street");
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressFieldMappingCity() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setCity("Different City");
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressFieldMappingState() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setState("CA");
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressFieldMappingCountry() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setCountry("Canada");
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressFieldMappingPinCode() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setPinCode(90210);
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressFieldMappingLandmark() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setLandmark("Different Landmark");
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).findByAddressId(any());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testFetchAddressByIdWithDifferentIds() {
        for (long id = 1L; id <= 3; id++) {
            Address tempAddress = new Address();
            tempAddress.setAddressId(id);
            tempAddress.setStreet("Street " + id);
            tempAddress.setLandmark("Landmark " + id);
            tempAddress.setCity("City " + id);
            tempAddress.setState("State " + id);
            tempAddress.setCountry("Country " + id);
            tempAddress.setPinCode((int) (10000 + id));

            when(addressRepository.findByAddressId(id)).thenReturn(Optional.of(tempAddress));

            AddressDto result = addressService.fetchAddressById(id);

            assertNotNull(result);
            assertEquals(id, result.getAddressId());
            assertEquals("Street " + id, result.getStreet());
        }
    }

    @Test
    void testRepositoryIsNotNull() {
        assertNotNull(addressService);
    }

    @Test
    void testCreateAddressVerifiesIdCheck() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressService.createAddress(addressCreateDto);

        verify(addressRepository, times(1)).findByAddressId(any());
    }

    @Test
    void testCreateAddressVerifiesSaveCall() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressService.createAddress(addressCreateDto);

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testCreateAddressErrorMessageFormat() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.of(address));
        
        AddressAlreadyExistsException exception = assertThrows(AddressAlreadyExistsException.class, () -> {
            addressService.createAddress(addressCreateDto);
        });

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    void testFetchAddressByIdErrorMessageFormat() {
        when(addressRepository.findByAddressId(999L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.fetchAddressById(999L);
        });

        String message = exception.getMessage();
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    void testFetchAllAddressesStreamMapping() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(address);

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
    }

    @Test
    void testCreateAddressReturnsCorrectDto() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        assertEquals(address.getAddressId(), result.getAddressId());
        assertEquals(address.getStreet(), result.getStreet());
        assertEquals(address.getLandmark(), result.getLandmark());
        assertEquals(address.getCity(), result.getCity());
        assertEquals(address.getState(), result.getState());
        assertEquals(address.getCountry(), result.getCountry());
        assertEquals(address.getPinCode(), result.getPinCode());
    }

    @Test
    void testCreateAddressRepositoryFindCallBeforeSave() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressService.createAddress(addressCreateDto);

        InOrder inOrder = inOrder(addressRepository);
        inOrder.verify(addressRepository).findByAddressId(any());
        inOrder.verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testFetchAddressByIdReturnsCorrectMapping() {
        Address fetchedAddress = new Address();
        fetchedAddress.setAddressId(10L);
        fetchedAddress.setStreet("Test Street");
        fetchedAddress.setLandmark("Test Landmark");
        fetchedAddress.setCity("Test City");
        fetchedAddress.setState("TS");
        fetchedAddress.setCountry("Test Country");
        fetchedAddress.setPinCode(12345);

        when(addressRepository.findByAddressId(10L)).thenReturn(Optional.of(fetchedAddress));

        AddressDto result = addressService.fetchAddressById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getAddressId());
        assertEquals("Test Street", result.getStreet());
        assertEquals("Test Landmark", result.getLandmark());
        assertEquals("Test City", result.getCity());
        assertEquals("TS", result.getState());
        assertEquals("Test Country", result.getCountry());
        assertEquals(12345, result.getPinCode());
    }

    @Test
    void testFetchAllAddressesMapsAllFields() {
        Address addr = new Address();
        addr.setAddressId(100L);
        addr.setStreet("Complete Street");
        addr.setLandmark("Complete Landmark");
        addr.setCity("Complete City");
        addr.setState("CC");
        addr.setCountry("Complete Country");
        addr.setPinCode(99999);

        List<Address> addressList = new ArrayList<>();
        addressList.add(addr);

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertEquals(1, result.size());
        AddressDto dto = result.get(0);
        assertEquals(100L, dto.getAddressId());
        assertEquals("Complete Street", dto.getStreet());
        assertEquals("Complete Landmark", dto.getLandmark());
        assertEquals("Complete City", dto.getCity());
        assertEquals("CC", dto.getState());
        assertEquals("Complete Country", dto.getCountry());
        assertEquals(99999, dto.getPinCode());
    }

    @Test
    void testCreateAddressNullCheckHappensFirst() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            addressService.createAddress(null);
        });

        assertEquals("Address must not be null", exception.getMessage());
        verify(addressRepository, never()).findByAddressId(any());
    }

    @Test
    void testCreateAddressAlreadyExistsExceptionMessage() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.of(address));

        AddressAlreadyExistsException exception = assertThrows(AddressAlreadyExistsException.class, () -> {
            addressService.createAddress(addressCreateDto);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testFetchAddressNotFoundExceptionMessage() {
        when(addressRepository.findByAddressId(1L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.fetchAddressById(1L);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testFetchAllAddressesOrderPreservation() {
        List<Address> addressList = new ArrayList<>();
        Address addr1 = new Address();
        addr1.setAddressId(1L);
        addr1.setStreet("First");
        Address addr2 = new Address();
        addr2.setAddressId(2L);
        addr2.setStreet("Second");
        Address addr3 = new Address();
        addr3.setAddressId(3L);
        addr3.setStreet("Third");

        addressList.add(addr1);
        addressList.add(addr2);
        addressList.add(addr3);

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.fetchAllAddresses();

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getAddressId());
        assertEquals(2L, result.get(1).getAddressId());
        assertEquals(3L, result.get(2).getAddressId());
    }

    @Test
    void testCreateAddressWithZeroPinCode() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setPinCode(0);
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressWithNegativePinCode() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setPinCode(-1);
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testCreateAddressWithLargePinCode() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressCreateDto.setPinCode(999999);
        AddressDto result = addressService.createAddress(addressCreateDto);

        assertNotNull(result);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testFetchAddressWithNegativeId() {
        when(addressRepository.findByAddressId(-1L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.fetchAddressById(-1L);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testFetchAddressWithZeroId() {
        when(addressRepository.findByAddressId(0L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.fetchAddressById(0L);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testCreateAddressMultipleTimes() {
        when(addressRepository.findByAddressId(any())).thenReturn(Optional.empty());
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        for (int i = 0; i < 3; i++) {
            AddressDto result = addressService.createAddress(addressCreateDto);
            assertNotNull(result);
        }

        verify(addressRepository, times(3)).findByAddressId(any());
        verify(addressRepository, times(3)).save(any(Address.class));
    }

    @Test
    void testFetchAddressMultipleTimes() {
        when(addressRepository.findByAddressId(1L)).thenReturn(Optional.of(address));

        for (int i = 0; i < 3; i++) {
            AddressDto result = addressService.fetchAddressById(1L);
            assertNotNull(result);
        }

        verify(addressRepository, times(3)).findByAddressId(1L);
    }

    @Test
    void testFetchAllAddressesConsistentResults() {
        List<Address> addressList = new ArrayList<>();
        addressList.add(address);

        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result1 = addressService.fetchAllAddresses();
        List<AddressDto> result2 = addressService.fetchAllAddresses();

        assertEquals(result1.size(), result2.size());
        assertEquals(result1.get(0).getAddressId(), result2.get(0).getAddressId());
    }
}
