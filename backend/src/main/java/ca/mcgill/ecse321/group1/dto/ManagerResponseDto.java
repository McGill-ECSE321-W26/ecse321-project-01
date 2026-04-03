package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Manager;

public class ManagerResponseDto {
  private final String role = "Manager";
  private String id;
  private String personId;
  private String email;

  public ManagerResponseDto() {}

  public ManagerResponseDto(Manager manager) {
    this.id = manager.getRoleID();
    this.personId = manager.getPerson().getPersonID();
    this.email = manager.getPerson().getEmail();
  }

  public String getRole() {
    return role;
  }

  public String getId() {
    return id;
  }

  public String getPersonId() {
    return personId;
  }

  public String getEmail() {
    return email;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
