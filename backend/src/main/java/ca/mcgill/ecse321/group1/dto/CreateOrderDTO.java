package ca.mcgill.ecse321.group1.dto;

public class CreateOrderDTO {
  private String customerID;
  private String deliveryDate;
  private int usedLoyaltyPoints;

  public String getCustomerID() {
    return customerID;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public String getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(String deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public int getUsedLoyaltyPoints() {
    return usedLoyaltyPoints;
  }

  public void setUsedLoyaltyPoints(int usedLoyaltyPoints) {
    this.usedLoyaltyPoints = usedLoyaltyPoints;
  }
}
