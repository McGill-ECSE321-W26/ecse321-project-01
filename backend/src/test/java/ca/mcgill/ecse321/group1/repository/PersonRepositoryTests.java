package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.group1.model.Person;

@SpringBootTest
public class PersonRepositoryTests {

    @Autowired
    private PersonRepository personRepository;

    @AfterEach
    public void clearDatabase() {
        personRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadPerson() {
        // Create person
        String personID = "David";
        String email = "david@example.com";
        String password = "davidpassword";
        Person person = new Person();
        person.setPersonID(personID);
        person.setEmail(email);
        person.setPassword(password);

        // Save person
        personRepository.save(person);

        // Read person
        Person personFromDb = personRepository.findPersonByEmail(email);

        // Assertions
        assertNotNull(personFromDb);
        assertEquals(personID, personFromDb.getPersonID());
        assertEquals(email, personFromDb.getEmail());
        assertEquals(password, personFromDb.getPassword());
    }

    @Test
    public void testFindPersonByInvalidEmail() {
        String invalidEmail = "nonexistent@example.com";
        Person personFromDb = personRepository.findPersonByEmail(invalidEmail);
        assertNull(personFromDb);
    }
    
    @Test
    public void testUpdatePerson() {
        // Create and save person
        String personID = "Santiago";
        String email = "santiago@example.com";
        String password = "santiagopassword";
        Person person = new Person(); // Using default constructor and setters
        person.setPersonID(personID);
        person.setEmail(email);
        person.setPassword(password);
        personRepository.save(person);
        
        // Update person
        String newEmail = "santiago_updated@example.com";
        person.setEmail(newEmail);
        personRepository.save(person);
        
        // Read person
        Person personFromDb = personRepository.findPersonByEmail(newEmail);
        
        // Assertions
        assertNotNull(personFromDb);
        assertEquals(newEmail, personFromDb.getEmail());
        assertEquals(password, personFromDb.getPassword());
    }

    @Test
    public void testDeletePerson() {
        // Create and save person
        String personID = "Maria";
        String email = "maria@example.com";
        String password = "mariapassword";
        Person person = new Person(personID, email, password); // Using parameterized constructor
        personRepository.save(person);
        
        // Delete person
        personRepository.delete(person);
        
        // Read person
        Person personFromDb = personRepository.findPersonByEmail(email);
        
        // Assertions
        assertNull(personFromDb);
    }
}
