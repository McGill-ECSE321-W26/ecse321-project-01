package ca.mcgill.ecse321.group1.dto;

import java.sql.Date;

public class OrderRequestUpdateDto {
  private String employeeID;
  private Date deliveryDate;
  private String orderStatus;

  public String getEmployeeID() {
    return employeeID;
  }

  public void setEmployeeID(String employeeID) {
    this.employeeID = employeeID;
  }

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
}
