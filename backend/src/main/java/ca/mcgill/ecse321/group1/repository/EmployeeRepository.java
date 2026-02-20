package ca.mcgill.ecse321.group1.repository;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.group1.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, String> {
    Employee findByEmployeeID(String employeeID);
}
