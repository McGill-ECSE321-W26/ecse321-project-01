package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class AccountServiceTests {
  @Mock private PersonRepository personRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private EmployeeRepository employeeRepository;
  @InjectMocks private PersonService service;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Test
  public void testCreateValidEmployee() {
    String accountID = "account1";
    String email = "bob@mail.mcgill.ca";
    String password = "12345678";
    Person accountTest = new Person(accountID, email, password);

    when(personRepository.findPersonByPersonID(accountID)).thenReturn(null).thenReturn(accountTest);
    when(personRepository.save(any(Person.class))).thenReturn(accountTest);

    Person createdPerson = service.createEmployee(accountID, email, password);

    assertNotNull(createdPerson);
    assertEquals(accountID, createdPerson.getPersonID());
    assertEquals(email, createdPerson.getEmail());
  }

  @Test
  public void testCreateValidCustomer() {
    String accountID = "account1";
    String email = "bob@mail.mcgill.ca";
    String password = "12345678";
    String address = "123 Main St";
    Person accountTest = new Person(accountID, email, password);

    when(personRepository.findPersonByPersonID(accountID)).thenReturn(null).thenReturn(accountTest);
    when(personRepository.save(any(Person.class))).thenReturn(accountTest);

    Person createdPerson = service.createCustomer(accountID, email, password, address);

    assertNotNull(createdPerson);
    assertEquals(accountID, createdPerson.getPersonID());
    assertEquals(email, createdPerson.getEmail());
  }

  @Test
  public void testCreateCustomerInvalidAddress() {
    String accountID = "account1";
    String email = "bob@mail.mcgill.ca";
    String password = "12345678";
    String address = ""; // blank address

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.createCustomer(accountID, email, password, address));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Address cannot be empty.", e.getReason());
  }

  @Test
  public void testCreatePersonInvalidEmail() {
    // Arrange
    String accountID = "account1";
    String email = "bobmcgill.ca";
    String password = "12345678";

    // Act + Assert
    // should have email validation in the insertPerson method in service
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.createEmployee(accountID, email, password));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Invalid email address.", e.getReason());
  }

  @Test
  public void testCreatePersonInvalidPassword() {
    // Arrange
    String accountID = "account1";
    String email = "bob@mail.mcgill.ca";
    String password = "1";

    // Act + Assert
    // should have email validation in the insertPerson method in service
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.createEmployee(accountID, email, password));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Password must be at least 8 characters.", e.getReason());
  }

  @Test
  public void testReadPersonByValidId() {
    // Arrange
    String ID = "validID";
    Person charlie = new Person(ID, "charlie@mail.mcgill.ca", "password123");
    when(personRepository.findPersonByPersonID(ID)).thenReturn(charlie);

    // Act
    Person person = service.getPersonById(ID);

    // Assert
    assertNotNull(person);
    assertEquals(charlie.getPersonID(), person.getPersonID());
    assertEquals(charlie.getEmail(), person.getEmail());
    assertEquals(charlie.getPassword(), person.getPassword());
  }

  @Test
  public void testReadPersonByInvalidId() {
    // Set up
    String ID = "validID";
    // Default is to return null, so you could omit this
    when(personRepository.findPersonByPersonID(ID)).thenReturn(null);

    // Act
    // Assert
    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> service.getPersonById(ID));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    assertEquals("There is no person with ID " + ID + ".", e.getReason());
  }

  @Test
  public void testGetPersonByValidEmail() {
    String email = "bob@mail.com";
    Person bob = new Person("id1", email, "password123");
    when(personRepository.findPersonByEmail(email)).thenReturn(bob);

    Person result = service.getPersonByEmail(email);

    assertNotNull(result);
    assertEquals(bob.getPersonID(), result.getPersonID());
    assertEquals(bob.getEmail(), result.getEmail());
  }

  @Test
  public void testGetPersonByInvalidEmail() {
    when(personRepository.findPersonByEmail(any())).thenReturn(null);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> service.getPersonByEmail("ghost@mail.com"));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    assertEquals("There is no person with email ghost@mail.com.", e.getReason());
  }

  @Test
  public void testValidLogIn() {
    // Arrange
    String email = "charlie@mail.mcgill.ca";
    String password = "12345678";

    // Act
    Person charlie = new Person("validID", email, passwordEncoder.encode(password));
    Customer customerRole = new Customer();
    charlie.addRole(customerRole);
    when(personRepository.findPersonByEmail(email)).thenReturn(charlie);

    Person person = service.logIn(email, password, "Customer");

    assertNotNull(person);
    assertEquals(charlie.getPersonID(), person.getPersonID());
    assertEquals(charlie.getEmail(), person.getEmail());
  }

  @Test
  public void testLogInValidEmployee() {
    // Arrange
    String email = "bob@mail.com";
    String password = "password123";
    Person bob = new Person("id1", email, passwordEncoder.encode(password));
    Employee employeeRole = new Employee();
    bob.addRole(employeeRole);
    when(personRepository.findPersonByEmail(email)).thenReturn(bob);

    // Act
    Person result = service.logIn(email, password, "Employee");

    // Assert
    assertNotNull(result);
    assertEquals(bob.getPersonID(), result.getPersonID());
    assertEquals(bob.getEmail(), result.getEmail());
  }

  @Test
  public void testLogInValidManager() {
    // Arrange
    String email = "bob@mail.com";
    String password = "password123";
    Person bob = new Person("id1", email, passwordEncoder.encode(password));
    Manager managerRole = new Manager();
    bob.addRole(managerRole);
    when(personRepository.findPersonByEmail(email)).thenReturn(bob);

    // Act
    Person result = service.logIn(email, password, "Manager");

    // Assert
    assertNotNull(result);
    assertEquals(bob.getPersonID(), result.getPersonID());
    assertEquals(bob.getEmail(), result.getEmail());
  }

  @Test
  public void testLogInEmailNotFound() {
    // Set up
    String email = "bob@mail.mcgill.ca";
    String password = "12345678";

    // Act
    // Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> service.logIn(email, password, "Customer"));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    assertEquals("There is no person with email " + email + ".", e.getReason());
  }

  @Test
  public void testLogInWrongPassword() {
    String email = "charlie@mail.mcgill.ca";
    String correctPassword = "correctPassword";
    Person charlie = new Person("validID", email, passwordEncoder.encode(correctPassword));
    Customer customerRole = new Customer();
    charlie.addRole(customerRole);
    when(personRepository.findPersonByEmail(email)).thenReturn(charlie);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> service.logIn(email, "wrongPassword", "Customer"));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    assertEquals("Wrong password.", e.getReason());
  }

  @Test
  public void testLogInInvalidRole() {
    // bob is only a Customer, tries to log in as Manager
    Person bob = new Person("id1", "bob@mail.com", passwordEncoder.encode("password123"));
    Customer customerRole = new Customer();
    bob.addRole(customerRole);
    when(personRepository.findPersonByEmail("bob@mail.com")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.logIn("bob@mail.com", "password123", "Manager"));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    assertEquals("Person does not have role Manager.", e.getReason());
  }

  @Test
  public void testLogInRoleStringInvalid() {
    // completely garbage role string
    Person bob = new Person("id1", "bob@mail.com", passwordEncoder.encode("password123"));
    when(personRepository.findPersonByEmail("bob@mail.com")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.logIn("bob@mail.com", "password123", "Astronaut"));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    assertEquals("Person does not have role Astronaut.", e.getReason());
  }

  @Test
  public void testValidUpdatePassword() {
    String oldPassword = "password123";
    Person bob = new Person("id1", "bob@mail.com", passwordEncoder.encode(oldPassword));
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);
    // mock save to return bob AFTER password is changed
    when(personRepository.save(any(Person.class))).thenAnswer(i -> i.getArgument(0));

    Person result = service.updatePassword("id1", "password123", "newPassword123");

    assertNotNull(result);
    assertTrue(passwordEncoder.matches("newPassword123", result.getPassword()));
  }

  @Test
  public void testInvalidUpdatePasswordOldPassword() {
    Person bob = new Person("id1", "bob@mail.com", passwordEncoder.encode("password123"));
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.updatePassword("id1", "wrongOldPassword", "newPassword123"));
    assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    assertEquals("Old password is incorrect.", e.getReason());
  }

  @Test
  public void testInvalidUpdatePasswordLength() {
    Person bob = new Person("id1", "bob@mail.com", passwordEncoder.encode("password123"));
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.updatePassword("id1", "password123", "short"));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("New password must be at least 8 characters.", e.getReason());
  }

  @Test
  public void testValidUpdateCustomerAddress() {
    Customer customer = new Customer();
    customer.setAddress("123 Old St");
    when(customerRepository.findByRoleID("roleId1")).thenReturn(customer);
    when(customerRepository.save(any(Customer.class))).thenReturn(customer);

    Customer result = service.updateCustomerAddress("roleId1", "456 New Ave");

    assertNotNull(result);
    assertEquals("456 New Ave", result.getAddress());
  }

  @Test
  public void testInvalidUpdateCustomerAddressEmpty() {
    Customer customer = new Customer();
    customer.setAddress("123 Old St");
    when(customerRepository.findByRoleID("roleId1")).thenReturn(customer);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> service.updateCustomerAddress("roleId1", ""));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("Address cannot be empty.", e.getReason());
  }

  @Test
  public void testValidAddCustomerRoleToEmployee() {
    Person bob = new Person("id1", "bob@mail.com", "password123");
    Employee employeeRole = new Employee();
    bob.addRole(employeeRole);
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob); // final fetch
    when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());

    Person result = service.addCustomerRoleToEmployee("id1", "123 Main St");

    assertNotNull(result);
  }

  @Test
  public void testInvalidAddCustomerRoleToEmployeeAlreadyExists() {
    Person bob = new Person("id1", "bob@mail.com", "password123");
    Employee employeeRole = new Employee();
    Customer customerRole = new Customer();
    bob.addRole(employeeRole);
    bob.addRole(customerRole); // already has customer role
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> service.addCustomerRoleToEmployee("id1", "123 Main St"));
    assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    assertEquals("This person already has a customer role.", e.getReason());
  }

  @Test
  public void testValidAddEmployeeRoleToCustomer() {
    Person bob = new Person("id1", "bob@mail.com", "password123");
    Customer customerRole = new Customer();
    bob.addRole(customerRole);
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);
    when(employeeRepository.save(any(Employee.class))).thenReturn(new Employee());

    Person result = service.addEmployeeRoleToCustomer("id1");

    assertNotNull(result);
  }

  @Test
  public void testInvalidAddEmployeeRoleToCustomerAlreadyExists() {
    Person bob = new Person("id1", "bob@mail.com", "password123");
    Customer customerRole = new Customer();
    Employee employeeRole = new Employee();
    bob.addRole(customerRole);
    bob.addRole(employeeRole); // already has employee role
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> service.addEmployeeRoleToCustomer("id1"));
    assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    assertEquals("This person already has an employee role.", e.getReason());
  }

  @Test
  public void testValidDeleteSelfAccount() {
    Person bob = new Person("id1", "bob@mail.com", "password123");
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    // just verify it doesn't throw and delete is called
    assertDoesNotThrow(() -> service.deleteSelfAccount("id1"));
    verify(personRepository, times(1)).delete(bob);
  }

  @Test
  public void testValidManagerDeleteAccount() {
    // just a regular person with no manager role
    Person bob = new Person("id1", "bob@mail.com", "password123");
    when(personRepository.findPersonByPersonID("id1")).thenReturn(bob);

    assertDoesNotThrow(() -> service.deleteAccount("id1"));
    verify(personRepository, times(1)).delete(bob);
  }

  @Test
  public void testInvalidManagerDeleteAccountNotFound() {
    // no when() needed, repo returns null by default

    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> service.deleteAccount("nonExistentId"));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    assertEquals("There is no person with ID nonExistentId.", e.getReason());
  }

  @Test
  public void testInvalidManagerDeleteSelfAccount() {
    Person manager = new Person("managerId", "manager@mail.com", "password123");
    Manager managerRole = new Manager();
    manager.addRole(managerRole);
    when(personRepository.findPersonByPersonID("managerId")).thenReturn(manager);

    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> service.deleteAccount("managerId"));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    assertEquals("The manager cannot delete their own account.", e.getReason());
  }
}
