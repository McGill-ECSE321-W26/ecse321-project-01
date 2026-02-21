package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class PersonRepositoryTests {

  @Autowired private PersonRepository personRepository;
  @Autowired private CustomerRepository customerRepository;

  @AfterEach
  public void clearDatabase() {
    customerRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadPerson() {
    // Create person
    String email = "david@example.com";
    String password = "davidpassword";
    Person person = new Person();
    person.setEmail(email);
    person.setPassword(password);

    // Save person
    personRepository.save(person);

    // Read person
    Person personFromDb = personRepository.findPersonByEmail(email);

    // Assertions
    assertNotNull(personFromDb);
    assertEquals(person.getPersonID(), personFromDb.getPersonID());
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
    // Create and save
    String email = "santiago@example.com";
    String password = "santiagopassword";
    Person person = new Person(); // Using default constructor and setters
    person.setEmail(email);
    person.setPassword(password);
    personRepository.save(person);

    // Update
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
    // Create
    String email = "maria@example.com";
    String password = "mariapassword";
    Person person = new Person(); // Using parameterized constructor
    person.setEmail(email);
    person.setPassword(password);
    personRepository.save(person);

    // Delete
    personRepository.delete(person);

    // Read
    Person personFromDb = personRepository.findPersonByEmail(email);

    // Assertions
    assertNull(personFromDb);
  }

  @Test
  @Transactional
  public void testPersistAndLoadPersonWithRole() {
    // Create
    String email = "roletest@example.com";
    String password = "roletestpassword";
    Person person = new Person();
    person.setEmail(email);
    person.setPassword(password);
    personRepository.save(person);

    // Create and save a Customer role (PersonRole) linked to the person
    Customer customer = new Customer();
    customer.setPerson(person);
    customerRepository.save(customer);

    // Read person back from DB
    Person personFromDb = personRepository.findPersonByEmail(email);

    // Assertions object and reference
    String roleID = customer.getRoleID();
    assertNotNull(personFromDb);
    assertEquals(1, personFromDb.getRoles().size());
    assertEquals(roleID, personFromDb.getRoles().getFirst().getRoleID());
  }
}
