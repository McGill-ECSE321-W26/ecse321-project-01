package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Should map to Transfer Objects
    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person getPersonById(String username) {
        return personRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Username " + username + " not found"));
    }

    public void insertPerson(Person person) {
        personRepository.save(person);
    }
}
