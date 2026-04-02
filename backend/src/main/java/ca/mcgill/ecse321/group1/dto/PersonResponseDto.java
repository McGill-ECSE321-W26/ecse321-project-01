package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Manager;
import ca.mcgill.ecse321.group1.model.Person;
import ca.mcgill.ecse321.group1.model.PersonRole;
import java.util.ArrayList;
import java.util.List;

public class PersonResponseDto {
  private String id;
  private String email;
  private List<String> roleTypes;
  private String address;
  private Integer loyaltyPoints;
  private String employeeRoleId;
  private String customerRoleId;

  @SuppressWarnings("unused")
  private PersonResponseDto() {}

  public PersonResponseDto(Person model) {
    this.id = model.getPersonID();
    this.email = model.getEmail();
    this.roleTypes = new ArrayList<>();
    for (PersonRole role : model.getRoles()) {
      if (role instanceof Customer customer) {
        this.roleTypes.add("Customer");
        this.address = customer.getAddress();
        this.loyaltyPoints = customer.getLoyaltyPoints();
        this.customerRoleId = customer.getRoleID();
      } else if (role instanceof Manager) {
        this.roleTypes.add("Manager");
      } else if (role instanceof Employee employee) {
        this.roleTypes.add("Employee");
        this.employeeRoleId = employee.getRoleID();
      }
    }
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public List<String> getRoleTypes() {
    return roleTypes;
  }

  public String getAddress() {
    return address;
  }

  public Integer getLoyaltyPoints() {
    return loyaltyPoints;
  }

  public String getEmployeeRoleId() {
    return employeeRoleId;
  }

  public String getCustomerRoleId() {
    return customerRoleId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setRoleTypes(List<String> roleTypes) {
    this.roleTypes = roleTypes;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setLoyaltyPoints(Integer loyaltyPoints) {
    this.loyaltyPoints = loyaltyPoints;
  }

  public void setEmployeeRoleId(String employeeRoleId) {
    this.employeeRoleId = employeeRoleId;
  }

  public void setCustomerRoleId(String customerRoleId) {
    this.customerRoleId = customerRoleId;
  }
}
