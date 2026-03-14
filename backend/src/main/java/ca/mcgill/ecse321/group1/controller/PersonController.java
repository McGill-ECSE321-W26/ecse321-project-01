package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.AddressDto;
import ca.mcgill.ecse321.group1.dto.CreateCustomerDto;
import ca.mcgill.ecse321.group1.dto.CreateEmployeeDto;
import ca.mcgill.ecse321.group1.dto.LoginDto;
import ca.mcgill.ecse321.group1.dto.PersonResponseDto;
import ca.mcgill.ecse321.group1.dto.UpdatePasswordDto;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.service.PersonService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/person")
@RestController
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @PostMapping("customer")
  public PersonResponseDto createCustomer(@RequestBody CreateCustomerDto dto) {
    Person p =
        personService.createCustomer(
            dto.getId(), dto.getEmail(), dto.getPassword(), dto.getAddress());
    return new PersonResponseDto(p);
  }

  @PostMapping("employee")
  public PersonResponseDto createEmployee(@RequestBody CreateEmployeeDto dto) {
    Person p = personService.createEmployee(dto.getId(), dto.getEmail(), dto.getPassword());
    return new PersonResponseDto(p);
  }

  @PostMapping("{id}/add-customer-role")
  public PersonResponseDto addCustomerRoleToEmployee(
      @PathVariable String id, @RequestBody AddressDto dto) {
    Person p = personService.addCustomerRoleToEmployee(id, dto.getAddress());
    return new PersonResponseDto(p);
  }

  @PostMapping("{id}/add-employee-role")
  public PersonResponseDto addEmployeeRoleToCustomer(@PathVariable String id) {
    Person p = personService.addEmployeeRoleToCustomer(id);
    return new PersonResponseDto(p);
  }

  @PostMapping("login")
  public PersonResponseDto logIn(@RequestBody LoginDto dto) {
    Person p = personService.logIn(dto.getEmail(), dto.getPassword(), dto.getRole());
    return new PersonResponseDto(p);
  }

  @GetMapping("")
  public List<PersonResponseDto> getPeople() {
    List<PersonResponseDto> dtos = new ArrayList<>();
    for (Person p : personService.getPeople()) {
      dtos.add(new PersonResponseDto(p));
    }
    return dtos;
  }

  @GetMapping("{id}")
  public PersonResponseDto getPersonById(@PathVariable String id) {
    return new PersonResponseDto(personService.getPersonById(id));
  }

  @GetMapping("email/{email}")
  public PersonResponseDto getPersonByEmail(@PathVariable String email) {
    return new PersonResponseDto(personService.getPersonByEmail(email));
  }

  @PutMapping("{id}/password")
  public PersonResponseDto updatePassword(
      @PathVariable String id, @RequestBody UpdatePasswordDto dto) {
    Person p = personService.updatePassword(id, dto.getOldPassword(), dto.getNewPassword());
    return new PersonResponseDto(p);
  }

  @PutMapping("customer/{customerRoleId}/address")
  public PersonResponseDto updateCustomerAddress(
      @PathVariable String customerRoleId, @RequestBody AddressDto dto) {
    Customer c = personService.updateCustomerAddress(customerRoleId, dto.getAddress());
    return new PersonResponseDto(c.getPerson());
  }

  @DeleteMapping("{id}")
  public void deleteAccount(@PathVariable String id) {
    personService.deleteAccount(id);
  }

  @DeleteMapping("{id}/self")
  public void deleteSelfAccount(@PathVariable String id) {
    personService.deleteSelfAccount(id);
  }
}
