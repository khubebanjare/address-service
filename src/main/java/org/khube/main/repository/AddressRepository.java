package org.khube.main.repository;

import org.khube.main.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Finds an Address by its addressId.
     *
     * @param aLong the addressId to search for
     * @return an Optional containing the Address if found, or empty if not found
     */
    Optional<Address> findByAddressId(Long aLong);
}
