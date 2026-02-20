package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Manager;
import org.springframework.data.repository.CrudRepository;

public interface ManagerRepository extends CrudRepository<Manager, String> {
  Manager findByRoleID(String roleID);
}
