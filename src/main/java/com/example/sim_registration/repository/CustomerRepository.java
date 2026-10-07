package com.example.sim_registration.repository;

import com.example.sim_registration.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNinHash(String ninHash);

}
