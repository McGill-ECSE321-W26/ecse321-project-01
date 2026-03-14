package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.PersonRepository;


@SpringBootTest
public class AccountServiceTests {
    @Mock
    private PersonRepository repo;
    @Mock
    private CustomerRepository customerRepo;
    @Mock
    private EmployeeRepository employeeRepo;
    @InjectMocks
    private PersonService service;



    @SuppressWarnings("null")
    @Test
    public void testCreateValidEmployee() {
        String accountID = "account1";
        String email = "bob@mail.mcgill.ca";
        String password = "12345678";
        Person accountTest = new Person(accountID, email, password);

        when(repo.save(any(Person.class))).thenReturn(accountTest);
        when(repo.findPersonByPersonID(accountID)).thenReturn(accountTest); // final fetch at end of createEmployee

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

        when(repo.save(any(Person.class))).thenReturn(accountTest);
        when(repo.findPersonByPersonID(accountID)).thenReturn(accountTest); // final fetch

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
        String address = "";  // blank address

        Exception e = assertThrows(Exception.class,
                () -> service.createCustomer(accountID, email, password, address));
        assertEquals("Address cannot be empty.", e.getMessage());
    }

    @Test
    public void testCreatePersonInvalidEmail() {
        // Arrange
        String accountID = "account1";
        String email = "bobmcgill.ca";
        String password = "12345678";

        // Act + Assert
        // should have email validation in the insertPerson method in service
        Exception e = assertThrows(Exception.class, () -> service.createEmployee(accountID, email, password));
        assertEquals("Invalid email address.", e.getMessage());
    }

    @Test
    public void testCreatePersonInvalidPassword() {
        // Arrange
        String accountID = "account1";
        String email = "bob@mail.mcgill.ca";
        String password = "1";

        // Act + Assert
        // should have email validation in the insertPerson method in service
        Exception e = assertThrows(Exception.class, () -> service.createEmployee(accountID, email, password));
        assertEquals("Password must be at least 8 characters.", e.getMessage());
    }

    @Test
    public void testReadPersonByValidId() {
        // Arrange
        String ID = "validID";
        Person charlie = new Person(ID, "charlie@mail.mcgill.ca", "password123");
        when(repo.findPersonByPersonID(ID)).thenReturn(charlie);

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
        when(repo.findPersonByPersonID(ID)).thenReturn(null);

        // Act
        // Assert
        Exception e = assertThrows(Exception.class, () -> service.getPersonById(ID));
        assertEquals("There is no person with ID " + ID + ".", e.getMessage());
    }

    @Test
    public void testValidLogIn() {
        // Set up
        String email = "charlie@mail.mcgill.ca";
        String password = "12345678";

        Person charlie = new Person("validID", email, password);
        Customer customerRole = new Customer();
        charlie.addRole(customerRole);

        // Act
        when(repo.findPersonByEmail(email)).thenReturn(charlie);
        Person person = service.logIn(email, password, "Customer");

        // Assert
        assertNotNull(person);
        assertEquals(charlie.getPersonID(), person.getPersonID());
        assertEquals(charlie.getEmail(), person.getEmail());
        assertEquals(charlie.getPassword(), person.getPassword());
    }

    @Test
    public void testLogInValidEmployee() {
        // Arrange
        String email = "bob@mail.com";
        String password = "password123";
        Person bob = new Person("id1", email, password);
        Employee employeeRole = new Employee();
        bob.addRole(employeeRole);
        when(repo.findPersonByEmail(email)).thenReturn(bob);

        // Act
        Person result = service.logIn(email, password, "Employee");

        // Assert
        assertNotNull(result);
        assertEquals(bob.getPersonID(), result.getPersonID());
        assertEquals(bob.getEmail(), result.getEmail());
        assertEquals(bob.getPassword(), result.getPassword());
    }

    @Test
    public void testLogInValidManager() {
        // Arrange
        String email = "bob@mail.com";
        String password = "password123";
        Person bob = new Person("id1", email, password);
        Manager managerRole = new Manager();
        bob.addRole(managerRole);
        when(repo.findPersonByEmail(email)).thenReturn(bob);

        // Act
        Person result = service.logIn(email, password, "Manager");

        // Assert
        assertNotNull(result);
        assertEquals(bob.getPersonID(), result.getPersonID());
        assertEquals(bob.getEmail(), result.getEmail());
        assertEquals(bob.getPassword(), result.getPassword());
    }

    @Test
    public void testLogInEmailNotFound() {
        // Set up
        String email = "bob@mail.mcgill.ca";
        String password = "12345678";

        // Act
        // Assert
        Exception e = assertThrows(Exception.class, () -> service.logIn(email, password, "Customer"));
        assertEquals("There is no person with email " + email + ".", e.getMessage());
    }

    @Test
    public void testLogInWrongPassword() {
        String email = "charlie@mail.mcgill.ca";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";

        Person charlie = new Person("validID", email, correctPassword);
        Customer customerRole = new Customer();
        charlie.addRole(customerRole);

        when(repo.findPersonByEmail(email)).thenReturn(charlie);

        Exception e = assertThrows(Exception.class,
                () -> service.logIn(email, wrongPassword, "Customer"));
        assertEquals("Wrong password.", e.getMessage());
    }


    @Test
    public void testLogInInvalidRole() {
        // bob is only a Customer, tries to log in as Manager
        Person bob = new Person("id1", "bob@mail.com", "password123");
        Customer customerRole = new Customer();
        bob.addRole(customerRole);
        when(repo.findPersonByEmail("bob@mail.com")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.logIn("bob@mail.com", "password123", "Manager"));
        assertEquals("Person does not have role Manager.", e.getMessage());
    }

    @Test
    public void testLogInRoleStringInvalid() {
        // completely garbage role string
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByEmail("bob@mail.com")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.logIn("bob@mail.com", "password123", "Astronaut"));
        assertEquals("Person does not have role Astronaut.", e.getMessage());
    }




    @Test
    public void testValidUpdatePassword() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);
        // mock save to return bob AFTER password is changed
        when(repo.save(any(Person.class))).thenAnswer(i -> i.getArgument(0));

        Person result = service.updatePassword("id1", "password123", "newPassword123");

        assertNotNull(result);
        assertEquals("newPassword123", result.getPassword());
    }

    @Test
    public void testInvalidUpdatePasswordOldPassword() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.updatePassword("id1", "wrongOldPassword", "newPassword123"));
        assertEquals("Old password is incorrect.", e.getMessage());
    }

    @Test
    public void testInvalidUpdatePasswordLength() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.updatePassword("id1", "password123", "short"));
        assertEquals("New password must be at least 8 characters.", e.getMessage());
    }

    @Test
    public void testValidUpdateCustomerAddress() {
        Customer customer = new Customer();
        customer.setAddress("123 Old St");
        when(customerRepo.findByRoleID("roleId1")).thenReturn(customer);
        when(customerRepo.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.updateCustomerAddress("roleId1", "456 New Ave");

        assertNotNull(result);
        assertEquals("456 New Ave", result.getAddress());
    }

    @Test
    public void testInvalidUpdateCustomerAddressEmpty() {
        Customer customer = new Customer();
        customer.setAddress("123 Old St");
        when(customerRepo.findByRoleID("roleId1")).thenReturn(customer);

        Exception e = assertThrows(Exception.class,
                () -> service.updateCustomerAddress("roleId1", ""));
        assertEquals("Address cannot be empty.", e.getMessage());
    }

    @Test
    public void testValidAddCustomerRoleToEmployee() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        Employee employeeRole = new Employee();
        bob.addRole(employeeRole);
        when(repo.findPersonByPersonID("id1")).thenReturn(bob); // final fetch
        when(customerRepo.save(any(Customer.class))).thenReturn(new Customer());

        Person result = service.addCustomerRoleToEmployee("id1", "123 Main St");

        assertNotNull(result);
    }

    @Test
    public void testInvalidAddCustomerRoleToEmployeeAlreadyExists() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        Employee employeeRole = new Employee();
        Customer customerRole = new Customer();
        bob.addRole(employeeRole);
        bob.addRole(customerRole);  // already has customer role
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.addCustomerRoleToEmployee("id1", "123 Main St"));
        assertEquals("This person already has a customer role.", e.getMessage());
    }

    @Test
    public void testValidAddEmployeeRoleToCustomer() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        Customer customerRole = new Customer();
        bob.addRole(customerRole);
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);
        when(employeeRepo.save(any(Employee.class))).thenReturn(new Employee());

        Person result = service.addEmployeeRoleToCustomer("id1");

        assertNotNull(result);
    }

    @Test
    public void testInvalidAddEmployeeRoleToCustomerAlreadyExists() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        Customer customerRole = new Customer();
        Employee employeeRole = new Employee();
        bob.addRole(customerRole);
        bob.addRole(employeeRole);  // already has employee role
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        Exception e = assertThrows(Exception.class,
                () -> service.addEmployeeRoleToCustomer("id1"));
        assertEquals("This person already has an employee role.", e.getMessage());
    }

    @Test
    public void testValidDeleteSelfAccount() {
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        // just verify it doesn't throw and delete is called
        assertDoesNotThrow(() -> service.deleteSelfAccount("id1"));
        verify(repo, times(1)).delete(bob);
    }

    @Test
    public void testValidManagerDeleteAccount() {
        // just a regular person with no manager role
        Person bob = new Person("id1", "bob@mail.com", "password123");
        when(repo.findPersonByPersonID("id1")).thenReturn(bob);

        assertDoesNotThrow(() -> service.deleteAccount("id1"));
        verify(repo, times(1)).delete(bob);
    }

    @Test
    public void testInvalidManagerDeleteAccountNotFound() {
        // no when() needed, repo returns null by default

        Exception e = assertThrows(Exception.class,
                () -> service.deleteAccount("nonExistentId"));
        assertEquals("There is no person with ID nonExistentId.", e.getMessage());
    }

    @Test
    public void testInvalidManagerDeleteSelfAccount() {
        Person manager = new Person("managerId", "manager@mail.com", "password123");
        Manager managerRole = new Manager();
        manager.addRole(managerRole);
        when(repo.findPersonByPersonID("managerId")).thenReturn(manager);

        Exception e = assertThrows(Exception.class,
                () -> service.deleteAccount("managerId"));
        assertEquals("The manager cannot delete their own account.", e.getMessage());
    }

}
