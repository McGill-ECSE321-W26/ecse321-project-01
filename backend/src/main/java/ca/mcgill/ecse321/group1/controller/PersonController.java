package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.service.PersonService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/person")
@RestController
public class PersonController {

  private final PersonService personService;

  public PersonController(PersonService personService) {
    this.personService = personService;
  }

  @GetMapping
  public Iterable<Person> getPeople() {
    return personService.getPeople();
  }

  @GetMapping("{username}")
  public Person getPersonByUsername(@PathVariable String username) {
    return personService.getPersonById(username);
  }

  @PostMapping
  public PersonDto addPerson(@RequestBody PersonDto request) {
    Person p = personService.insertPerson(
            request.getId(),
            request.getEmail(),
            request.getPassword()
    );
    return new PersonDto(p); // never leaks password back
  }
}
