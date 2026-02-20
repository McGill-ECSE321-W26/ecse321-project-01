package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, String> {
  Employee findByRoleID(String employeeID);
}
