package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.Order;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import ca.mcgill.ecse321.group1.repository.OrderRepository;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import org.springframework.stereotype.Service;

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

    public void testCreateValidPersonByInvalidEmail() {
        // Arrange
        String accountID = "account1";
        String email = "bobmcgill.ca";
        String password = "12345678";

        // Act + Assert
        // should have email validation in the insertPerson method in service
        Exception e = assertThrows(Exception.class, () -> service.insertPerson(accountID, email, password));
        assertEquals("Invalid email Address.", e.getMessage());
    }

    public void testCreateValidPersonByInvalidPassword() {
        // Arrange
        String accountID = "account1";
        String email = "bob@mail.mcgill.ca";
        String password = "1";

        // Act + Assert
        // should have email validation in the insertPerson method in service
        Exception e = assertThrows(Exception.class, () -> service.insertPerson(accountID, email, password));
        assertEquals("Password must be at least 8 characters long.", e.getMessage());
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
        String ID = "validID";
        // Default is to return null, so you could omit this
        when(repo.findPersonByPersonID(ID)).thenReturn(null);

        // Act
        // Assert
        Exception e = assertThrows(Exception.class, () -> service.getPersonById(ID));
        assertEquals("There is no person with ID " + ID + ".", e.getMessage());
    }

    @Test
    public void testLogInInvalidEmail() {
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
    public void testLogInInvalidPassword() {
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
    public void testValidLogOut() {
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
    public void testValidSwitchAccount() {
        // Set up
        String ID = "validID";
        // Default is to return null, so you could omit this
        when(repo.findPersonByPersonID(ID)).thenReturn(null);

        // Act
        // Assert
        Exception e = assertThrows(Exception.class, () -> service.getPersonById(ID));
        assertEquals("There is no person with ID " + ID + ".", e.getMessage());
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