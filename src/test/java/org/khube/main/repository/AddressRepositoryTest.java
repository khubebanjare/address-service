package org.khube.main.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.khube.main.entity.Address;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AddressRepository addressRepository;

    private Address address;
    private Address address2;
    private Address address3;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setStreet("123 Main Street");
        address.setLandmark("Near Park");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setPinCode(10001);

        address2 = new Address();
        address2.setStreet("456 Second Avenue");
        address2.setLandmark("Near School");
        address2.setCity("Boston");
        address2.setState("MA");
        address2.setCountry("USA");
        address2.setPinCode(02101);

        address3 = new Address();
        address3.setStreet("789 Third Street");
        address3.setLandmark("Near Library");
        address3.setCity("Chicago");
        address3.setState("IL");
        address3.setCountry("USA");
        address3.setPinCode(60601);
    }

    @Test
    void testRepositoryIsNotNull() {
        assertNotNull(addressRepository);
    }

    @Test
    void testSaveAddress() {
        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getAddressId());
        assertEquals("123 Main Street", savedAddress.getStreet());
        assertEquals("Near Park", savedAddress.getLandmark());
        assertEquals("New York", savedAddress.getCity());
        assertEquals("NY", savedAddress.getState());
        assertEquals("USA", savedAddress.getCountry());
        assertEquals(10001, savedAddress.getPinCode());
    }

    @Test
    void testFindByAddressIdSuccess() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findByAddressId(savedAddress.getAddressId());

        assertTrue(foundAddress.isPresent());
        assertEquals(savedAddress.getAddressId(), foundAddress.get().getAddressId());
        assertEquals("123 Main Street", foundAddress.get().getStreet());
    }

    @Test
    void testFindByAddressIdNotFound() {
        Optional<Address> foundAddress = addressRepository.findByAddressId(999L);

        assertFalse(foundAddress.isPresent());
        assertTrue(foundAddress.isEmpty());
    }

    @Test
    void testFindByIdInheritedMethod() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getAddressId());

        assertTrue(foundAddress.isPresent());
        assertEquals(savedAddress.getAddressId(), foundAddress.get().getAddressId());
    }

    @Test
    void testFindAllAddresses() {
        testEntityManager.persistAndFlush(address);
        testEntityManager.persistAndFlush(address2);
        testEntityManager.persistAndFlush(address3);
        testEntityManager.clear();

        List<Address> addresses = addressRepository.findAll();

        assertNotNull(addresses);
        assertEquals(3, addresses.size());
        assertTrue(addresses.stream().anyMatch(a -> a.getStreet().equals("123 Main Street")));
        assertTrue(addresses.stream().anyMatch(a -> a.getStreet().equals("456 Second Avenue")));
        assertTrue(addresses.stream().anyMatch(a -> a.getStreet().equals("789 Third Street")));
    }

    @Test
    void testFindAllAddressesEmpty() {
        List<Address> addresses = addressRepository.findAll();

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());
        assertEquals(0, addresses.size());
    }

    @Test
    void testFindAllAddressesSingle() {
        testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        List<Address> addresses = addressRepository.findAll();

        assertNotNull(addresses);
        assertEquals(1, addresses.size());
        assertEquals("123 Main Street", addresses.get(0).getStreet());
    }

    @Test
    void testDeleteAddressById() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        addressRepository.deleteById(savedAddress.getAddressId());
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getAddressId());
        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testDeleteAddress() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        addressRepository.delete(savedAddress);
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getAddressId());
        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testExistsByIdTrue() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        boolean exists = addressRepository.existsById(savedAddress.getAddressId());

        assertTrue(exists);
    }

    @Test
    void testExistsByIdFalse() {
        boolean exists = addressRepository.existsById(999L);

        assertFalse(exists);
    }

    @Test
    void testUpdateAddress() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        Long addressId = savedAddress.getAddressId();
        testEntityManager.clear();

        Address addressToUpdate = addressRepository.findById(addressId).get();
        addressToUpdate.setStreet("Updated Street");
        addressToUpdate.setCity("Updated City");
        addressToUpdate.setPinCode(99999);

        Address updatedAddress = addressRepository.save(addressToUpdate);
        testEntityManager.clear();

        Address retrieved = addressRepository.findById(addressId).get();
        assertEquals("Updated Street", retrieved.getStreet());
        assertEquals("Updated City", retrieved.getCity());
        assertEquals(99999, retrieved.getPinCode());
    }

    @Test
    void testFindByAddressIdWithMultipleAddresses() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        Address saved2 = testEntityManager.persistAndFlush(address2);
        Address saved3 = testEntityManager.persistAndFlush(address3);
        testEntityManager.clear();

        Optional<Address> found = addressRepository.findByAddressId(saved2.getAddressId());

        assertTrue(found.isPresent());
        assertEquals("456 Second Avenue", found.get().getStreet());
    }

    @Test
    void testSaveMultipleAddresses() {
        addressRepository.save(address);
        addressRepository.save(address2);
        addressRepository.save(address3);

        List<Address> addresses = addressRepository.findAll();

        assertEquals(3, addresses.size());
    }

    @Test
    void testSaveAllMethod() {
        List<Address> addressesToSave = List.of(address, address2, address3);
        List<Address> savedAddresses = addressRepository.saveAll(addressesToSave);

        assertNotNull(savedAddresses);
        assertEquals(3, savedAddresses.size());
        assertTrue(savedAddresses.stream().allMatch(a -> a.getAddressId() != null));
    }

    @Test
    void testCountAddresses() {
        testEntityManager.persistAndFlush(address);
        testEntityManager.persistAndFlush(address2);
        testEntityManager.clear();

        long count = addressRepository.count();

        assertEquals(2, count);
    }

    @Test
    void testCountAddressesEmpty() {
        long count = addressRepository.count();

        assertEquals(0, count);
    }

    @Test
    void testGetById() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Address foundAddress = addressRepository.getById(savedAddress.getAddressId());

        assertNotNull(foundAddress);
        assertEquals("123 Main Street", foundAddress.getStreet());
    }

    @Test
    void testDeleteAll() {
        testEntityManager.persistAndFlush(address);
        testEntityManager.persistAndFlush(address2);
        testEntityManager.clear();

        addressRepository.deleteAll();
        testEntityManager.clear();

        long count = addressRepository.count();
        assertEquals(0, count);
    }

    @Test
    void testDeleteAllWithIterable() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        Address saved2 = testEntityManager.persistAndFlush(address2);
        testEntityManager.persistAndFlush(address3);
        testEntityManager.clear();

        addressRepository.deleteAll(List.of(saved1, saved2));
        testEntityManager.clear();

        long count = addressRepository.count();
        assertEquals(1, count);
    }

    @Test
    void testAddressFieldsAfterSave() {
        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress.getAddressId());
        assertEquals("123 Main Street", savedAddress.getStreet());
        assertEquals("Near Park", savedAddress.getLandmark());
        assertEquals("New York", savedAddress.getCity());
        assertEquals("NY", savedAddress.getState());
        assertEquals("USA", savedAddress.getCountry());
        assertEquals(10001, savedAddress.getPinCode());
    }

    @Test
    void testAddressIsPersistedAfterSave() {
        Address savedAddress = addressRepository.save(address);
        testEntityManager.clear();

        Address retrieved = addressRepository.findById(savedAddress.getAddressId()).get();

        assertEquals(savedAddress.getAddressId(), retrieved.getAddressId());
        assertEquals(savedAddress.getStreet(), retrieved.getStreet());
        assertEquals(savedAddress.getLandmark(), retrieved.getLandmark());
        assertEquals(savedAddress.getCity(), retrieved.getCity());
        assertEquals(savedAddress.getState(), retrieved.getState());
        assertEquals(savedAddress.getCountry(), retrieved.getCountry());
        assertEquals(savedAddress.getPinCode(), retrieved.getPinCode());
    }

    @Test
    void testFindByAddressIdAfterDelete() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        Long addressId = savedAddress.getAddressId();
        testEntityManager.clear();

        addressRepository.deleteById(addressId);
        testEntityManager.clear();

        Optional<Address> foundAddress = addressRepository.findByAddressId(addressId);

        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testFindByAddressIdConsistency() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> found1 = addressRepository.findByAddressId(savedAddress.getAddressId());
        Optional<Address> found2 = addressRepository.findByAddressId(savedAddress.getAddressId());

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals(found1.get().getAddressId(), found2.get().getAddressId());
    }

    @Test
    void testMultipleFindByAddressIdCalls() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        Address saved2 = testEntityManager.persistAndFlush(address2);
        testEntityManager.clear();

        Optional<Address> found1 = addressRepository.findByAddressId(saved1.getAddressId());
        Optional<Address> found2 = addressRepository.findByAddressId(saved2.getAddressId());

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getAddressId(), found2.get().getAddressId());
        assertEquals("123 Main Street", found1.get().getStreet());
        assertEquals("456 Second Avenue", found2.get().getStreet());
    }

    @Test
    void testFindByAddressIdWithNegativeId() {
        Optional<Address> foundAddress = addressRepository.findByAddressId(-1L);

        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testFindByAddressIdWithZeroId() {
        Optional<Address> foundAddress = addressRepository.findByAddressId(0L);

        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testSaveAndRetrieveWithDifferentFields() {
        address.setStreet("Different Street");
        address.setLandmark("Different Landmark");
        address.setCity("Different City");

        Address savedAddress = addressRepository.save(address);
        testEntityManager.clear();

        Optional<Address> retrieved = addressRepository.findByAddressId(savedAddress.getAddressId());

        assertTrue(retrieved.isPresent());
        assertEquals("Different Street", retrieved.get().getStreet());
        assertEquals("Different Landmark", retrieved.get().getLandmark());
        assertEquals("Different City", retrieved.get().getCity());
    }

    @Test
    void testAddressEntityEquality() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> found = addressRepository.findByAddressId(saved1.getAddressId());

        assertTrue(found.isPresent());
        assertEquals(saved1.getAddressId(), found.get().getAddressId());
    }

    @Test
    void testAddressPinCodeVariations() {
        address.setPinCode(0);
        Address saved1 = addressRepository.save(address);
        testEntityManager.clear();

        Optional<Address> found1 = addressRepository.findByAddressId(saved1.getAddressId());
        assertTrue(found1.isPresent());
        assertEquals(0, found1.get().getPinCode());

        address2.setPinCode(999999);
        Address saved2 = addressRepository.save(address2);
        testEntityManager.clear();

        Optional<Address> found2 = addressRepository.findByAddressId(saved2.getAddressId());
        assertTrue(found2.isPresent());
        assertEquals(999999, found2.get().getPinCode());
    }

    @Test
    void testFindByAddressIdReturnsEmptyOptional() {
        Optional<Address> foundAddress = addressRepository.findByAddressId(12345L);

        assertNotNull(foundAddress);
        assertFalse(foundAddress.isPresent());
        assertTrue(foundAddress.isEmpty());
    }

    @Test
    void testFindAllOrderPreservation() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        Address saved2 = testEntityManager.persistAndFlush(address2);
        Address saved3 = testEntityManager.persistAndFlush(address3);
        testEntityManager.clear();

        List<Address> addresses = addressRepository.findAll();

        assertNotNull(addresses);
        assertEquals(3, addresses.size());
        assertEquals(saved1.getAddressId(), addresses.get(0).getAddressId());
        assertEquals(saved2.getAddressId(), addresses.get(1).getAddressId());
        assertEquals(saved3.getAddressId(), addresses.get(2).getAddressId());
    }

    @Test
    void testSaveNullField() {
        address.setLandmark(null);
        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress);
        assertNull(savedAddress.getLandmark());
    }

    @Test
    void testUpdateNullField() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        Long addressId = savedAddress.getAddressId();
        testEntityManager.clear();

        Address toUpdate = addressRepository.findById(addressId).get();
        toUpdate.setLandmark(null);
        addressRepository.save(toUpdate);
        testEntityManager.clear();

        Address retrieved = addressRepository.findById(addressId).get();
        assertNull(retrieved.getLandmark());
    }

    @Test
    void testFindByAddressIdReturnType() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> result = addressRepository.findByAddressId(savedAddress.getAddressId());

        assertNotNull(result);
        assertTrue(result instanceof Optional);
    }

    @Test
    void testAddressRepositoryExtendsJpaRepository() {
        assertTrue(addressRepository instanceof org.springframework.data.jpa.repository.JpaRepository);
    }

    @Test
    void testSaveReturnsPersistedEntity() {
        assertNull(address.getAddressId());

        Address savedAddress = addressRepository.save(address);

        assertNotNull(savedAddress.getAddressId());
        assertNotNull(savedAddress);
    }

    @Test
    void testFindByIdReturnsOptional() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> result = addressRepository.findById(savedAddress.getAddressId());

        assertTrue(result instanceof Optional);
    }

    @Test
    void testFindAllReturnsEmptyListNotNull() {
        List<Address> result = addressRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCountReturnsLong() {
        testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        long count = addressRepository.count();

        assertEquals(1L, count);
        assertTrue(count >= 0);
    }

    @Test
    void testDeleteMultipleAddresses() {
        Address saved1 = testEntityManager.persistAndFlush(address);
        Address saved2 = testEntityManager.persistAndFlush(address2);
        Address saved3 = testEntityManager.persistAndFlush(address3);
        testEntityManager.clear();

        addressRepository.deleteById(saved1.getAddressId());
        addressRepository.deleteById(saved2.getAddressId());
        testEntityManager.clear();

        List<Address> remaining = addressRepository.findAll();
        assertEquals(1, remaining.size());
        assertEquals(saved3.getAddressId(), remaining.get(0).getAddressId());
    }

    @Test
    void testExistsAfterDelete() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        Long addressId = savedAddress.getAddressId();
        testEntityManager.clear();

        assertTrue(addressRepository.existsById(addressId));

        addressRepository.deleteById(addressId);
        testEntityManager.clear();

        assertFalse(addressRepository.existsById(addressId));
    }

    @Test
    void testFindByAddressIdAfterMultipleSaves() {
        addressRepository.save(address);
        addressRepository.save(address2);
        Address saved3 = addressRepository.save(address3);

        Optional<Address> found = addressRepository.findByAddressId(saved3.getAddressId());

        assertTrue(found.isPresent());
        assertEquals("789 Third Street", found.get().getStreet());
    }

    @Test
    void testRepositoryMethodChaining() {
        Address saved1 = addressRepository.save(address);
        Address saved2 = addressRepository.save(address2);

        Optional<Address> found1 = addressRepository.findByAddressId(saved1.getAddressId());
        Optional<Address> found2 = addressRepository.findByAddressId(saved2.getAddressId());

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertNotEquals(found1.get().getAddressId(), found2.get().getAddressId());
    }

    @Test
    void testFindByAddressIdConsistentWithFindById() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> byAddressId = addressRepository.findByAddressId(savedAddress.getAddressId());
        Optional<Address> byId = addressRepository.findById(savedAddress.getAddressId());

        assertTrue(byAddressId.isPresent());
        assertTrue(byId.isPresent());
        assertEquals(byAddressId.get().getAddressId(), byId.get().getAddressId());
    }

    @Test
    void testCountAfterOperations() {
        assertEquals(0, addressRepository.count());

        addressRepository.save(address);
        assertEquals(1, addressRepository.count());

        addressRepository.save(address2);
        assertEquals(2, addressRepository.count());

        addressRepository.deleteAll();
        assertEquals(0, addressRepository.count());
    }

    @Test
    void testFindByAddressIdWithHighIdValue() {
        Optional<Address> foundAddress = addressRepository.findByAddressId(Long.MAX_VALUE);

        assertFalse(foundAddress.isPresent());
    }

    @Test
    void testAddressPersistenceAfterFlush() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        Long addressId = savedAddress.getAddressId();

        testEntityManager.clear();

        Optional<Address> retrieved = addressRepository.findByAddressId(addressId);

        assertTrue(retrieved.isPresent());
        assertEquals(savedAddress.getStreet(), retrieved.get().getStreet());
    }

    @Test
    void testConcurrentAddressSaves() {
        Address saved1 = addressRepository.save(address);
        Address saved2 = addressRepository.save(address2);
        Address saved3 = addressRepository.save(address3);

        List<Address> all = addressRepository.findAll();

        assertEquals(3, all.size());
        assertTrue(all.stream().map(Address::getAddressId).allMatch(id -> id != null));
    }

    @Test
    void testFindByAddressIdDifferentInstances() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> found1 = addressRepository.findByAddressId(savedAddress.getAddressId());
        testEntityManager.clear();
        Optional<Address> found2 = addressRepository.findByAddressId(savedAddress.getAddressId());

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals(found1.get().getAddressId(), found2.get().getAddressId());
    }

    @Test
    void testAddressNotFoundAfterClear() {
        Address savedAddress = testEntityManager.persistAndFlush(address);
        testEntityManager.clear();

        Optional<Address> found = addressRepository.findByAddressId(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testAddressFieldsPreserved() {
        address.setStreet("Street 1");
        address.setLandmark("Landmark 1");
        address.setCity("City 1");
        address.setState("State 1");
        address.setCountry("Country 1");
        address.setPinCode(12345);

        Address saved = addressRepository.save(address);
        testEntityManager.clear();

        Optional<Address> found = addressRepository.findByAddressId(saved.getAddressId());

        assertTrue(found.isPresent());
        Address retrieved = found.get();
        assertEquals("Street 1", retrieved.getStreet());
        assertEquals("Landmark 1", retrieved.getLandmark());
        assertEquals("City 1", retrieved.getCity());
        assertEquals("State 1", retrieved.getState());
        assertEquals("Country 1", retrieved.getCountry());
        assertEquals(12345, retrieved.getPinCode());
    }

    @Test
    void testSaveAndFindAllConsistency() {
        addressRepository.save(address);
        addressRepository.save(address2);

        List<Address> all = addressRepository.findAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(a -> a.getStreet().equals("123 Main Street")));
        assertTrue(all.stream().anyMatch(a -> a.getStreet().equals("456 Second Avenue")));
    }
}
