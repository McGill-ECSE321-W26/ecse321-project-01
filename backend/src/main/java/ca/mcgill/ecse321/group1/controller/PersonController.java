package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.AddressDto;
import ca.mcgill.ecse321.group1.dto.AuthResponseDto;
import ca.mcgill.ecse321.group1.dto.CreateCustomerDto;
import ca.mcgill.ecse321.group1.dto.CreateEmployeeDto;
import ca.mcgill.ecse321.group1.dto.LoginDto;
import ca.mcgill.ecse321.group1.dto.PersonResponseDto;
import ca.mcgill.ecse321.group1.dto.UpdatePasswordDto;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.security.JwtUtil;
import ca.mcgill.ecse321.group1.service.PersonService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
  public PersonResponseDto createCustomer(@RequestBody CreateCustomerDto dto) {
    Person p = personService.createCustomer(dto.getEmail(), dto.getPassword(), dto.getAddress());
    return new PersonResponseDto(p);
  }

  @PostMapping("/employees")
  @ResponseStatus(HttpStatus.CREATED)
  public PersonResponseDto createEmployee(@RequestBody CreateEmployeeDto dto) {
    Person p = personService.createEmployee(dto.getEmail(), dto.getPassword());
    return new PersonResponseDto(p);
  }

  @PostMapping("/{id}/roles/customer")
  @ResponseStatus(HttpStatus.CREATED)
  public PersonResponseDto addCustomerRoleToEmployee(
      @PathVariable String id, @RequestBody AddressDto dto) {
    Person p = personService.addCustomerRoleToEmployee(id, dto.getAddress());
    return new PersonResponseDto(p);
  }

  @PostMapping("/{id}/roles/employee")
  @ResponseStatus(HttpStatus.CREATED)
  public PersonResponseDto addEmployeeRoleToCustomer(@PathVariable String id) {
    Person p = personService.addEmployeeRoleToCustomer(id);
    return new PersonResponseDto(p);
  }

  // Login endpoint validates credentials via PersonService, then generates a JWT token
  // containing the person's ID and the requested role. Returns both the token (for the client
  // to store and send on future requests) and the person data
  @PostMapping("/sessions")
  public AuthResponseDto logIn(@RequestBody LoginDto dto) {
    Person p = personService.logIn(dto.getEmail(), dto.getPassword(), dto.getRole());
    String token = jwtUtil.generateToken(p, dto.getRole());
    return new AuthResponseDto(token, new PersonResponseDto(p));
  }

  @GetMapping
  public List<PersonResponseDto> getPeople(@RequestParam(required = false) String email) {
    if (email != null) {
      return List.of(new PersonResponseDto(personService.getPersonByEmail(email)));
    }
    List<PersonResponseDto> dtos = new ArrayList<>();
    for (Person p : personService.getPeople()) {
      dtos.add(new PersonResponseDto(p));
    }
    return dtos;
  }

  @GetMapping("/{id}")
  public PersonResponseDto getPersonById(@PathVariable String id) {
    return new PersonResponseDto(personService.getPersonById(id));
  }

  @PatchMapping("/{id}/password")
  public PersonResponseDto updatePassword(
      @PathVariable String id, @RequestBody UpdatePasswordDto dto) {
    Person p = personService.updatePassword(id, dto.getOldPassword(), dto.getNewPassword());
    return new PersonResponseDto(p);
  }

  @PatchMapping("/{id}/address")
  public PersonResponseDto updateCustomerAddress(
      @PathVariable String id, @RequestBody AddressDto dto) {
    Customer c = personService.updateCustomerAddress(id, dto.getAddress());
    return new PersonResponseDto(c.getPerson());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAccount(@PathVariable String id) {
    personService.deleteAccount(id);
  }
}
