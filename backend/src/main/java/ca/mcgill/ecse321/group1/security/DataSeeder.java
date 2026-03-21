package ca.mcgill.ecse321.group1.security;

import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.ManagerRepository;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Runs on every server startup and ensures a default Manager account exists
// If the manager email already exists in the database, this does nothing
// Credentials are hard-coded for development for now
@Component
public class DataSeeder implements CommandLineRunner {

  private static final String MANAGER_EMAIL = "manager@admin.com";
  private static final String MANAGER_PASSWORD = "manager123";

  private final PersonRepository personRepository;
  private final ManagerRepository managerRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public DataSeeder(PersonRepository personRepository, ManagerRepository managerRepository) {
    this.personRepository = personRepository;
    this.managerRepository = managerRepository;
  }

  @Override
  public void run(String... args) {
    // Skip if the manager account already exists
    if (personRepository.findByEmail(MANAGER_EMAIL) != null) {
      return;
    }

    // Create the person with a BCrypt-hashed password
    Person person = new Person();
    person.setEmail(MANAGER_EMAIL);
    person.setPassword(passwordEncoder.encode(MANAGER_PASSWORD));
    person = personRepository.save(person);

    // Assign the Manager role to the person
    Manager manager = new Manager();
    manager.setPerson(person);
    managerRepository.save(manager);
  }
}
