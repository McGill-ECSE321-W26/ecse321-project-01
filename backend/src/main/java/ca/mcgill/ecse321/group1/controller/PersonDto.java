package ca.mcgill.ecse321.group1.controller;


import ca.mcgill.ecse321.group1.model.Person;

public class PersonDto {
    private String id;
    private String email;
    private String password;

    // Jackson needs a default constructor, but it doesn't need to be public
    @SuppressWarnings("unused")
    private PersonDto() {
    }

    public PersonDto(Person model) {
        this.id = model.getPersonID();
        this.email = model.getEmail();
    }


    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}