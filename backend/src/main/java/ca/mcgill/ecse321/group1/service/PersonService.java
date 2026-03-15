package ca.mcgill.ecse321.group1.service;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.model.PersonRole;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersonService {

  private final PersonRepository personRepository;
  private final CustomerRepository customerRepository;
  private final EmployeeRepository employeeRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public PersonService(
      PersonRepository personRepository,
      CustomerRepository customerRepository,
      EmployeeRepository employeeRepository) {
    this.personRepository = personRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  // Should map to Transfer Objects
  public Iterable<Person> getPeople() {
    return personRepository.findAll();
  }

  public Person getPersonById(String id) {

    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }
    return person;
  }

  @Transactional
  public Person createCustomer(String id, String email, String password, String address) {
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account id cannot be empty.");
    }
    if (email == null || !email.contains("@")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be at least 8 characters.");
    }
    if (personRepository.findPersonByPersonID(id) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Account id already exists.");
    }
    if (personRepository.findPersonByEmail(email) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Email " + email + " is already in use.");
    }
    if (address == null || address.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address cannot be empty.");
    }

    Person person = new Person(id, email, passwordEncoder.encode(password));
    person = personRepository.save(person);

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    customerRepository.save(customer);

    return personRepository.findPersonByPersonID(id);
  }

  @Transactional
  public Person createEmployee(String id, String email, String password) {
    if (id == null || id.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account id cannot be empty.");
    }
    if (email == null || !email.contains("@")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be at least 8 characters.");
    }
    if (personRepository.findPersonByPersonID(id) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Account id already exists.");
    }
    if (personRepository.findPersonByEmail(email) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Email " + email + " is already in use.");
    }

    Person person = new Person(id, email, passwordEncoder.encode(password));
    person = personRepository.save(person);

    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    return personRepository.findPersonByPersonID(id);
  }

  public Person logIn(String email, String password, String role) {
    Person person = personRepository.findPersonByEmail(email);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with email " + email + ".");
    }
    if (!passwordEncoder.matches(password, person.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Wrong password.");
    }
    boolean hasRole =
        person.getRoles().stream().anyMatch(r -> r.getClass().getSimpleName().equals(role));
    if (!hasRole) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Person does not have role " + role + ".");
    }
    return person;
  }

  @Transactional
  public Person updatePassword(String id, String oldPassword, String newPassword) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }
    if (!passwordEncoder.matches(oldPassword, person.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Old password is incorrect.");
    }
    if (newPassword == null || newPassword.length() < 8) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password must be at least 8 characters.");
    }

    person.setPassword(passwordEncoder.encode(newPassword));
    return personRepository.save(person);
  }

  @Transactional
  public Customer updateCustomerAddress(String customerRoleId, String newAddress) {
    Customer customer = customerRepository.findByRoleID(customerRoleId);
    if (customer == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no customer with role ID " + customerRoleId + ".");
    }
    if (newAddress == null || newAddress.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address cannot be empty.");
    }

    customer.setAddress(newAddress);
    return customerRepository.save(customer);
  }

  @Transactional
  public Person addCustomerRoleToEmployee(String id, String address) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }
    if (address == null || address.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Address cannot be empty.");
    }

    // Check that this person actually has an employee role
    boolean hasEmployee = false;
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Customer) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,"This person already has a customer role.");
      }
      if (role instanceof Employee) {
        hasEmployee = true;
      }
    }
    if (!hasEmployee) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This person is not an employee.");
    }

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    customerRepository.save(customer);

    return personRepository.findPersonByPersonID(id);
  }

  @Transactional
  public Person addEmployeeRoleToCustomer(String id) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }

    boolean hasCustomer = false;
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Employee) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,"This person already has an employee role.");
      }
      if (role instanceof Customer) {
        hasCustomer = true;
      }
    }
    if (!hasCustomer) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This person is not a customer.");
    }

    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    return personRepository.findPersonByPersonID(id);
  }

  @Transactional
  public void deleteSelfAccount(String id) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }
    personRepository.delete(person);
  }

  @Transactional
  public void deleteAccount(String id) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with ID " + id + ".");
    }

    // Prevent manager from deleting their own account
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Manager) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The manager cannot delete their own account.");
      }
    }

    personRepository.delete(person);
  }

  public Person getPersonByEmail(String email) {
    Person person = personRepository.findPersonByEmail(email);
    if (person == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"There is no person with email " + email + ".");
    }
    return person;
  }
}
