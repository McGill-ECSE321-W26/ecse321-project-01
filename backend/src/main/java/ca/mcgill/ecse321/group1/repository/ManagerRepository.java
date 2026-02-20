package ca.mcgill.ecse321.group1.repository;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.group1.model.Manager;

public interface ManagerRepository extends CrudRepository<Manager, String> {
    Manager findByRoleID(String roleID);
}
