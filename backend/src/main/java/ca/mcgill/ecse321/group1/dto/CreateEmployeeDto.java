package ca.mcgill.ecse321.group1.dto;

public class CreateEmployeeDto {
  private String email;
  private String password;

  @SuppressWarnings("unused")
  private CreateEmployeeDto() {}

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
