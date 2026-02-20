package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  // Should map to Transfer Objects
  public Iterable<Person> getPeople() {
    return personRepository.findAll();
  }

  public Person getPersonById(String id) {
    return personRepository.findPersonByPersonID(id);
  }

  public void insertPerson(Person person) {
    personRepository.save(person);
  }
}
