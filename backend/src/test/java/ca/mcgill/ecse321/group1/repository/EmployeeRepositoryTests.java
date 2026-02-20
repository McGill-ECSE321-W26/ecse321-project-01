package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeRepositoryTests {

    @Autowired private PersonRepository personRepository;
    @Autowired private EmployeeRepository employeeRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        employeeRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadEmployee() {
        // Create and save person
        Person person = new Person("emp_person_1", "emp@example.com", "emppassword");
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
        assertEquals("emp@example.com", employeeFromDb.getPerson().getEmail());
    }

    @Test
    public void testFindEmployeeByInvalidRoleID() {
        Employee employeeFromDb = employeeRepository.findByRoleID("nonexistent-id");
        assertNull(employeeFromDb);
    }

    @Test
    public void testDeleteEmployee() {
        // Create and save person
        Person person = new Person("emp_person_2", "emp2@example.com", "password");
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
}
