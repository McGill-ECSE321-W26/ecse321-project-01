package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Customer;
import org.springframework.data.repository.ListCrudRepository;

public interface CustomerRepository extends ListCrudRepository<Customer, String> {
  Customer findByRoleID(String roleID);
}
