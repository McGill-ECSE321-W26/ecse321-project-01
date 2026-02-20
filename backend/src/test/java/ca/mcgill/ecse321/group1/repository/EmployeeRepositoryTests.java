package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.model.Person;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeRepositoryTests {

  @Autowired private PersonRepository personRepository;
  @Autowired private EmployeeRepository employeeRepository;
  @Autowired private OrderRepository orderRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    orderRepository.deleteAll();
    employeeRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadEmployee() {
    // Create and save person
    Person person = new Person();
    person.setEmail("employee@example.com");
    person.setPassword("emppassword");
    personRepository.save(person);

    // Create and save employee
    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    String roleID = employee.getRoleID();

    // Read employee
    Employee employeeFromDb = employeeRepository.findByRoleID(roleID);

    // Assertions
    assertNotNull(employeeFromDb);
    assertNotNull(employeeFromDb.getPerson());
    assertEquals(roleID, employeeFromDb.getRoleID());
    assertEquals("employee@example.com", employeeFromDb.getPerson().getEmail());
  }

  @Test
  public void testFindEmployeeByInvalidRoleID() {
    Employee employeeFromDb = employeeRepository.findByRoleID("nonexistent-id");
    assertNull(employeeFromDb);
  }

  @Test
  public void testDeleteEmployee() {
    // Create and save person
    Person person = new Person();
    person.setEmail("emp2@example.com");
    person.setPassword("password");
    personRepository.save(person);

    // Create and save employee
    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    String roleID = employee.getRoleID();

    // Delete employee
    employeeRepository.delete(employee);

    // Read employee
    Employee employeeFromDb = employeeRepository.findByRoleID(roleID);

    // Assertions
    assertNull(employeeFromDb);
  }

  @Test
  @Transactional
  public void testEmployeeOrdersReference() {
    // Create and save person and employee
    Person person = new Person();
    person.setEmail("emp_orders@example.com");
    person.setPassword("password");
    personRepository.save(person);

    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    // Create and save an order linked to the employee
    Order order = new Order();
    order.setOrderStatus(Order.OrderStatus.Preparing);
    order.setAddress("3 Work St");
    order.setEmployee(employee);
    orderRepository.save(order);

    String expectedOrderID = order.getOrderID();
    String roleID = employee.getRoleID();

    // Reload employee and verify the preparingOrders reference
    Employee employeeFromDb = employeeRepository.findByRoleID(roleID);

    assertNotNull(employeeFromDb);
    assertEquals(1, employeeFromDb.numberOfPreparingOrders());
    assertEquals(expectedOrderID, employeeFromDb.getPreparingOrder(0).getOrderID());
  }
}
