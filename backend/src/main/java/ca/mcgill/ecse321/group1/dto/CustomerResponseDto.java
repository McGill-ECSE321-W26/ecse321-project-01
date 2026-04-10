package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.PersonRole;

public class CustomerResponseDto {
  private final String role = "Customer";
  private String id;
  private String personId;
  private String email;
  private String address;
  private int loyaltyPoints;
  private boolean isEmployee;

  public CustomerResponseDto() {}

  public CustomerResponseDto(Customer customer) {
    this.id = customer.getRoleID();
    this.personId = customer.getPerson().getPersonID();
    this.email = customer.getPerson().getEmail();
    this.address = customer.getAddress();
    this.loyaltyPoints = customer.getLoyaltyPoints();
    for (PersonRole role : customer.getPerson().getRoles()) {
      if (role instanceof Employee) {
        this.isEmployee = true;
        break;
      }
    }
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

  public String getAddress() {
    return address;
  }

  public int getLoyaltyPoints() {
    return loyaltyPoints;
  }

  public boolean getIsEmployee() {
    return isEmployee;
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

  public void setAddress(String address) {
    this.address = address;
  }

  public void setLoyaltyPoints(int loyaltyPoints) {
    this.loyaltyPoints = loyaltyPoints;
  }
}
