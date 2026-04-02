package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Employee;
import org.springframework.data.repository.ListCrudRepository;

public interface EmployeeRepository extends ListCrudRepository<Employee, String> {
  Employee findByRoleID(String employeeID);
}
