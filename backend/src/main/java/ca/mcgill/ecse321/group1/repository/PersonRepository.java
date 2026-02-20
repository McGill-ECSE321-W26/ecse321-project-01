package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, String> {
    Person findPersonByPersonID(String id);
    Person findPersonByEmail(String email);
}
