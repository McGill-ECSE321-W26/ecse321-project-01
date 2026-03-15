package ca.mcgill.ecse321.group1.dto;

public class CreateEmployeeDto {
  private String id;
  private String email;
  private String password;

  @SuppressWarnings("unused")
  private CreateEmployeeDto() {}

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
