package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, String> {}
