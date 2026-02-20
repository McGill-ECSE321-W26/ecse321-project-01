package ca.mcgill.ecse321.group1.repository;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.group1.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String>{
    Customer findByRoleID(String roleID);
}