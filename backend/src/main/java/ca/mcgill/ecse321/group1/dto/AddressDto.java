package ca.mcgill.ecse321.group1.dto;

public class AddressDto {
  private String address;

  @SuppressWarnings("unused")
  private AddressDto() {}

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
