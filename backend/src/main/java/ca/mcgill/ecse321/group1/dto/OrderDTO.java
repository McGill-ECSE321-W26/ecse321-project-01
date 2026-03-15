package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.Order;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
  private String orderID;
  private String orderStatus;
  private Date orderDate;
  private Date deliveryDate;
  private float loyaltySaving;
  private String address;
  private String customerID;
  private String employeeID;
  private List<String> itemIDs;

  @SuppressWarnings("unused")
  private OrderDTO() {}

  public OrderDTO(Order model) {
    this.orderID = model.getOrderID();
    this.orderStatus = model.getOrderStatus() != null ? model.getOrderStatus().toString() : null;
    this.orderDate = model.getOrderDate();
    this.deliveryDate = model.getDeliveryDate();
    this.loyaltySaving = model.getLoyaltySaving();
    this.address = model.getAddress();
    this.customerID = model.getCustomer() != null ? model.getCustomer().getRoleID() : null;
    this.employeeID = model.getEmployee() != null ? model.getEmployee().getRoleID() : null;
    this.itemIDs = new ArrayList<>();
    for (Item item : model.getItems()) {
      this.itemIDs.add(item.getItemID());
    }
  }

  public String getOrderID() {
    return orderID;
  }

  public void setOrderID(String orderID) {
    this.orderID = orderID;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public Date getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public float getLoyaltySaving() {
    return loyaltySaving;
  }

  public void setLoyaltySaving(float loyaltySaving) {
    this.loyaltySaving = loyaltySaving;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCustomerID() {
    return customerID;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public String getEmployeeID() {
    return employeeID;
  }

  public void setEmployeeID(String employeeID) {
    this.employeeID = employeeID;
  }

  public List<String> getItemIDs() {
    return itemIDs;
  }
}
