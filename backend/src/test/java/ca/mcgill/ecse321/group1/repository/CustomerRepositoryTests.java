package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.model.Person;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerRepositoryTests {

  @Autowired private PersonRepository personRepository;
  @Autowired private CustomerRepository customerRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private ItemRepository itemRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    itemRepository.deleteAll();
    orderRepository.deleteAll();
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

    // Update customer
    customer.setAddress("789 New Blvd");
    customer.setLoyaltyPoints(100);
    customerRepository.save(customer);

    // Read customer
    String roleID = customer.getRoleID();
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

  @Test
  public void testCustomerOrdersReference() {
    // Create and save person and customer
    Person person = new Person();
    person.setEmail("orders_ref@example.com");
    person.setPassword("password");
    personRepository.save(person);

    Customer customer = new Customer();
    customer.setPerson(person);
    customer.setAddress("1 Order Lane");
    customer.setLoyaltyPoints(0);
    customerRepository.save(customer);

    // Create and save an order linked to the customer
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setAddress("1 Order Lane");
    order.setCustomer(customer);
    orderRepository.save(order);

    String expectedOrderID = order.getOrderID();
    String roleID = customer.getRoleID();

    // Reload customer and verify the orders reference
    Customer customerFromDb = customerRepository.findByRoleID(roleID);

    assertNotNull(customerFromDb);
    assertEquals(1, customerFromDb.numberOfOrders());
    assertEquals(expectedOrderID, customerFromDb.getOrder(0).getOrderID());
  }

  @Test
  public void testCustomerItemsReference() {
    // Create and save person and customer
    Person person = new Person();
    person.setEmail("items_ref@example.com");
    person.setPassword("password");
    personRepository.save(person);

    Customer customer = new Customer();
    customer.setPerson(person);
    customer.setAddress("2 Item Blvd");
    customer.setLoyaltyPoints(0);
    customerRepository.save(customer);

    // Create and save an item linked to the customer
    Item item = new Item();
    item.setQuantity(3);
    item.setPrice(19.99f);
    item.setCustomer(customer);
    itemRepository.save(item);

    String expectedItemID = item.getItemID();
    String roleID = customer.getRoleID();

    // Reload customer and verify the items reference
    Customer customerFromDb = customerRepository.findByRoleID(roleID);

    assertNotNull(customerFromDb);
    assertEquals(1, customerFromDb.numberOfItems());
    assertEquals(expectedItemID, customerFromDb.getItem(0).getItemID());
  }
}
