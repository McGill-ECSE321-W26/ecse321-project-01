package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerRepositoryTests {

  @Autowired private PersonRepository personRepository;
  @Autowired private CustomerRepository customerRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    customerRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadCustomer() {
    // Create and save person
    Person person = new Person();
    person.setEmail("customer@example.com");
    person.setPassword("password");
    personRepository.save(person);

    // Create and save customer
    Customer customer = new Customer();
    customer.setPerson(person);
    customer.setAddress("123 Main St");
    customer.setLoyaltyPoints(50);
    customerRepository.save(customer);

    String roleID = customer.getRoleID();

    // Read customer
    Customer customerFromDb = customerRepository.findByRoleID(roleID);

    // Assertions
    assertNotNull(customerFromDb);
    assertNotNull(customerFromDb.getPerson());
    assertEquals(roleID, customerFromDb.getRoleID());
    assertEquals("123 Main St", customerFromDb.getAddress());
    assertEquals(50, customerFromDb.getLoyaltyPoints());
    assertEquals("customer@example.com", customerFromDb.getPerson().getEmail());
  }

  @Test
  public void testFindCustomerByInvalidRoleID() {
    Customer customerFromDb = customerRepository.findByRoleID("nonexistent-id");
    assertNull(customerFromDb);
  }

  @Test
  public void testUpdateCustomer() {
    // Create and save person
    Person person = new Person();
    person.setEmail("update@example.com");
    person.setPassword("password");
    personRepository.save(person);

    // Create and save customer
    Customer customer = new Customer();
    customer.setPerson(person);
    customer.setAddress("456 Old Ave");
    customer.setLoyaltyPoints(10);
    customerRepository.save(customer);

    String roleID = customer.getRoleID();

    // Update customer
    customer.setAddress("789 New Blvd");
    customer.setLoyaltyPoints(100);
    customerRepository.save(customer);

    // Read customer
    Customer customerFromDb = customerRepository.findByRoleID(roleID);

    // Assertions
    assertNotNull(customerFromDb);
    assertEquals("789 New Blvd", customerFromDb.getAddress());
    assertEquals(100, customerFromDb.getLoyaltyPoints());
  }

  @Test
  public void testDeleteCustomer() {
    // Create and save person
    Person person = new Person();
    person.setEmail("delete@example.com");
    person.setPassword("password");
    personRepository.save(person);

    // Create and save customer
    Customer customer = new Customer();
    customer.setPerson(person);
    customerRepository.save(customer);

    String roleID = customer.getRoleID();

    // Delete customer
    customerRepository.delete(customer);

    // Read customer
    Customer customerFromDb = customerRepository.findByRoleID(roleID);

    // Assertions
    assertNull(customerFromDb);
  }
}
