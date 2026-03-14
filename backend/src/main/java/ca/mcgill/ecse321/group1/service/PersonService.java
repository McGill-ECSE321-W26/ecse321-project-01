package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.model.PersonRole;
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

    Person person = personRepository.findPersonByPersonID(id);
    if(person == null){
      throw new IllegalArgumentException("There is no person with ID " + id + ".");
    }
    return person;
  }

  public Person insertPerson(String ID, String email, String password) {
    if (ID == null || ID.isBlank()) {
      throw new IllegalArgumentException("Account ID cannot be empty.");
    }
    if (email == null || !email.contains("@")) {
      throw new IllegalArgumentException("Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters.");
    }

    if (personRepository.findPersonByPersonID(ID) != null) {
      throw new IllegalArgumentException("Account ID already exists.");
    }

    Person p = new Person(ID, email, password);
    personRepository.save(p);
    return p;
  }

  public Person logIn(String email, String password, String role) {
    Person person = personRepository.findPersonByEmail(email);
    if (person == null) {
      throw new IllegalArgumentException("There is no person with email " + email + ".");
    }
    if (!person.getPassword().equals(password)) {
      throw new IllegalArgumentException("Wrong password.");
    }
    boolean hasRole = person.getRoles()
            .stream()
            .anyMatch(r -> r.getClass().getSimpleName().equals(role));
    if (!hasRole) {
      throw new IllegalArgumentException("Person does not have role " + role + ".");
    }
    return person;
  }


}

