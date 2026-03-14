package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.service.PersonService;
import org.springframework.web.bind.annotation.*;
import ca.mcgill.ecse321.group1.dto.PersonRequestDto;
import ca.mcgill.ecse321.group1.dto.PersonResponseDto;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/person")
@RestController
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping
  public Iterable<PersonResponseDto> getPeople() {
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

  @PostMapping
  public PersonResponseDto addPerson(@RequestBody PersonRequestDto request) {
    Person p = personService.insertPerson(
            request.getId(),
            request.getEmail(),
            request.getPassword()
    );
    return new PersonResponseDto(p);
  }

  @PostMapping("/login")
  public PersonResponseDto logIn(@RequestBody PersonRequestDto request) {
    Person p = personService.logIn(
            request.getEmail(),
            request.getPassword(),
            request.getRole()
    );
    return new PersonResponseDto(p);
  }
}
