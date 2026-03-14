package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    @InjectMocks
    private PersonService service;

    @SuppressWarnings("null")
    @Test
    public void testCreateValidPerson() {
        // Arrange
        String accountID = "account1";
        String email = "bob@mail.mcgill.ca";
        String password = "12345678";
        Person accountTest = new Person(accountID, email, password);
        when(repo.save(any(Person.class))).thenReturn(accountTest);

        // Act
        Person createdPerson = service.insertPerson(accountID, email, password);

        // Assert
        assertNotNull(createdPerson);
        assertEquals(accountID, createdPerson.getPersonID());
        assertEquals(email, createdPerson.getEmail());
        assertEquals(password, createdPerson.getPassword());
        verify(repo, times(1)).save(any(Person.class));
    }

    @Test
    public void testCreatePersonInvalidEmail() {
        // Arrange
        String accountID = "account1";
        String email = "bobmcgill.ca";
        String password = "12345678";

        // Act + Assert
        // should have email validation in the insertPerson method in service
        Exception e = assertThrows(Exception.class, () -> service.insertPerson(accountID, email, password));
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
        Exception e = assertThrows(Exception.class, () -> service.insertPerson(accountID, email, password));
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


}


/*
* Testing Account:

* Will be made:
createAccount -- test create, test invalid pass, test invalid email
getAccountByID valid and invalid --
getAccounts
*
*
* TOKEN ? COOKIES ?
logIn, test invalid email, test invalid password, test valid login
logOut test valid logout
*
*
getCartByAccountID (is this needed ?)

switchAccount (different account types or accounts)
updateAccountPassword (optional ?)
updateAccountName (optional ?)

*
* */