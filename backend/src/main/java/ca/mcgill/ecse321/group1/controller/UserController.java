package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/users")
@RestController
public class UserController {

    private final PersonService personService;

    public UserController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getPeople() {
        return personService.getPeople();
    }

    @GetMapping("{username}")
    public Person getPersonByUsername(@PathVariable String username) {
        return personService.getPersonById(username);
    }

    @PostMapping
    public void addPerson(@RequestBody Person person) {
        personService.insertPerson(person);
    }

}
