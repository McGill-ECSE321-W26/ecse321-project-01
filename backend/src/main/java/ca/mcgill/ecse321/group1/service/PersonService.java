package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.model.PersonRole;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import ca.mcgill.ecse321.group1.repository.OrderRepository;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersonService {

  private final PersonRepository personRepository;
  private final CustomerRepository customerRepository;
  private final EmployeeRepository employeeRepository;
  private final OrderRepository orderRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public PersonService(
      PersonRepository personRepository,
      CustomerRepository customerRepository,
      EmployeeRepository employeeRepository,
      OrderRepository orderRepository) {
    this.personRepository = personRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.orderRepository = orderRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  private Person findPersonOrThrow(String id) {
    Person person = personRepository.findByPersonID(id);
    if (person == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no person with ID " + id + ".");
    }
    return person;
  }

  private void validateNewPerson(String email, String password) {
    if (email == null || !email.contains("@")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Password must be at least 8 characters.");
    }
    if (personRepository.findByEmail(email) != null) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Email " + email + " is already in use.");
    }
  }

  @Transactional(readOnly = true)
  public List<Person> getPeople() {
    return personRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Employee> getEmployees() {
    return employeeRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Customer> getCustomers() {
    return customerRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Customer getCustomerById(String id) {
    Customer customer = customerRepository.findByRoleID(id);
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no customer with ID " + id + ".");
    }
    return customer;
  }

  @Transactional(readOnly = true)
  public Person getPersonById(String id) {
    return findPersonOrThrow(id);
  }

  @Transactional
  public Customer createCustomer(String email, String password, String address) {
    validateNewPerson(email, password);
    if (address == null || address.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be empty.");
    }

    Person person = new Person();
    person.setEmail(email);
    person.setPassword(passwordEncoder.encode(password));
    person = personRepository.save(person);

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    return customerRepository.save(customer);
  }

  @Transactional
  public Employee createEmployee(String email, String password, String address) {
    validateNewPerson(email, password);
    if (address == null || address.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be empty.");
    }

    Person person = new Person();
    person.setEmail(email);
    person.setPassword(passwordEncoder.encode(password));
    person = personRepository.save(person);

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    customerRepository.save(customer);

    Employee employee = new Employee();
    employee.setPerson(person);
    return employeeRepository.save(employee);
  }

  @Transactional(readOnly = true)
  public Person logIn(String email, String password, String role) {
    Person person = personRepository.findByEmail(email);
    if (person == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no person with email " + email + ".");
    }
    if (!passwordEncoder.matches(password, person.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password.");
    }
    // Match the requested role string against the concrete class name of each PersonRole
    boolean hasRole =
        person.getRoles().stream().anyMatch(r -> r.getClass().getSimpleName().equals(role));
    if (!hasRole) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Person does not have role " + role + ".");
    }
    return person;
  }

  @Transactional
  public Person updatePassword(String id, String oldPassword, String newPassword) {
    Person person = findPersonOrThrow(id);
    if (!passwordEncoder.matches(oldPassword, person.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect.");
    }
    if (newPassword == null || newPassword.length() < 8) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "New password must be at least 8 characters.");
    }

    person.setPassword(passwordEncoder.encode(newPassword));
    return personRepository.save(person);
  }

  @Transactional
  public Customer updateCustomerAddress(String personId, String newAddress) {
    Person person = findPersonOrThrow(personId);
    if (newAddress == null || newAddress.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be empty.");
    }

    // Find the Customer role among the person's roles via pattern matching
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Customer customer) {
        customer.setAddress(newAddress);
        return customerRepository.save(customer);
      }
    }
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, "Person with ID " + personId + " does not have a customer role.");
  }

  @Transactional
  public Person addCustomerRoleToEmployee(String id, String address) {
    Person person = findPersonOrThrow(id);
    if (address == null || address.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be empty.");
    }

    // Check that this person actually has an employee role
    boolean hasEmployee = false;
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Customer) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "This person already has a customer role.");
      }
      if (role instanceof Employee) {
        hasEmployee = true;
      }
    }
    if (!hasEmployee) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This person is not an employee.");
    }

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    customerRepository.save(customer);

    return personRepository.findByPersonID(id);
  }

  @Transactional
  public Person addEmployeeRoleToCustomer(String id) {
    Person person = findPersonOrThrow(id);
    boolean hasCustomer = false;
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Employee) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "This person already has an employee role.");
      }
      if (role instanceof Customer) {
        hasCustomer = true;
      }
    }
    if (!hasCustomer) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This person is not a customer.");
    }

    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    return personRepository.findByPersonID(id);
  }

  @Transactional
  public void removeEmployeeRole(String id) {
    Person person = findPersonOrThrow(id);
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Employee employee) {
        orderRepository.unassignEmployee(employee);
        employeeRepository.deleteByRoleId(employee.getRoleID());
        return;
      }
    }
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST, "Person with ID " + id + " does not have an employee role.");
  }

  @Transactional
  public void deleteAccount(String id) {
    Person person = findPersonOrThrow(id);

    // Prevent manager from deleting their own account
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Manager) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "The manager cannot delete their own account.");
      }
    }

    personRepository.delete(person);
  }

  @Transactional(readOnly = true)
  public Person getPersonByEmail(String email) {
    Person person = personRepository.findByEmail(email);
    if (person == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "There is no person with email " + email + ".");
    }
    return person;
  }
}
