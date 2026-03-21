package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Person;
import org.springframework.data.repository.ListCrudRepository;

public interface PersonRepository extends ListCrudRepository<Person, String> {
  Person findByPersonID(String personID);

  Person findByEmail(String email);
}
