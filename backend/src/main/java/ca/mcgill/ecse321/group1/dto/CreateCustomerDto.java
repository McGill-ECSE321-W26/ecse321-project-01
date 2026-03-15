package ca.mcgill.ecse321.group1.dto;

public class CreateCustomerDto {
  private String id;
  private String email;
  private String password;
  private String address;

  @SuppressWarnings("unused")
  private CreateCustomerDto() {}

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getAddress() {
    return address;
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

  public void setAddress(String address) {
    this.address = address;
  }
}
