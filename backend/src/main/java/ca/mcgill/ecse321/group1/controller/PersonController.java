package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.AddressDto;
import ca.mcgill.ecse321.group1.dto.AuthResponseDto;
import ca.mcgill.ecse321.group1.dto.CustomerCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.CustomerResponseDto;
import ca.mcgill.ecse321.group1.dto.EmployeeCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.EmployeeResponseDto;
import ca.mcgill.ecse321.group1.dto.LoginRequestDto;
import ca.mcgill.ecse321.group1.dto.ManagerResponseDto;
import ca.mcgill.ecse321.group1.dto.PersonPasswordUpdateRequestDto;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.security.JwtUtil;
import ca.mcgill.ecse321.group1.service.PersonService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/persons")
@RestController
public class PersonController {

  private final PersonService personService;
  // JwtUtil is injected to generate tokens when a user logs in
  private final JwtUtil jwtUtil;

  public PersonController(PersonService personService, JwtUtil jwtUtil) {
    this.personService = personService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/customers")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponseDto createCustomer(@RequestBody CustomerCreateRequestDto dto) {
    Customer c = personService.createCustomer(dto.getEmail(), dto.getPassword(), dto.getAddress());
    return new CustomerResponseDto(c);
  }

  @PostMapping("/employees")
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeResponseDto createEmployee(@RequestBody EmployeeCreateRequestDto dto) {
    Employee e = personService.createEmployee(dto.getEmail(), dto.getPassword());
    return new EmployeeResponseDto(e);
  }

  @PostMapping("/{id}/roles/customer")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponseDto addCustomerRoleToEmployee(
      @PathVariable String id, @RequestBody AddressDto dto) {
    Person p = personService.addCustomerRoleToEmployee(id, dto.getAddress());
    Customer c =
        p.getRoles().stream()
            .filter(r -> r instanceof Customer)
            .map(r -> (Customer) r)
            .findFirst()
            .orElseThrow();
    return new CustomerResponseDto(c);
  }

  @PostMapping("/{id}/roles/employee")
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeResponseDto addEmployeeRoleToCustomer(@PathVariable String id) {
    Person p = personService.addEmployeeRoleToCustomer(id);
    Employee e =
        p.getRoles().stream()
            .filter(r -> r instanceof Employee)
            .map(r -> (Employee) r)
            .findFirst()
            .orElseThrow();
    return new EmployeeResponseDto(e);
  }

  // Login endpoint validates credentials via PersonService, then generates a JWT token
  // containing the person's ID and the requested role. Returns both the token (for the client
  // to store and send on future requests) and the role-specific person data
  @PostMapping("/sessions")
  public AuthResponseDto<?> logIn(@RequestBody LoginRequestDto dto) {
    Person p = personService.logIn(dto.getEmail(), dto.getPassword(), dto.getRole());
    String token = jwtUtil.generateToken(p, dto.getRole());
    return switch (dto.getRole()) {
      case "Customer" -> {
        Customer c =
            p.getRoles().stream()
                .filter(r -> r instanceof Customer)
                .map(r -> (Customer) r)
                .findFirst()
                .orElseThrow();
        yield new AuthResponseDto<>(token, new CustomerResponseDto(c));
      }
      case "Employee" -> {
        Employee e =
            p.getRoles().stream()
                .filter(r -> r instanceof Employee)
                .map(r -> (Employee) r)
                .findFirst()
                .orElseThrow();
        yield new AuthResponseDto<>(token, new EmployeeResponseDto(e));
      }
      case "Manager" -> {
        Manager m =
            p.getRoles().stream()
                .filter(r -> r instanceof Manager)
                .map(r -> (Manager) r)
                .findFirst()
                .orElseThrow();
        yield new AuthResponseDto<>(token, new ManagerResponseDto(m));
      }
      default -> throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Unknown role: " + dto.getRole());
    };
  }

  @GetMapping("/employees")
  public List<EmployeeResponseDto> getEmployees() {
    return personService.getEmployees().stream().map(EmployeeResponseDto::new).toList();
  }

  @GetMapping("/customers")
  public List<CustomerResponseDto> getCustomers() {
    return personService.getCustomers().stream().map(CustomerResponseDto::new).toList();
  }

  @PatchMapping("/{id}/password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updatePassword(
      @PathVariable String id, @RequestBody PersonPasswordUpdateRequestDto dto) {
    personService.updatePassword(id, dto.getOldPassword(), dto.getNewPassword());
  }

  @PatchMapping("/{id}/address")
  public CustomerResponseDto updateCustomerAddress(
      @PathVariable String id, @RequestBody AddressDto dto) {
    Customer c = personService.updateCustomerAddress(id, dto.getAddress());
    return new CustomerResponseDto(c);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(@PathVariable String id) {
    personService.deleteAccount(id);
  }
}
