package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Person;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    // Create and save person
    String email = "santiago@example.com";
    String password = "santiagopassword";
    Person person = new Person(); // Using default constructor and setters
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
    String email = "maria@example.com";
    String password = "mariapassword";
    Person person = new Person(); // Using parameterized constructor
    person.setEmail(email);
    person.setPassword(password);
    personRepository.save(person);

    // Delete person
    personRepository.delete(person);

    // Read person
    Person personFromDb = personRepository.findPersonByEmail(email);

    // Assertions
    assertNull(personFromDb);
  }

  @Test
  public void testPersonRolesReference() {
    // Create and save person
    String email = "roles_test@example.com";
    Person person = new Person();
    person.setEmail(email);
    person.setPassword("testpassword");
    personRepository.save(person);

    // Create and save a customer linked to that person
    Customer customer = new Customer();
    customer.setPerson(person);
    customer.setAddress("123 Test St");
    customer.setLoyaltyPoints(0);
    customerRepository.save(customer);

    String expectedRoleID = customer.getRoleID();

    // Reload person and verify the roles reference
    Person personFromDb = personRepository.findPersonByEmail(email);

    assertNotNull(personFromDb);
    assertEquals(1, personFromDb.numberOfRoles());
    assertEquals(expectedRoleID, personFromDb.getRole(0).getRoleID());
  }
}
