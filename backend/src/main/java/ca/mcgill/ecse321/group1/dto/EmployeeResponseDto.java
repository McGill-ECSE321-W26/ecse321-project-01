package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Employee;

public class EmployeeResponseDto {
  private String id;
  private String email;
  private String personId;

  public EmployeeResponseDto() {}

  public EmployeeResponseDto(Employee employee) {
    this.id = employee.getRoleID();
    this.personId = employee.getPerson().getPersonID();
    this.email = employee.getPerson().getEmail();
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
