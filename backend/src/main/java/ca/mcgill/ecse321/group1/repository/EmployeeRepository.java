package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Employee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends ListCrudRepository<Employee, String> {
  Employee findByRoleID(String employeeID);

  @Modifying
  @Query("DELETE FROM Employee e WHERE e.roleID = :roleId")
  void deleteByRoleId(@Param("roleId") String roleId);
}
