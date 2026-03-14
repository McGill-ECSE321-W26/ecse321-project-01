package ca.mcgill.ecse321.group1.dto;


import ca.mcgill.ecse321.group1.model.Person;

public class PersonResponseDto {
    private String id;
    private String email;

    // Jackson needs a default constructor, but it doesn't need to be public
    @SuppressWarnings("unused")
    private PersonResponseDto() {
    }

    public PersonResponseDto(Person model) {
        this.id = model.getPersonID();
        this.email = model.getEmail();
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

}