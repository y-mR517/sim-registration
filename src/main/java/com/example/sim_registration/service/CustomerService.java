package com.example.sim_registration.service;

import com.example.sim_registration.entity.Customer;
import com.example.sim_registration.repository.CustomerRepository;

import java.util.List;

public interface CustomerService {
    public Customer createCustomer(Customer customer);
    public List<Customer> getAllCustomers();
}
