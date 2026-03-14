package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.repository.PersonRepository;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import ca.mcgill.ecse321.group1.exception.NotFoundException;
import ca.mcgill.ecse321.group1.exception.InvalidInputException;
import jakarta.transaction.Transactional;
import ca.mcgill.ecse321.group1.model.PersonRole;
import ca.mcgill.ecse321.group1.model.Manager;

@Service
public class PersonService {

  private final PersonRepository personRepository;
  private final CustomerRepository customerRepository;
  private final EmployeeRepository employeeRepository;

  public PersonService(PersonRepository personRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
    this.personRepository = personRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
  }

  // Should map to Transfer Objects
  public Iterable<Person> getPeople() {
    return personRepository.findAll();
  }

  public Person getPersonById(String id) {

    Person person = personRepository.findPersonByPersonID(id);
    if(person == null){
      throw new NotFoundException("There is no person with ID " + id + ".");
    }
    return person;
  }

  @Transactional
  public Person createCustomer(String id, String email, String password, String address) {
    if (id == null || id.isBlank()) {
      throw new InvalidInputException("Account id cannot be empty.");
    }
    if (email == null || !email.contains("@")) {
      throw new InvalidInputException("Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new InvalidInputException("Password must be at least 8 characters.");
    }
    if (personRepository.findPersonByPersonID(id) != null) {
      throw new InvalidInputException("Account id already exists.");
    }
    if (personRepository.findPersonByEmail(email) != null) {
      throw new InvalidInputException("Email " + email + " is already in use.");
    }
    if (address == null || address.isBlank()) {
      throw new InvalidInputException("Address cannot be empty.");
    }

    Person person = new Person(id, email, password);
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
      throw new InvalidInputException("Account id cannot be empty.");
    }
    if (email == null || !email.contains("@")) {
      throw new InvalidInputException("Invalid email address.");
    }
    if (password == null || password.length() < 8) {
      throw new InvalidInputException("Password must be at least 8 characters.");
    }
    if (personRepository.findPersonByPersonID(id) != null) {
      throw new InvalidInputException("Account id already exists.");
    }
    if (personRepository.findPersonByEmail(email) != null) {
      throw new InvalidInputException("Email " + email + " is already in use.");
    }

    Person person = new Person(id, email, password);
    person = personRepository.save(person);

    Employee employee = new Employee();
    employee.setPerson(person);
    employeeRepository.save(employee);

    return personRepository.findPersonByPersonID(id);
  }



  public Person logIn(String email, String password, String role) {
    Person person = personRepository.findPersonByEmail(email);
    if (person == null) {
      throw new NotFoundException("There is no person with email " + email + ".");
    }
    if (!person.getPassword().equals(password)) {
      throw new InvalidInputException("Wrong password.");
    }
    boolean hasRole = person.getRoles()
            .stream()
            .anyMatch(r -> r.getClass().getSimpleName().equals(role));
    if (!hasRole) {
      throw new InvalidInputException("Person does not have role " + role + ".");
    }
    return person;
  }

  @Transactional
  public Person updatePassword(String id, String oldPassword, String newPassword) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new NotFoundException("There is no person with ID " + id + ".");
    }
    if (!person.getPassword().equals(oldPassword)) {
      throw new InvalidInputException("Old password is incorrect.");
    }
    if (newPassword == null || newPassword.length() < 8) {
      throw new InvalidInputException("New password must be at least 8 characters.");
    }

    person.setPassword(newPassword);
    return personRepository.save(person);
  }

  @Transactional
  public Customer updateCustomerAddress(String customerRoleId, String newAddress) {
    Customer customer = customerRepository.findByRoleID(customerRoleId);
    if (customer == null) {
      throw new NotFoundException("There is no customer with role ID " + customerRoleId + ".");
    }
    if (newAddress == null || newAddress.isBlank()) {
      throw new InvalidInputException("Address cannot be empty.");
    }

    customer.setAddress(newAddress);
    return customerRepository.save(customer);
  }

  @Transactional
  public Person addCustomerRoleToEmployee(String id, String address) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new NotFoundException("There is no person with ID " + id + ".");
    }
    if (address == null || address.isBlank()) {
      throw new InvalidInputException("Address cannot be empty.");
    }

    // Check that this person actually has an employee role
    boolean hasEmployee = false;
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Customer) {
        throw new InvalidInputException("This person already has a customer role.");
      }
      if (role instanceof Employee) {
        hasEmployee = true;
      }
    }
    if (!hasEmployee) {
      throw new InvalidInputException("This person is not an employee.");
    }

    Customer customer = new Customer();
    customer.setAddress(address);
    customer.setLoyaltyPoints(0);
    customer.setPerson(person);
    customerRepository.save(customer);

    return personRepository.findPersonByPersonID(id);
  }

  @Transactional
  public void deleteSelfAccount(String id) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new NotFoundException("There is no person with ID " + id + ".");
    }
    personRepository.delete(person);
  }

  @Transactional
  public void deleteAccount(String id) {
    Person person = personRepository.findPersonByPersonID(id);
    if (person == null) {
      throw new NotFoundException("There is no person with ID " + id + ".");
    }

    // Prevent manager from deleting their own account
    for (PersonRole role : person.getRoles()) {
      if (role instanceof Manager) {
        throw new InvalidInputException("The manager cannot delete their own account.");
      }
    }

    personRepository.delete(person);
  }

}

