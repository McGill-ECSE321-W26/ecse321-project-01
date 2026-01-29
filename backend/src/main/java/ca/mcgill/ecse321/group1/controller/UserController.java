package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/users")
@RestController
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("santiago.p", "santiago@mail.mcgill.ca","password"));
        users.add(new User("ethan", "ethan@mail.mcgill.ca","1234567"));
        return users;
    }

}
