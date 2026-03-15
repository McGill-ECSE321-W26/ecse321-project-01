package ca.mcgill.ecse321.group1.dto;

import java.sql.Date;

public class CreateOrderRequestDto {
  private String customerID;
  private Date deliveryDate;
  private int usedLoyaltyPoints;

  public String getCustomerID() {
    return customerID;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public int getUsedLoyaltyPoints() {
    return usedLoyaltyPoints;
  }

  public void setUsedLoyaltyPoints(int usedLoyaltyPoints) {
    this.usedLoyaltyPoints = usedLoyaltyPoints;
  }
}
