package ca.mcgill.ecse321.group1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class ManagerRepositoryTests {

  @Autowired private PersonRepository personRepository;
  @Autowired private ManagerRepository managerRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    managerRepository.deleteAll();
    personRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadManager() {
    // Create and save person
    Person person = new Person();
    person.setEmail("manager@example.com");
    person.setPassword("mgrpassword");
    personRepository.save(person);

    // Create and save manager
    Manager manager = new Manager();
    manager.setPerson(person);
    managerRepository.save(manager);

    String roleID = manager.getRoleID();

    // Read manager
    Manager managerFromDb = managerRepository.findByRoleID(roleID);

    // Assertions
    assertNotNull(managerFromDb);
    assertNotNull(managerFromDb.getPerson());
    assertEquals(roleID, managerFromDb.getRoleID());
    assertEquals("manager@example.com", managerFromDb.getPerson().getEmail());
  }

  @Test
  public void testFindManagerByInvalidRoleID() {
    Manager managerFromDb = managerRepository.findByRoleID("nonexistent-id");
    assertNull(managerFromDb);
  }

  @Test
  public void testDeleteManager() {
    // Create and save person
    Person person = new Person();
    person.setEmail("manager2@example.com");
    person.setPassword("password");
    personRepository.save(person);

    // Create and save manager
    Manager manager = new Manager();
    manager.setPerson(person);
    managerRepository.save(manager);

    String roleID = manager.getRoleID();

    // Delete manager
    managerRepository.delete(manager);

    // Read manager
    Manager managerFromDb = managerRepository.findByRoleID(roleID);

    // Assertions
    assertNull(managerFromDb);
  }

  @Test
  @Transactional
  public void testManagerPersonReference() {
    // Create and save person and manager
    Person person = new Person();
    person.setEmail("manager@example.com");
    person.setPassword("password");
    personRepository.save(person);

    String expectedPersonID = person.getPersonID();

    Manager manager = new Manager();
    manager.setPerson(person);
    managerRepository.save(manager);

    String roleID = manager.getRoleID();

    // Reload manager and verify the person reference
    Manager managerFromDb = managerRepository.findByRoleID(roleID);

    assertNotNull(managerFromDb);
    assertNotNull(managerFromDb.getPerson());
    assertEquals(expectedPersonID, managerFromDb.getPerson().getPersonID());
  }
}
