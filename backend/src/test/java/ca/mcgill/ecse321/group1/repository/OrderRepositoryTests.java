package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.group1.model.*;
import java.sql.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderRepositoryTests {
  @Autowired private OrderRepository orderRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private EmployeeRepository employeeRepository;

  @Autowired private PersonRepository personRepository;

  @Autowired private ItemRepository itemRepository;

  @AfterEach
  public void clearDatabase() {
    itemRepository.deleteAll();
    orderRepository.deleteAll();
    customerRepository.deleteAll();
    employeeRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadOrder() {
    // Create person

    Order.OrderStatus status = Order.OrderStatus.Preparing;
    Date orderDate = Date.valueOf("2026-01-01");
    Date deliveryDate = Date.valueOf("2026-01-10");
    String address = "123 fisher lane";
    float loyaltySaving = 15.50f;
    Order orderTest = new Order();
    orderTest.setOrderStatus(status);
    orderTest.setOrderDate(orderDate);
    orderTest.setDeliveryDate(deliveryDate);
    orderTest.setAddress(address);
    orderTest.setLoyaltySaving(loyaltySaving);

    // Save Order
    orderTest = orderRepository.save(orderTest);
    String id = orderTest.getOrderID();

    // Read order from database
    Order orderTestFromDb = orderRepository.findOrderByOrderID(id);

    // Assert correct response
    assertNotNull(orderTestFromDb);
    assertEquals(orderTestFromDb.getOrderStatus(), status);
    assertEquals(orderTestFromDb.getOrderDate(), orderDate);
    assertEquals(orderTestFromDb.getDeliveryDate(), deliveryDate);
    assertEquals(orderTestFromDb.getAddress(), address);
    assertEquals(orderTestFromDb.getLoyaltySaving(), loyaltySaving);
  }

  @Test
  public void testFindOrderByInvalidId() {
    Order result = orderRepository.findOrderByOrderID("nonexistent-id-999");
    assertNull(result);
  }

  @Test
  public void testUpdateOrder() {
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setOrderDate(Date.valueOf("2026-01-01"));
    order.setDeliveryDate(Date.valueOf("2026-01-10"));
    order.setAddress("123 Main St");
    order.setLoyaltySaving(0f);
    order = orderRepository.save(order);

    // Update fields
    order.setOrderStatus(Order.OrderStatus.Delivered);
    order.setAddress("789 New Ave");
    order.setLoyaltySaving(25.00f);
    orderRepository.save(order);

    String id = order.getOrderID();
    Order updatedOrder = orderRepository.findOrderByOrderID(id);
    assertNotNull(updatedOrder);
    assertEquals(Order.OrderStatus.Delivered, updatedOrder.getOrderStatus());
    assertEquals("789 New Ave", updatedOrder.getAddress());
    assertEquals(25.00f, updatedOrder.getLoyaltySaving());
  }

  @Test
  public void testDeleteOrder() {
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Cancelled);
    order.setOrderDate(Date.valueOf("2026-01-01"));
    order.setDeliveryDate(Date.valueOf("2026-01-10"));
    order.setAddress("123 Main St");
    order = orderRepository.save(order);
    String id = order.getOrderID();

    orderRepository.delete(order);

    Order deletedOrder = orderRepository.findOrderByOrderID(id);
    assertNull(deletedOrder);
  }

  @Test
  public void testFindAllOrders() {
    Order order1 = new Order();
    order1.setOrderStatus(Order.OrderStatus.Preparing);
    order1.setAddress("Addr 1");

    Order order2 = new Order();
    order2.setOrderStatus(Order.OrderStatus.Delivered);
    order2.setAddress("Addr 2");

    Order order3 = new Order();
    order3.setOrderStatus(Order.OrderStatus.Cancelled);
    order3.setAddress("Addr 3");

    orderRepository.save(order1);
    orderRepository.save(order2);
    orderRepository.save(order3);

    List<Order> allOrders = (List<Order>) orderRepository.findAll();
    assertNotNull(allOrders);
    assertEquals(3, allOrders.size());
  }

  @Test
  public void testAllOrderStatusEnumValuesPersist() {
    // ensures that there are no issues with the different enum values when stored in DB
    for (Order.OrderStatus status : Order.OrderStatus.values()) {
      Order order = new Order();
      order.setOrderStatus(status);
      order.setAddress("Test Address");
      order = orderRepository.save(order);

      Order fromDb = orderRepository.findOrderByOrderID(order.getOrderID());
      assertNotNull(fromDb);
      assertEquals(status, fromDb.getOrderStatus());
    }
  }

  @Test
  public void testPersistOrderWithZeroLoyaltySaving() {
    // Edge case: no loyalty discount applied
    // in the event where an order has zero loyalty savings, ensure there are no issues
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Delivered);
    order.setOrderDate(Date.valueOf("2026-02-01"));
    order.setDeliveryDate(Date.valueOf("2026-02-05"));
    order.setAddress("456 Oak Ave");
    order.setLoyaltySaving(0f);

    order = orderRepository.save(order);
    String id = order.getOrderID();

    Order fromDb = orderRepository.findOrderByOrderID(id);
    assertNotNull(fromDb);
    assertEquals(0f, fromDb.getLoyaltySaving());
  }

  @Test
  public void testAutoGeneratedIdIsUnique() {
    Order order1 = new Order();
    Order order2 = new Order();
    // no need to assign attributes, just testing the ids

    order1 = orderRepository.save(order1);
    order2 = orderRepository.save(order2);

    assertNotNull(order1.getOrderID());
    assertNotNull(order2.getOrderID());
    assertNotEquals(order1.getOrderID(), order2.getOrderID());
  }

  @Test
  public void testOrderPersistsWithCustomerAndEmployee() {
    // Create and save Person for Customer
    Person customerPerson = new Person();
    customerPerson.setEmail("customer@example.com");
    customerPerson.setPassword("password");
    personRepository.save(customerPerson);

    // Create and save Person for Employee
    Person employeePerson = new Person();
    employeePerson.setEmail("employee@example.com");
    employeePerson.setPassword("password");
    personRepository.save(employeePerson);

    // Create and save Customer
    Customer customer = new Customer();
    customer.setAddress("123 Main St");
    customer.setLoyaltyPoints(0);
    customer.setPerson(customerPerson);
    customer = customerRepository.save(customer);

    // Create and save Employee
    Employee employee = new Employee();
    employee.setPerson(employeePerson);
    employee = employeeRepository.save(employee);

    // Create and save Order linked to both
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setOrderDate(Date.valueOf("2026-01-01"));
    order.setDeliveryDate(Date.valueOf("2026-01-10"));
    order.setAddress("123 Main St");
    order.setLoyaltySaving(10f);
    order.setCustomer(customer);
    order.setEmployee(employee);
    order = orderRepository.save(order);
    String id = order.getOrderID();

    // Read back and assert
    Order fromDb = orderRepository.findOrderByOrderID(id);
    assertNotNull(fromDb);
    assertNotNull(fromDb.getCustomer());
    assertNotNull(fromDb.getEmployee());
    assertEquals(customer.getRoleID(), fromDb.getCustomer().getRoleID());
    assertEquals(employee.getRoleID(), fromDb.getEmployee().getRoleID());
  }

  @Test
  public void testOrderPersistsWithItems() {
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setAddress("123 Main St");
    order = orderRepository.save(order);

    // Create items linked to this order
    Item item1 = new Item();
    item1.setQuantity(2);
    item1.setPrice(20f);
    item1.setOrder(order);
    // reassign item1 to be from the item repo db
    item1 = itemRepository.save(item1);

    Item item2 = new Item();
    item2.setQuantity(5);
    item2.setPrice(50f);
    item2.setOrder(order);
    // reassign item2 to be from the item repo db
    item2 = itemRepository.save(item2);

    // The bidirectional association between order and items allows it to have an accurate
    // numberOfItems value when simply doing item.setOrder(order).
    Order fromDb = orderRepository.findOrderByOrderID(order.getOrderID());
    assertNotNull(fromDb);
    assertTrue(fromDb.hasItems());
    assertEquals(2, fromDb.numberOfItems());

    // this checks if the items referenced when creating the order are still the same in the item
    // repo.
    Item item1FromDb = itemRepository.findItemByItemID(item1.getItemID());
    assertNotNull(item1FromDb);
    assertEquals(2, item1FromDb.getQuantity());
    assertEquals(20f, item1FromDb.getPrice());

    Item item2FromDb = itemRepository.findItemByItemID(item2.getItemID());
    assertNotNull(item2FromDb);
    assertEquals(5, item2FromDb.getQuantity());
    assertEquals(50f, item2FromDb.getPrice());
  }
}
