package ca.mcgill.ecse321.group1.dto;

public class EmployeeCreateRequestDto {
  private String email;
  private String password;
  private String address;

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getAddress() {
    return address;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
